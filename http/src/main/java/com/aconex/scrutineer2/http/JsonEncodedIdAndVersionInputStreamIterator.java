package com.aconex.scrutineer2.http;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.IdAndVersionFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class JsonEncodedIdAndVersionInputStreamIterator implements Iterator<IdAndVersion>, Closeable {
    static class IdAndVersionRequestModel{
        private String id;
        private long version;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }
    }
    private final InputStream inputStream;
    private final IdAndVersionFactory idAndVersionFactory;
    private JsonParser jsonParser;
    private boolean isInitialized;

    private IdAndVersion nextObject;

    public JsonEncodedIdAndVersionInputStreamIterator(final InputStream inputStream, IdAndVersionFactory idAndVersionFactory) {
        this.inputStream = inputStream;
        this.idAndVersionFactory = idAndVersionFactory;
        this.isInitialized = false;
        this.nextObject = null;
    }

    private void init() {
        this.initJsonParser();
        this.initFirstElement();
        this.isInitialized = true;
    }

    private void initJsonParser() {
        try {
            this.jsonParser = new ObjectMapper().getFactory().createParser(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error when setting up the JsonParser: " + e.getMessage(), e);
        }
    }

    private void initFirstElement() {
        JsonToken arrayStartToken = assertFirstTokenIsStartArray();
        if (arrayStartToken == null) {
            this.nextObject = null;
            return;
        }
        this.readNextObject();
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    private JsonToken assertFirstTokenIsStartArray() {
        JsonToken jsonToken;
        try {
            jsonToken = this.jsonParser.nextToken();
        } catch (IOException e) {
            throw new InvalidSourceContentException("Failed to read first token in json", e);
        }
        if (isNotStartArrayToken(jsonToken)) {
            throw new InvalidSourceContentException("The first element of the json should start with an array token, but it was: " + jsonToken);
        }
        return jsonToken;
    }

    private boolean isNotStartArrayToken(JsonToken arrayStartToken) {
        return arrayStartToken != null && arrayStartToken != JsonToken.START_ARRAY;
    }

    private void readNextObject() {
        JsonToken nextToken = assertNextTokenIsStartObject();
        if (nextToken == JsonToken.END_ARRAY) {
            this.nextObject = null;
            return;
        }
        parseNextObject();
    }

    private void parseNextObject() {
        try {
            IdAndVersionRequestModel idAndVersionRequestModel = this.jsonParser.readValueAs(IdAndVersionRequestModel.class);
            this.nextObject = idAndVersionFactory.create(idAndVersionRequestModel.getId(), idAndVersionRequestModel.getVersion()) ;
        } catch (IOException e) {
            throw new InvalidSourceContentException("Failed to read array item in the json", e);
        }
    }

    @SuppressWarnings("PMD.NcssMethodCount")
    private JsonToken assertNextTokenIsStartObject() {
        JsonToken nextToken;
        try {
            nextToken = this.jsonParser.nextToken();
        } catch (IOException e) {
            throw new InvalidSourceContentException("Failed to read first token in json", e);
        }
        if (isNotStartObjectToken(nextToken)) {
            throw new IllegalStateException("The next token of Json structure was expected to be a start object token, but it was: " + nextToken);
        }
        return nextToken;
    }

    private boolean isNotStartObjectToken(JsonToken nextToken) {
        return nextToken != JsonToken.END_ARRAY && nextToken != JsonToken.START_OBJECT;
    }

    @Override
    public boolean hasNext() {
        if (!this.isInitialized) {
            this.init();
        }
        return this.nextObject != null;
    }

    @Override
    public IdAndVersion next() {
        if (!this.isInitialized) {
            this.init();
        }
        IdAndVersion currentNextObject = this.nextObject;
        this.readNextObject();
        return currentNextObject;
    }

    @Override
    public void close() {
        closeQuitely(this.jsonParser);
        closeQuitely(this.inputStream);
    }

    private void closeQuitely(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}