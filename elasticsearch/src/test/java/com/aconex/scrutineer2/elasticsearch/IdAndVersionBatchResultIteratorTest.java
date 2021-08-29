package com.aconex.scrutineer2.elasticsearch;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.StringIdAndVersion;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class IdAndVersionBatchResultIteratorTest {
    @Test
    public void shouldIteratorWithoutScroll(){
        Iterator<IdAndVersion> firstBatchHits = new ArrayList<IdAndVersion>(){{
            add(new StringIdAndVersion("1",1));
            add(new StringIdAndVersion("2",2));
        }}.iterator();
        IdAndVersionBatchResultIterator iterator = new IdAndVersionBatchResultIterator(()-> null, firstBatchHits);
        assertTrue(iterator.hasNext());
        IdAndVersion idAndVersion = iterator.next();
        assertThat(idAndVersion.getId(),is("1"));
        assertThat(idAndVersion.getVersion(),is(1L));
        idAndVersion = iterator.next();
        assertThat(idAndVersion.getId(),is("2"));
        assertThat(idAndVersion.getVersion(),is(2L));
    }

    @Test
    public void hasNextShouldReturnFalseWhenNoDataInBatch(){
        Iterator<IdAndVersion> firstBatchHits = new ArrayList<IdAndVersion>(){}.iterator();
        IdAndVersionBatchResultIterator iterator = new IdAndVersionBatchResultIterator(()-> null, firstBatchHits);
        assertFalse(iterator.hasNext());

        firstBatchHits = null;
        iterator = new IdAndVersionBatchResultIterator(()-> null, firstBatchHits);
        assertFalse(iterator.hasNext());
    }
    @Test
    public void nextShouldThrowExceptionWhenNoDataInBatch(){
        Iterator<IdAndVersion> firstBatchHits = new ArrayList<IdAndVersion>(){}.iterator();
        IdAndVersionBatchResultIterator iterator = new IdAndVersionBatchResultIterator(()-> null, firstBatchHits);
        assertThrows("Should throw exception when call next on empty iterator", NoSuchElementException.class, iterator::next);

        firstBatchHits = null;
        iterator = new IdAndVersionBatchResultIterator(()-> null, firstBatchHits);
        assertThrows("Should throw exception when call next on empty iterator", NoSuchElementException.class, iterator::next);
    }

    @Test
    public void shouldIteratorWithScroll(){
        Iterator<IdAndVersion> firstBatchHits = new ArrayList<IdAndVersion>(){{
            add(new StringIdAndVersion("1",1));
            add(new StringIdAndVersion("2",2));
        }}.iterator();

        Iterator<IdAndVersion> nextBatchHits = new ArrayList<IdAndVersion>(){{
            add(new StringIdAndVersion("3",3));
            add(new StringIdAndVersion("4",4));
        }}.iterator();

        IdAndVersionBatchResultIterator iterator = new IdAndVersionBatchResultIterator(()-> nextBatchHits, firstBatchHits);
        assertTrue(iterator.hasNext());
        iterator.next();
        iterator.next();
        IdAndVersion idAndVersion = iterator.next();
        assertThat(idAndVersion.getId(),is("3"));
        assertThat(idAndVersion.getVersion(),is(3L));
        idAndVersion = iterator.next();
        assertThat(idAndVersion.getId(),is("4"));
        assertThat(idAndVersion.getVersion(),is(4L));

        assertFalse(iterator.hasNext());
        assertThrows("Call next should throw exception when no more data in iterator", NoSuchElementException.class, iterator::next);
    }
}