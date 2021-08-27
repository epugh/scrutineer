package com.aconex.scrutineer2.http;

import com.aconex.scrutineer2.IdAndVersion;
import com.aconex.scrutineer2.StringIdAndVersionFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class JsonEncodedIdAndVersionInputStreamIteratorTest {
    @Test
    public void shouldParseJsonStream(){
        String jsonString="[" +
                "    {" +
                "        \"id\": \"foo\"," +
                "        \"version\": 1" +
                "    }," +
                "    {" +
                "        \"id\": \"bar\"," +
                "        \"version\": 2" +
                "    }" +
                "]";
        JsonEncodedIdAndVersionInputStreamIterator jsonEncodedIdAndVersionInputStreamIterator = new JsonEncodedIdAndVersionInputStreamIterator(new ByteArrayInputStream(jsonString.getBytes()), StringIdAndVersionFactory.INSTANCE);
        assertTrue(jsonEncodedIdAndVersionInputStreamIterator.hasNext());
        IdAndVersion firstRecord = jsonEncodedIdAndVersionInputStreamIterator.next();
        assertEquals("foo", firstRecord.getId());
        assertEquals(1, firstRecord.getVersion());
        IdAndVersion secondRecord = jsonEncodedIdAndVersionInputStreamIterator.next();
        assertEquals("bar", secondRecord.getId());
        assertEquals(2, secondRecord.getVersion());
    }

    @Test
    public void shouldParseEmptyStream(){
        String jsonString="";
        JsonEncodedIdAndVersionInputStreamIterator jsonEncodedIdAndVersionInputStreamIterator = new JsonEncodedIdAndVersionInputStreamIterator(new ByteArrayInputStream(jsonString.getBytes()),StringIdAndVersionFactory.INSTANCE);
        assertFalse(jsonEncodedIdAndVersionInputStreamIterator.hasNext());
    }

    @Test
    public void shouldThrowExceptionWhenNotArrayInJson(){
        String jsonString="{" +
                "        \"id\": \"foo\"," +
                "        \"version\": 1" +
                "    }";
        JsonEncodedIdAndVersionInputStreamIterator jsonEncodedIdAndVersionInputStreamIterator = new JsonEncodedIdAndVersionInputStreamIterator(new ByteArrayInputStream(jsonString.getBytes()), StringIdAndVersionFactory.INSTANCE);
        InvalidSourceContentException e = assertThrows(InvalidSourceContentException.class, jsonEncodedIdAndVersionInputStreamIterator::hasNext);
        assertEquals("The first element of the json should start with an array token, but it was: START_OBJECT", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenFieldNotMatchWithClass(){
        String jsonString="[{" +
                "        \"newId\": \"foo\"," +
                "        \"version\": 1" +
                "    }]";
        JsonEncodedIdAndVersionInputStreamIterator jsonEncodedIdAndVersionInputStreamIterator = new JsonEncodedIdAndVersionInputStreamIterator(new ByteArrayInputStream(jsonString.getBytes()), StringIdAndVersionFactory.INSTANCE);
        InvalidSourceContentException e = assertThrows(InvalidSourceContentException.class, jsonEncodedIdAndVersionInputStreamIterator::hasNext);
        assertEquals("Failed to read array item in the json", e.getMessage());
    }
}