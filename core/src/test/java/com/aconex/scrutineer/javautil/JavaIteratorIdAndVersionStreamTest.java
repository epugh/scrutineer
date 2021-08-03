package com.aconex.scrutineer.javautil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;

import com.aconex.scrutineer.IdAndVersion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

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
