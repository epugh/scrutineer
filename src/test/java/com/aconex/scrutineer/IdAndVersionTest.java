package com.aconex.scrutineer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


public class IdAndVersionTest {

    @Test
    public void shouldBeEqualWhenIdAndVersionAreTheSame() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("2",3);
        assertThat(idAndVersion1, is(idAndVersion2));
        assertThat(idAndVersion1.hashCode(), is(idAndVersion2.hashCode()));
    }

    @Test
    public void shouldNotBeEqualWhenIdsDiffer() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("3",3);
        assertThat(idAndVersion1, is(not(idAndVersion2)));
        assertThat(idAndVersion1.hashCode(), is(not(idAndVersion2.hashCode())));
    }

    @Test
    public void shouldNotBeEqualWhenVersionsDiffer() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",2);
        IdAndVersion idAndVersion2 = new IdAndVersion("2",15);
        assertThat(idAndVersion1, is(not(idAndVersion2)));
        assertThat(idAndVersion1.hashCode(), is(not(idAndVersion2.hashCode())));
    }

    @Test
    public void shouldGet0WhenComapringEqualObjects() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("2",3);
        assertThat(idAndVersion1.compareTo(idAndVersion2), is(0));
    }

    @Test
    public void shouldGetn1WhenComapringLesserIds() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("3",3);
        assertThat(idAndVersion1.compareTo(idAndVersion2), is(-1));
    }

    @Test
    public void shouldGetn1WhenComapringLesserVersions() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("2",4);
        assertThat(idAndVersion1.compareTo(idAndVersion2), is(-1));
    }

    @Test
    public void shouldGet1WhenComapringGreaterIds() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("1",3);
        assertThat(idAndVersion1.compareTo(idAndVersion2), is(1));
    }

    @Test
    public void shouldGet1WhenComapringGreaterVersions() {
        IdAndVersion idAndVersion1 = new IdAndVersion("2",3);
        IdAndVersion idAndVersion2 = new IdAndVersion("2",2);
        assertThat(idAndVersion1.compareTo(idAndVersion2), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEWhenComparedToNull() {
        IdAndVersion idAndVersion = new IdAndVersion("2",3);
        idAndVersion.compareTo(null);
    }

    @Test
    public void shouldPrintTheIdAndVersionInToString() {
        IdAndVersion idAndVersion = new IdAndVersion("2",3);
        assertThat(idAndVersion.toString(), is("2:3"));
    }
}
