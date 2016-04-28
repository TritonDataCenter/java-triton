package com.joyent.triton;

import com.joyent.triton.http.CloudApiHttpHeaders;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Collection of general utility functions used globally throughout the project.
 * Many of these functions could be replaced with Guava or Apache Commons Lang.
 * However, we are trying to limit our dependency footprint here, so we have our
 * own versions.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public final class CloudApiUtils {
    /**
     * Private constructor because this is a utility class.
     */
    private CloudApiUtils() { }

    /**
     * Converts an object's toString value to a String. If an empty string, the return null.
     *
     * @param value object to parse .toString() value from
     * @return null if toString() returns empty or if the passed in value is null, otherwise toString() value
     */
    public static String toStringEmptyToNull(final Object value) {
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();

        if (StringUtils.isBlank(stringValue)) {
            return null;
        }

        return stringValue;
    }

    /**
     * Parses an arbitrary object for an integer. If it can't be found, return null.
     *
     * @param value Object to parse for an integer
     * @return if parsing fails, return null
     */
    public static Integer parseIntegerOrNull(final Object value) {
        Integer parsed;

        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            final Number number = ((Number)value);

            if (number.longValue() > Integer.MAX_VALUE || number.longValue() < Integer.MIN_VALUE) {
                return null;
            }

            return number.intValue();
        }

        final String string = StringUtils.strip(toStringEmptyToNull(value));

        if (string == null) {
            return null;
        }

        try {
            parsed = NumberUtils.createInteger(string);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(CloudApiUtils.class);
            String msg = "Error parsing value as integer. Value: %s";
            logger.warn(String.format(msg, value), e);
            parsed = null;
        }

        return parsed;
    }


    /**
     * Parses an arbitrary object for a boolean. If it can't be found, return null.
     *
     * @param value Object to parse for an boolean
     * @return if parsing fails, return null
     */
    public static Boolean parseBooleanOrNull(final Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean) {
            return (Boolean)value;
        }

        final String string = StringUtils.strip(toStringEmptyToNull(value));

        if (string == null) {
            return null;
        }

        Boolean parsed;

        try {
            parsed = BooleanUtils.toBoolean(string);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(CloudApiUtils.class);
            String msg = "Error parsing value as boolean. Value: %s";
            logger.warn(String.format(msg, value), e);
            parsed = null;
        }

        return parsed;
    }

    /**
     * Naively converts a collection of objects to a single CSV string.
     * Warning: this doesn't escape.
     * @param stringable collection of objects with implemented toString methods
     * @return CSV string or empty string
     */
    public static String csv(final Iterable<?> stringable) {
        Objects.requireNonNull(stringable, "Iterable value must be present");

        final StringBuilder builder = new StringBuilder();

        Iterator<?> itr = stringable.iterator();

        while (itr.hasNext()) {
            final Object o = itr.next();

            if (o == null) {
                continue;
            }

            final String value;

            if (o instanceof InetAddress) {
                value = ((InetAddress)o).getHostAddress();
            } else if (o instanceof Map) {
                StringBuilder sb = new StringBuilder();

                @SuppressWarnings("unchecked")
                final Map map = (Map)o;
                @SuppressWarnings("unchecked")
                final Iterator<Map.Entry<?, ?>> mapItr =
                        (Iterator<Map.Entry<?, ?>>)map.entrySet().iterator();

                while (mapItr.hasNext()) {
                    Map.Entry<?, ?> entry = mapItr.next();
                    sb.append("[")
                      .append(String.valueOf(entry.getKey()))
                      .append("=")
                      .append(entry.getValue())
                      .append("]");

                    if (mapItr.hasNext()) {
                        sb.append(" ");
                    }
                }

                value = sb.toString();
            } else {
                value = o.toString();
            }

            // Strip any commas out of the string
            builder.append(StringUtils.replaceChars(value, ',', ' '));

            if (itr.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    /**
     * Naively converts a map to a single CSV string. Warning: this doesn't
     * escape.
     *
     * @param map map with objects with implemented toString methods
     * @return CSV string or empty string
     */
    public static String csv(final Map<?, ?> map) {
        Objects.requireNonNull(map, "Map must be present");

        final StringBuilder builder = new StringBuilder();

        /* We do this contorted type conversion because of Java's generics. */
        @SuppressWarnings("rawtypes")
        final Map noGenericsMap = (Map)map;
        @SuppressWarnings({"rawtypes", "unchecked"})
        final Set<Map.Entry<?, ?>> set = noGenericsMap.entrySet();

        final Iterator<Map.Entry<?, ?>> itr = set.iterator();

        while (itr.hasNext()) {
            Map.Entry<?, ?> entry = itr.next();

            if (entry == null || entry.getKey() == null) {
                continue;
            }

            builder.append(entry.getKey().toString())
                   .append(": ")
                   .append(String.valueOf(entry.getValue()));

            if (itr.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    /**
     * Converts a naive CSV string to a collection.
     *
     * @param line CSV string
     * @return collection containing each value between each comma
     */
    public static Collection<String> fromCsv(final String line) {
        Objects.requireNonNull(line, "Line must be present");

        final List<String> list = new ArrayList<>();

        if (line.contains(",")) {
            String[] parts = csv2array(line);
            Collections.addAll(list, parts);
        } else {
            list.add(line);
        }

        return list;
    }

    /**
     * Naively converts a CSV string into an array.
     *
     * @param line non-null String containing comma delimiters
     * @return an array of Strings for each token between a comma
     */
    public static String[] csv2array(final String line) {
        Objects.requireNonNull(line, "Line must be present");

        if (line.contains(",")) {
            return line.split(",\\s*");
        } else {
            return new String[] {line};
        }
    }

    /**
     * Serializes a specified value to a {@link String}.
     * @param value the value to be serialized
     * @return a serialized value as a {@link String}
     */
    public static String asString(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Iterable<?>) {
            StringBuilder sb = new StringBuilder();

            Iterator<?> itr = ((Iterable<?>) value).iterator();
            while (itr.hasNext()) {
                Object next = itr.next();

                if (next != null) {
                    sb.append(next.toString());
                }

                if (itr.hasNext()) {
                    sb.append(",");
                }
            }

            return sb.toString();
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[])value;

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < array.length; i++) {
                Object next = array[i];

                if (next != null) {
                    sb.append(next.toString());
                }

                if (i < array.length - 1) {
                    sb.append(", ");
                }
            }

            return sb.toString();
        }

        return value.toString();
    }

    /**
     * Converts a map of string to object values to a pure string map.
     *
     * @param map map to convert
     * @return a string map
     */
    public static Map<String, String> asStringMap(final Map<String, ?> map) {
        Objects.requireNonNull(map, "Map must be present");

        if (map.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<String, String> stringMap = new LinkedHashMap<>(map.size());

        // Silly Java generics won't covert wildcard to simple generic
        @SuppressWarnings("unchecked")
        final Map<String, Object> objectMap = (Map<String, Object>)map;
        final Set<Map.Entry<String, Object>> entrySet = objectMap.entrySet();

        for (Map.Entry<String, Object> next : entrySet) {
            final Object obj = next.getValue();
            final String value;

            if (obj == null || obj instanceof String) {
                value = (String) obj;
            } else if (obj instanceof InetAddress) {
                value = ((InetAddress) obj).getHostAddress();
            } else if (obj instanceof Map) {
                value = CloudApiUtils.csv((Map) obj);
            } else if (obj instanceof Iterable) {
                value = CloudApiUtils.csv((Iterable) obj);
            } else if (obj instanceof CharSequence) {
                value = String.valueOf(obj);
            } else {
                value = obj.toString();
            }

            stringMap.put(next.getKey(), value);
        }

        return Collections.unmodifiableMap(stringMap);
    }

    /**
     * Determines if a given IP address is private.
     *
     * @param ip IP address as {@link String} to test
     * @return true if private, otherwise false
     * @throws UnknownHostException thrown when the passed string can't be parsed for an IP
     */
    public static boolean isPrivateIp(final String ip) throws UnknownHostException {
        final InetAddress address = InetAddress.getByName(ip);
        return isPrivateIp(address);
    }

    /**
     * Determines if a given IP address is private.
     *
     * @param address address to test
     * @return true if private, otherwise false
     */
    public static boolean isPrivateIp(final InetAddress address) {
        Objects.requireNonNull("IP address must be present");
        return address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isLinkLocalAddress() || address.isSiteLocalAddress();
    }

    /**
     * Finds all privates IP address from the passed collection and eliminates
     * all duplicates.
     *
     * @param addresses collection of IP addresses
     * @return immutable set of unique private IP addresses
     */
    public static Set<InetAddress> findUniquePrivateIps(
            final Collection<InetAddress> addresses) {
        Objects.requireNonNull(addresses, "Set of addresses must be present");

        if (addresses.isEmpty()) {
            return Collections.emptySet();
        }

        final Set<InetAddress> privateIPs = new LinkedHashSet<>(addresses.size());

        for (InetAddress address : addresses) {
            if (isPrivateIp(address)) {
                privateIPs.add(address);
            }
        }

        return Collections.unmodifiableSet(privateIPs);
    }

    /**
     * Appends context attributes for the HTTP request and HTTP response objects
     * to a {@link ExceptionContext} instance.
     *
     * @param exception exception to append to
     * @param request HTTP request object
     */
    public static void annotateContextedException(final ExceptionContext exception,
                                                  final HttpRequest request) {
        annotateContextedException(exception, request, null);
    }

    /**
     * Appends context attributes for the HTTP request and HTTP response objects
     * to a {@link ExceptionContext} instance.
     *
     * @param exception exception to append to
     * @param request HTTP request object
     * @param response HTTP response object
     */
    public static void annotateContextedException(final ExceptionContext exception,
                                                  final HttpRequest request,
                                                  final HttpResponse response) {
        Objects.requireNonNull(exception, "Exception context object must be present");

        if (request != null) {
            final Header requestIdHeader = request.getFirstHeader(CloudApiHttpHeaders.REQUEST_ID);

            if (requestIdHeader != null) {
                final String requestId = requestIdHeader.getValue();
                exception.setContextValue("requestId", requestId);
            } else {
                exception.setContextValue("requestId", "[not set]");
            }

            exception.setContextValue("request", request);
            final String requestHeaders = asString(request.getAllHeaders());
            exception.setContextValue("requestHeaders", requestHeaders);
        }

        if (response != null) {
            exception.setContextValue("response", response);
            final String responseHeaders = asString(response.getAllHeaders());
            exception.setContextValue("responseHeaders", responseHeaders);
        }
    }
}
