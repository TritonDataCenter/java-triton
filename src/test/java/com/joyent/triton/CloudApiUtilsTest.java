package com.joyent.triton;

import com.joyent.triton.http.DeserializationMode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = { "unit" })
public class CloudApiUtilsTest {
    public void toStringEmptyToNullReturnsNullWhenPassedNull() {
        assertNull(CloudApiUtils.toStringEmptyToNull(null),
                "null should always return null");
    }

    public void toStringEmptyToNullReturnsNullWhenPassedEmpty() {
        assertNull(CloudApiUtils.toStringEmptyToNull(""),
                "empty should always return null");
    }

    public void toStringEmptyToNullReturnsNullWhenPassedOnlyWhitespace() {
        assertNull(CloudApiUtils.toStringEmptyToNull("    \t   \n "),
                "whitespace only should always return null");
    }

    public void toStringEmptyToNullReturnsStringWhenPassedString() {
        assertEquals(CloudApiUtils.toStringEmptyToNull("hello"), "hello",
                "string value should return itself");
    }

    public void toStringEmptyToNullReturnsToStringValueWhenPassedObject() {
        assertEquals(CloudApiUtils.toStringEmptyToNull(new StringBuilder("hello")), "hello",
                "string value should return toString() value");
    }

    public void parseIntegerOrNullReturnsNullWhenPassedNull() {
        assertNull(CloudApiUtils.parseIntegerOrNull(null),
                "null should always return null");
    }

    public void parseIntegerOrNullReturnsNullWhenPassedEmpty() {
        assertNull(CloudApiUtils.parseIntegerOrNull(""),
                "empty should always return null");
    }

    public void parseIntegerOrNullReturnsNullWhenPassedOnlyWhitespace() {
        assertNull(CloudApiUtils.parseIntegerOrNull("    \t   \n "),
                "whitespace only should always return null");
    }

    public void parseIntegerOrNullReturnsIntegerWhenPassedInteger() {
        @SuppressWarnings("UnnecessaryBoxing")
        final Integer integer = new Integer(12);
        assertEquals(CloudApiUtils.parseIntegerOrNull(integer), integer,
                "integer should return as itself");
    }

    public void parseIntegerOrNullReturnsIntegerWhenPassedIntegerStringWithWhitespace() {
        @SuppressWarnings("UnnecessaryBoxing")
        final Integer integer = new Integer(12);
        assertEquals(CloudApiUtils.parseIntegerOrNull(" 12 \t \n"), integer,
                "integer should return as itself");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedInt() {
        final int integer = 16;
        assertEquals(CloudApiUtils.parseIntegerOrNull(integer), (Integer)integer,
                "integer should return as itself");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedLong() {
        @SuppressWarnings("UnnecessaryBoxing")
        final Long integer = new Long(24L);
        assertEquals(CloudApiUtils.parseIntegerOrNull(integer).intValue(), 24,
                "longs should return as integer value");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedWholeFloat() {
        @SuppressWarnings("UnnecessaryBoxing")
        final float floatVal = 16.0f;
        assertEquals(CloudApiUtils.parseIntegerOrNull(floatVal).intValue(), 16,
                "floats should return as integer value");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedFractionalFloat() {
        @SuppressWarnings("UnnecessaryBoxing")
        final float floatVal = 16.8f;
        assertEquals(CloudApiUtils.parseIntegerOrNull(floatVal).intValue(), 16,
                "floats should return as integer value");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedIntOverflow() {
        @SuppressWarnings("UnnecessaryBoxing")
        final Long integer = new Long(Integer.MAX_VALUE + 1L);
        assertNull(CloudApiUtils.parseIntegerOrNull(integer),
                "when too big for an integer we return null because it is an invalid value");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedNumericIntegerString() {
        final int integer = 16;
        assertEquals(CloudApiUtils.parseIntegerOrNull("16"), (Integer)integer,
                "we should have parsed 16 as an integer from the string");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedNumericHexString() {
        final int integer = 16;
        assertEquals(CloudApiUtils.parseIntegerOrNull("0x0010"), (Integer)integer,
                "we should have parsed 16 as an integer from the string");
    }

    public void parseIntegerOrNullReturnsIntWhenPassedNumericOctalString() {
        final int integer = 16;
        assertEquals(CloudApiUtils.parseIntegerOrNull("00020"), (Integer)integer,
                "we should have parsed 16 as an integer from the string");
    }

    public void parseIntegerOrNullReturnsNullWhenPassedNumericFloatString() {
        assertNull(CloudApiUtils.parseIntegerOrNull("16.0"),
                "we should have returned null when passed a float string");
    }

    public void parseIntegerOrNullReturnsNullWhenPassedNonNumericString() {
        assertNull(CloudApiUtils.parseIntegerOrNull("meatloaf"),
                "we should have returned null when not a number");
    }

    public void parseBooleanOrNullReturnsNullWhenPassedNull() {
        assertNull(CloudApiUtils.parseBooleanOrNull(null),
                "null should always return null");
    }

    public void parseBooleanOrNullReturnsNullWhenPassedEmpty() {
        assertNull(CloudApiUtils.parseBooleanOrNull(""),
                "empty should always return null");
    }

    public void parseBooleanOrNullReturnsNullWhenPassedOnlyWhitespace() {
        assertNull(CloudApiUtils.parseBooleanOrNull(" \t \n"),
                "empty with whitespace should always return null");
    }

    public void parseBooleanOrNullReturnsBooleanWhenPassedBoolean() {
        assertTrue(CloudApiUtils.parseBooleanOrNull(true),
                "should return true (itself) when passed boolean");
        assertTrue(CloudApiUtils.parseBooleanOrNull(new Boolean(true)),
                "should return true (itself) when passed boolean");


        assertFalse(CloudApiUtils.parseBooleanOrNull(false),
                "should return false (itself) when passed boolean");
        assertFalse(CloudApiUtils.parseBooleanOrNull(new Boolean(false)),
                "should return false (itself) when passed boolean");
    }

    public void parseBooleanOrNullReturnsBooleanWhenPassedBooleanString() {
        assertTrue(CloudApiUtils.parseBooleanOrNull("true"));
        assertTrue(CloudApiUtils.parseBooleanOrNull("TRUE"));
        assertTrue(CloudApiUtils.parseBooleanOrNull("t"));
        assertTrue(CloudApiUtils.parseBooleanOrNull("yes"));
        assertTrue(CloudApiUtils.parseBooleanOrNull("y"));

        assertFalse(CloudApiUtils.parseBooleanOrNull("false"));
        assertFalse(CloudApiUtils.parseBooleanOrNull("FALSE"));
        assertFalse(CloudApiUtils.parseBooleanOrNull("f"));
        assertFalse(CloudApiUtils.parseBooleanOrNull("no"));
        assertFalse(CloudApiUtils.parseBooleanOrNull("n"));
    }

    public void csvSingleCSVIsItself() {
        final String expected = "hello";
        final String csv = CloudApiUtils.csv(Collections.singletonList(expected));
        assertEquals(csv, expected, "Expected no delimiter");
    }

    public void csvMultipleValuesConvertToCSV() {
        final String expected = "hello, goodbye, farewell";
        final Iterable<String> list = ImmutableList.of("hello", "goodbye", "farewell");
        final String csv = CloudApiUtils.csv(list);
        assertEquals(csv, expected, "Expected proper delimiters of comma space");
    }

    public void csvConvertsIpAddressesProperly() throws Exception {
        final String expected = "192.168.24.99, 10.10.10.22, 0.0.0.0, 2a03:2880:2110:df07:face:b00c:0:1";
        final Iterable<?> list = ImmutableList.of(
                "192.168.24.99",
                InetAddress.getByName("10.10.10.22"),
                InetAddress.getByName("0.0.0.0"),
                InetAddress.getByName("2a03:2880:2110:df07:face:b00c:0:1"));
        final String csv = CloudApiUtils.csv(list);
        assertEquals(csv, expected, "Expected proper conversion to IP address");
    }

    public void csvChangesCommasInInputToSpaces() {
        final Iterable<String> list = ImmutableList.of("hello", "goodbye,", "farewell");
        final String csv = CloudApiUtils.csv(list);
        assertEquals(csv, "hello, goodbye , farewell");
    }

    public void csvCanSerializeStringMap() {
        final String expected = "foo: bar, hello: goodbye";
        final Map<String, String> map = ImmutableMap.of("foo", "bar", "hello", "goodbye");
        final String csv = CloudApiUtils.csv(map);

        assertEquals(csv, expected, "Map values should convert to <key>: <value>");
    }

    public void csvCanSerializeObjectMap() {
        final String expected = "foo: bar, hello: goodbye";
        final ImmutableMap.Builder<Object, Object> builder = new ImmutableMap.Builder<>();
        final Map<Object, Object> map = builder
                .put(new StringBuilder("foo"), "bar")
                .put(new StringBuilder("hello"), "goodbye")
                .build();
        final String csv = CloudApiUtils.csv(map);

        assertEquals(csv, expected, "Map values should convert to <key>: <value>");
    }

    public void csvWithoutDelimiterToStringArray() {
        final String[] expected = new String[] { "hello" };
        final String[] actual = CloudApiUtils.csv2array("hello");
        assertEquals(actual, expected, "expecting array with only 'hello'");
    }

    public void csvMultipleValuesToStringArray() {
        final String[] expected = new String[] { "hello", "goodbye", "farewell" };
        final String[] actual = CloudApiUtils.csv2array("hello, goodbye, farewell");
        assertEquals(actual, expected, "expecting array to match CSV value");
    }

    public void csvWithoutDelimiterToStringCollection() {
        final Collection<String> expected = Collections.singletonList("hello");
        final Collection<String> actual = CloudApiUtils.fromCsv("hello");
        assertEquals(actual, expected, "expecting collection with only 'hello'");
    }

    public void csvMultipleValuesToStringCollection() {
        final Collection<String> expected = ImmutableList.of("hello", "goodbye", "farewell");
        final Collection<String> actual = CloudApiUtils.fromCsv("hello, goodbye, farewell");
        assertEquals(actual, expected, "expecting collection to match CSV value");
    }

    public void canCreateStringMapFromObjectMap() {
        Map<String, ?> objectMap = ImmutableMap.of(
                "key1", 12,
                "key2", Instant.ofEpochMilli(72),
                "key3", ImmutableMap.of("hello", "goodbye"),
                "key4", ImmutableList.of("one", "two", "three"),
                "key5", DeserializationMode.ENTITY
        );

        Map<String, String> stringMap = CloudApiUtils.asStringMap(objectMap);

        String expected = "{key1=12, key2=1970-01-01T00:00:00.072Z, key3=hello: goodbye, key4=one, two, three, key5=ENTITY}";
        @SuppressWarnings("unchecked")
        String actual = StringUtils.join(stringMap);

        assertEquals(actual, expected, "We should be able to transparently "
                + "convert object maps to string maps");
    }
}
