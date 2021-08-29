package com.aconex.scrutineer2;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;

public class IdAndVersionStreamVerifier {

    private static final Logger LOG = LogUtils.loggerForThisClass();

    //CHECKSTYLE:OFF
    @SuppressWarnings("PMD.NcssMethodCount")
	public void verify(IdAndVersionStreamConnector primaryStreamConnector, IdAndVersionStreamConnector secondaryStreamConnector, IdAndVersionStreamVerifierListener idAndVersionStreamVerifierListener) {
        idAndVersionStreamVerifierListener.onVerificationStarted();

        long numItems = 0;
        long begin = System.currentTimeMillis();

        try {
            parallelOpenStreamsAndWait(primaryStreamConnector, secondaryStreamConnector);

            Iterator<IdAndVersion> primaryIterator = primaryStreamConnector.stream().iterator();
            Iterator<IdAndVersion> secondaryIterator = secondaryStreamConnector.stream().iterator();

            IdAndVersion primaryItem = next(primaryIterator);
            IdAndVersion secondaryItem = next(secondaryIterator);

            while (primaryItem != null && secondaryItem != null) {
                idAndVersionStreamVerifierListener.onStreamComparison(primaryItem, secondaryItem);

                if (primaryItem.equals(secondaryItem)) {
                    primaryItem = verifiedNext(primaryIterator, primaryItem);
                    secondaryItem = next(secondaryIterator);
                } else if (primaryItem.getId().equals(secondaryItem.getId())) {
                    idAndVersionStreamVerifierListener.onVersionMisMatch(primaryItem, secondaryItem);
                    primaryItem = verifiedNext(primaryIterator, primaryItem);
                    secondaryItem = next(secondaryIterator);
                } else if (primaryItem.compareTo(secondaryItem) < 0) {
                    idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryItem);
                    primaryItem = verifiedNext(primaryIterator, primaryItem);
                } else {
                    idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryItem);
                    secondaryItem = next(secondaryIterator);
                }
                numItems++;
            }

            while (primaryItem != null) {
                idAndVersionStreamVerifierListener.onMissingInSecondaryStream(primaryItem);
                idAndVersionStreamVerifierListener.onStreamComparison(primaryItem, secondaryItem);

                primaryItem = verifiedNext(primaryIterator, primaryItem);
                numItems++;
            }

            while (secondaryItem != null) {
                idAndVersionStreamVerifierListener.onMissingInPrimaryStream(secondaryItem);
                idAndVersionStreamVerifierListener.onStreamComparison(primaryItem, secondaryItem);
                secondaryItem = next(secondaryIterator);
                numItems++;
            }

            idAndVersionStreamVerifierListener.onVerificationCompleted();
        } finally {
            closeQuietly(primaryStreamConnector);
            closeQuietly(secondaryStreamConnector);
        }
        LogUtils.infoTimeTaken(LOG, begin, numItems, "Completed verification");
    }
    //CHECKSTYLE:ON

    @SuppressWarnings("PMD.NcssMethodCount")
	private void parallelOpenStreamsAndWait(IdAndVersionStreamConnector primaryStreamConnector, IdAndVersionStreamConnector secondaryStreamConnector) {
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(1, new NamedDaemonThreadFactory("StreamOpener"));
			Future<?> secondaryStreamFuture = executorService.submit(new OpenStreamRunner(secondaryStreamConnector));

			primaryStreamConnector.open();
			secondaryStreamFuture.get();

			executorService.shutdown();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to open one or both of the streams in parallel", e);
		}
	}

	private IdAndVersion verifiedNext(Iterator<IdAndVersion> iterator, IdAndVersion previous) {
		IdAndVersion next = next(iterator);
		if (next != null && previous.compareTo(next) >= 0) {
			throw new IllegalStateException("primary stream not ordered as expected: " + next + " followed "
					+ previous);
		} else {
			return next;
		}
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	private IdAndVersion next(Iterator<IdAndVersion> iterator) {
		if (iterator.hasNext()) {
			IdAndVersion next = iterator.next();
			if (next == null) {
				throw new IllegalStateException("stream must not return null");
			} else {
				return next;
			}
		} else {
			return null;
		}
	}

    private void closeQuietly(IdAndVersionStreamConnector connector) {
        try {
            connector.close();
        } catch (Exception e) {
            LogUtils.warn(LOG, "Unable to close IdAndVersionStreamConnector", e);
        }
    }

    private static class OpenStreamRunner implements Runnable {
        private final IdAndVersionStreamConnector streamConnector;

        OpenStreamRunner(IdAndVersionStreamConnector streamConnector) {
            this.streamConnector = streamConnector;
        }

        @Override
        public void run() {
            streamConnector.open();
        }
    }

    private static class NamedDaemonThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadCount = new AtomicInteger();

        NamedDaemonThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable command) {
            Thread thread = new Thread(command, namePrefix + "-" + threadCount.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
