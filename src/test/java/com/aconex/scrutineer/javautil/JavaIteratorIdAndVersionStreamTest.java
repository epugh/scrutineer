package com.aconex.scrutineer.javautil;

import com.aconex.scrutineer.IdAndVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Iterator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JavaIteratorIdAndVersionStreamTest {

    private Iterator<IdAndVersion> iterator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPassThroughIterator() {
        JavaIteratorIdAndVersionStream javaIteratorIdAndVersionStream = new JavaIteratorIdAndVersionStream(iterator);
        assertThat(javaIteratorIdAndVersionStream.iterator(), is(iterator));
    }
}
