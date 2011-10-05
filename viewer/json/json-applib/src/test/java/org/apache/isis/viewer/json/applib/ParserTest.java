package org.apache.isis.viewer.json.applib;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class ParserTest {

    @Test
    public void forBoolean() {
        Parser<Boolean> parser = Parser.forBoolean();
        for (Boolean v : new Boolean[] {Boolean.TRUE, Boolean.FALSE}) {
            final String asString = parser.asString(v);
            final Boolean valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
        
        final Boolean valueOf = parser.valueOf(Arrays.asList(parser.asString(Boolean.TRUE), parser.asString(Boolean.FALSE)));
        assertThat(valueOf, is(Boolean.TRUE));
    }

    @Test
    public void forString() {
        Parser<String> parser = Parser.forString();
        
        for (String v : new String[] {"", "foo", "foz"}) {
            final String asString = parser.asString(v);
            final String valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
    }

    @Test
    public void forListOfStrings() {
        Parser<List<String>> parser = Parser.forListOfStrings();
        
        List<String> v = Arrays.asList("", "foo", "foz" );
        
        final String asString = parser.asString(v);
        final List<String> valueOf = parser.valueOf(asString);
        
        assertThat(v, sameContentsAs(valueOf));
    }

    @Test
    public void forListOfListOfStringsDottedNotation() {
        Parser<List<List<String>>> parser = Parser.forListOfListOfStrings();
        
        final List<List<String>> valueOf = parser.valueOf(Arrays.asList("a", "b.c", "d.e.f" ));
        
        assertThat(valueOf.size(), is(3));
        assertThat(valueOf.get(0).size(), is(1));
        assertThat(valueOf.get(0).get(0), is("a"));
        assertThat(valueOf.get(1).size(), is(2));
        assertThat(valueOf.get(1).get(0), is("b"));
        assertThat(valueOf.get(1).get(1), is("c"));
        assertThat(valueOf.get(2).size(), is(3));
        assertThat(valueOf.get(2).get(0), is("d"));
        assertThat(valueOf.get(2).get(1), is("e"));
        assertThat(valueOf.get(2).get(2), is("f"));
        
        assertThat(parser.asString(valueOf), is("a,b.c,d.e.f"));
    }

    @Test
    public void forListOfListOfStringsCommaSeparated() {
        Parser<List<List<String>>> parser = Parser.forListOfListOfStrings();
        
        final List<List<String>> valueOf = parser.valueOf("a,b.c,d.e.f");
        
        assertThat(valueOf.size(), is(3));
        assertThat(valueOf.get(0).size(), is(1));
        assertThat(valueOf.get(0).get(0), is("a"));
        assertThat(valueOf.get(1).size(), is(2));
        assertThat(valueOf.get(1).get(0), is("b"));
        assertThat(valueOf.get(1).get(1), is("c"));
        assertThat(valueOf.get(2).size(), is(3));
        assertThat(valueOf.get(2).get(0), is("d"));
        assertThat(valueOf.get(2).get(1), is("e"));
        assertThat(valueOf.get(2).get(2), is("f"));
    }

    @Test
    public void forMediaTypes() {
        Parser<MediaType> parser = Parser.forMediaType();
        MediaType mediaType = RepresentationType.DOMAIN_OBJECT.getMediaType();
        final String asString = parser.asString(mediaType);
        final MediaType valueOf = parser.valueOf(asString);
        
        assertThat(valueOf, is(mediaType));
    }


    @Test
    public void forListOfMediaTypes() {
        Parser<List<MediaType>> parser = Parser.forListOfMediaTypes();
        MediaType mediaType = RepresentationType.DOMAIN_OBJECT.getMediaType();
        List<MediaType> v = Arrays.asList(mediaType, MediaType.APPLICATION_JSON_TYPE);
        final String asString = parser.asString(v);
        final List<MediaType> valueOf = parser.valueOf(asString);
        
        assertThat(v, sameContentsAs(valueOf));
    }

    @Test
    public void forDate() {
        Parser<Date> parser = Parser.forDate();
        
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MILLISECOND, 0);
        Date nowToNearestSecond = cal.getTime();
        for (Date v : new Date[] {nowToNearestSecond}) {
            final String asString = parser.asString(v);
            final Date valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
    }

    @Test
    public void forInteger() {
        Parser<Integer> parser = Parser.forInteger();
        
        for (Integer v : new Integer[] {1, 2, 3, -5, -100, 0, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            final String asString = parser.asString(v);
            final Integer valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
    }

    @Test
    public void forMediaType() {
        Parser<MediaType> parser = Parser.forMediaType();
        
        for (MediaType v : new MediaType[] { 
                MediaType.APPLICATION_ATOM_XML_TYPE, 
                MediaType.APPLICATION_JSON_TYPE, 
                MediaType.APPLICATION_XHTML_XML_TYPE, 
                MediaType.valueOf(RestfulMediaType.APPLICATION_JSON_DOMAIN_OBJECT) }) {
            final String asString = parser.asString(v);
            final MediaType valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
    }


    @Test
    public void forCacheControl() {
        Parser<CacheControl> parser = Parser.forCacheControl();
        
        final CacheControl cc1 = createCacheControl();
        cc1.setMaxAge(2000);
        final CacheControl cc2 = createCacheControl();
        cc2.setNoCache(true);
        for (CacheControl v : new CacheControl[] { cc1, cc2 }) {
            final String asString = parser.asString(v);
            final CacheControl valueOf = parser.valueOf(asString);
            assertThat(v, is(equalTo(valueOf)));
        }
    }

    private static CacheControl createCacheControl() {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.getCacheExtension(); // workaround for bug in CacheControl's equals() method
        cacheControl.getNoCacheFields(); // workaround for bug in CacheControl's equals() method
        return cacheControl;
    }

    public static <T> Matcher<List<T>> sameContentsAs(final List<T> expected) {
        return new TypeSafeMatcher<List<T>>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("same sequence as " + expected);
            }

            @Override
            public boolean matchesSafely(List<T> actual) {
                return actual.containsAll(expected) && expected.containsAll(actual);
            }
        };
    }

}
