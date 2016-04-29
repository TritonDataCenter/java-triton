package com.joyent.triton;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.domain.Image;
import com.joyent.triton.exceptions.CloudApiException;
import com.joyent.triton.exceptions.CloudApiIOException;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.http.CloudApiConnectionFactory;
import com.joyent.triton.http.CloudApiResponseHandler;
import com.joyent.triton.http.HttpCollectionResponse;
import com.joyent.triton.queryfilters.ImageFilter;
import com.joyent.triton.queryfilters.ImageFilterConverter;
import com.joyent.triton.queryfilters.PackageFilter;
import com.joyent.triton.queryfilters.QueryFilterConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.apache.http.HttpStatus.SC_OK;

/**
 * API to interact directly with operating system images on Triton.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.0
 */
public class Images {
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
     * Query filter converter class that allows you to convert from a {@link PackageFilter}
     * to a {@link java.util.Collection} of {@link NameValuePair}.
     */
    private final QueryFilterConverter<ImageFilter> imageFilterConverter =
            new ImageFilterConverter();

    /**
     * Response handler for listing packages.
     */
    private final CloudApiResponseHandler<List<Image>> listImageHandler;

    /**
     * Response handler for finding specific packages.
     */
    private final CloudApiResponseHandler<Image> findByIdImageHandler;

    /**
     * Creates a new configured {@code Images} API instance.
     * @param cloudApi reference to {@link CloudApi} instance that is backing API calls.
     * @param mapper reference to the jackson object mapper to use for processing JSON
     */
    Images(final CloudApi cloudApi, final ObjectMapper mapper) {
        this.cloudApi = cloudApi;
        this.config = cloudApi.getConfig();

        this.connectionFactory = new CloudApiConnectionFactory(config);

        this.listImageHandler = new CloudApiResponseHandler<List<Image>>(
                "list images", mapper, new TypeReference<List<Image>>() { },
                SC_OK, false
        );

        this.findByIdImageHandler = new CloudApiResponseHandler<Image>(
                "find image", mapper, new TypeReference<Image>() { },
                SC_OK, true
        );
    }

    /**
     * Lists all of the available operating system images.
     *
     * @return a collection of every image
     * @throws IOException thrown when there is a problem getting the image list
     */
    public Collection<Image> list() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            return list(context);
        }
    }

    /**
     * Lists all of the available operating system images.
     *
     * @param context request context used for sharing resources between API operations
     * @return a collection of every image
     * @throws IOException thrown when there is a problem getting the image list
     */
    public Collection<Image> list(final CloudApiConnectionContext context) throws IOException {
        return list(context, new ImageFilter());
    }

    /**
     * Lists all of the available operating system images.
     *
     * @param context request context used for sharing resources between API operations
     * @param filter query filter to filter results by
     * @return a collection of every image
     * @throws IOException thrown when there is a problem getting the image list
     */
    public Collection<Image> list(final CloudApiConnectionContext context,
                                  final ImageFilter filter) throws IOException {
        Objects.requireNonNull(context, "Context object must be present");
        Objects.requireNonNull(context, "Filter object must be present");

        final List<NameValuePair> filterParams = imageFilterConverter.urlParamsFromFilter(filter);
        final String path = String.format("/%s/images", config.getUser());

        final HttpClient client = context.getHttpClient();

        final HttpGet get = connectionFactory.get(path, filterParams);

        try {
            @SuppressWarnings("unchecked")
            final HttpCollectionResponse<Image> result =
                    (HttpCollectionResponse<Image>) client.execute(
                            get, listImageHandler, context.getHttpContext());

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
}
