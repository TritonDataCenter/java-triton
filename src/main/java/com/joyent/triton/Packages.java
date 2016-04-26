package com.joyent.triton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.exceptions.CloudApiException;
import com.joyent.triton.exceptions.CloudApiIOException;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiConnectionFactory;
import com.joyent.triton.http.CloudApiResponseHandler;
import com.joyent.triton.http.HttpCollectionResponse;
import com.joyent.triton.domain.Package;
import com.joyent.triton.queryfilters.PackageFilter;
import com.joyent.triton.queryfilters.PackageFilterConverter;
import com.joyent.triton.queryfilters.QueryFilterConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_OK;

/**
 * API to interact directly with packages (instance types) on Triton.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Packages {
    /**
     * Configuration context that provides SDK settings.
     */
    private final ConfigContext config;

    /**
     * Reference to main API class used for creating connections.
     */
    private final CloudApi cloudApi;

    /**
     * Logger instance.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@link org.apache.http.impl.client.CloseableHttpClient} connection factory.
     */
    private final CloudApiConnectionFactory connectionFactory;

    /**
     * Customized Jackson serialization/deserialization object.
     */
    private final ObjectMapper mapper;

    /**
     * Query filter converter class that allows you to convert from a {@link PackageFilter}
     * to a {@link java.util.Collection} of {@link NameValuePair}.
     */
    private final QueryFilterConverter<PackageFilter> packageFilterConverter =
            new PackageFilterConverter();

    /**
     * Response handler for listing packages.
     */
    private final CloudApiResponseHandler<List<Package>> listPackageHandler;

    /**
     * Response handler for finding specific packages.
     */
    private final CloudApiResponseHandler<Package> findByIdPackageHandler;

    /**
     * Creates a new configured {@code Packages} API instance.
     * @param cloudApi reference to {@link CloudApi} instance that is backing API calls.
     * @param mapper reference to the jackson object mapper to use for processing JSON
     */
    Packages(final CloudApi cloudApi, final ObjectMapper mapper) {
        this.cloudApi = cloudApi;
        this.mapper = mapper;
        this.config = cloudApi.getConfig();

        this.connectionFactory = new CloudApiConnectionFactory(config);

        this.listPackageHandler = new CloudApiResponseHandler<>(
                "list packages", mapper, new TypeReference<List<Package>>() { },
                SC_OK, false
        );

        this.findByIdPackageHandler = new CloudApiResponseHandler<Package>(
                "find package", mapper, new TypeReference<Package>() { },
                SC_OK, true
        );
    }

    /**
     * Lists all of the available packages (instance types).
     *
     * @return a collection of every package type
     * @throws IOException thrown when there is a problem getting the package list
     */
    public Collection<Package> list() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            return list(context);
        }
    }

    /**
     * Lists all of the available packages (instance types).
     *
     * @param context request context used for sharing resources between API operations
     * @return a collection of every package type
     * @throws IOException thrown when there is a problem getting the package list
     */
    public Collection<Package> list(final CloudApiConnectionContext context) throws IOException {
        return list(context, new PackageFilter());
    }

    /**
     * Lists all of the available packages (instance types).
     *
     * @param context request context used for sharing resources between API operations
     * @param filter query filter to filter results by
     * @return a collection of every package type
     * @throws IOException thrown when there is a problem getting the package list
     */
    public Collection<Package> list(final CloudApiConnectionContext context,
                                    final PackageFilter filter) throws IOException {
        Objects.requireNonNull(context, "Context object must be present");
        Objects.requireNonNull(context, "Filter object must be present");

        final List<NameValuePair> filterParams = packageFilterConverter.urlParamsFromFilter(filter);
        final String path = String.format("/%s/packages", config.getUser());

        final HttpClient client = context.getHttpClient();

        final HttpGet get = connectionFactory.get(path, filterParams);

        try {
            @SuppressWarnings("unchecked")
            final HttpCollectionResponse<Package> result =
                    (HttpCollectionResponse<Package>) client.execute(
                            get, listPackageHandler, context.getHttpContext());

            return result;
        } catch (CloudApiIOException | CloudApiException e) {
            CloudApiUtils.annotateContextedException(e, get);
            throw e;
        } catch (IOException e) {
            final String msg = "Error making request to CloudAPI.";
            final CloudApiIOException exception = new CloudApiIOException(msg, e);
            CloudApiUtils.annotateContextedException(exception, get);
            throw exception;
        }
    }

    /**
     * Get a package by specifying its id.
     *
     * @param packageId UUID of the package
     * @return package matching id if found, otherwise null
     * @throws IOException thrown when there is a problem getting the package information
     */
    public Package findById(final UUID packageId) throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            return findById(context, packageId);
        }
    }

    /**
     * Get a package by specifying its id.
     *
     * @param context request context used for sharing resources between API operations
     * @param packageId UUID of the package
     * @return package matching id if found, otherwise null
     * @throws IOException thrown when there is a problem getting the package information
     */
    public Package findById(final CloudApiConnectionContext context,
                            final UUID packageId) throws IOException  {
        Objects.requireNonNull(context, "Context must be present");
        Objects.requireNonNull(packageId, "Package id must be present");

        final String path = String.format("/%s/packages/%s",
                config.getUser(), packageId);

        final HttpClient client = context.getHttpClient();

        final HttpGet get = connectionFactory.get(path);

        try {
            return client.execute(get, findByIdPackageHandler, context.getHttpContext());
        } catch (CloudApiIOException | CloudApiException e) {
            CloudApiUtils.annotateContextedException(e, get);
            throw e;
        } catch (IOException e) {
            final String msg = "Error making request to CloudAPI.";
            final CloudApiIOException exception = new CloudApiIOException(msg, e);
            CloudApiUtils.annotateContextedException(exception, get);
            throw exception;
        }
    }

    /**
     * Finds the packages with the smallest memory footprint. This is a very
     * useful method for when you just want to create an instance with the
     * smallest amount of memory as possible.
     *
     * @return the package with the lowest amount of memory or null if there are no packages
     * @throws IOException thrown when there is a problem getting the package information
     */
    public Collection<Package> smallestMemory() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            return smallestMemory(context);
        }
    }

    /**
     * Finds the packages with the smallest memory footprint. This is a very
     * useful method for when you just want to create an instance with the
     * smallest amount of memory as possible.
     *
     * @param context request context used for sharing resources between API operations
     * @return the packages with the lowest amount of memory or an empty collection if none
     * @throws IOException thrown when there is a problem getting the package information
     */
    public Collection<Package> smallestMemory(final CloudApiConnectionContext context) throws IOException {
        return smallestMemory(context, new PackageFilter());
    }

    /**
     * Finds the packages with the smallest memory footprint. This is a very
     * useful method for when you just want to create an instance with the
     * smallest amount of memory as possible.
     *
     * @param context request context used for sharing resources between API operations
     * @param filter query filter to filter results by
     * @return the packages with the lowest amount of memory or an empty collection if none
     * @throws IOException thrown when there is a problem getting the package information
     */
    public Collection<Package> smallestMemory(final CloudApiConnectionContext context,
                                              final PackageFilter filter) throws IOException {
        Objects.requireNonNull(context, "Context must be present");
        Objects.requireNonNull(filter, "Filter must be present");

        final Collection<Package> packages = list(context, filter);

        LinkedHashSet<Package> subset = new LinkedHashSet<>();
        long smallest = -1;

        for (Package pkg : packages) {
            final long memory = pkg.getMemory();

            if (smallest < 0) {
                smallest = memory;
                subset.add(pkg);
            } else if (smallest > memory) {
                smallest = memory;
                subset.clear();
                subset.add(pkg);
            } else if (smallest == memory) {
                subset.add(pkg);
            }
        }

        return Collections.unmodifiableSet(subset);
    }
}
