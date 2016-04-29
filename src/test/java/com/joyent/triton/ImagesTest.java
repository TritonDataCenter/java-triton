package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.StandardConfigContext;
import com.joyent.triton.domain.Image;
import com.joyent.triton.http.CloudApiConnectionContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import static com.joyent.triton.FakeHttpClient.createMockContext;
import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit")
public class ImagesTest {

    private ConfigContext config = null;
    private Images imagesApi = null;
    private CloudApi cloudApi = null;

    @BeforeClass
    public void setup() {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new StandardConfigContext()
                        .setNoAuth(true)

        );
        this.cloudApi = new CloudApi(this.config);
        this.imagesApi = this.cloudApi.images();
    }

    public void canListWithNoImages() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        response.setEntity(new StringEntity("[]"));

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Collection<Image> images = imagesApi.list(context);
            assertTrue(images.isEmpty(), "This should be an empty collection");
        }
    }

    public void canListImages() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/images/list_images.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Collection<Image> images = imagesApi.list(context);
            assertFalse(images.isEmpty(), "This should not be an empty collection");
            assertEquals(images.size(), 405, "Expected 405 images");
        }
    }

    public void canGetImageById() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/domain/image.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Image pkg = imagesApi.findById(context, new UUID(1, 1));
            assertNotNull(pkg);
        }
    }

    public void canHandleNotFindingImageById() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1,
                HttpStatus.SC_NOT_FOUND, "Not Found");
        final HttpResponse response = new BasicHttpResponse(statusLine);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Image pkg = imagesApi.findById(context, new UUID(1, 1));
            assertNull(pkg);
        }
    }
}
