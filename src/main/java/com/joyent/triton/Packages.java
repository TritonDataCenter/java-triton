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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
                "list packages", mapper, new TypeReference<List<Package>>() {
        },
                SC_OK, false
        );
    }

    public Iterator<Package> list() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            return list(context);
        }
    }

    public Iterator<Package> list(final CloudApiConnectionContext context) throws IOException {
        return list(context, new PackageFilter());
    }

    public Iterator<Package> list(final CloudApiConnectionContext context,
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

            return result.iterator();
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
}
