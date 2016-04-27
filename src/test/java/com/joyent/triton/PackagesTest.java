package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.StandardConfigContext;
import com.joyent.triton.domain.Package;
import com.joyent.triton.http.CloudApiConnectionContext;
import org.apache.http.*;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static com.joyent.triton.FakeHttpClient.createMockContext;
import static org.testng.Assert.*;

@Test(groups = "unit")
public class PackagesTest {
    private static final ProtocolVersion HTTP_1_1 = new HttpVersion(1, 1);

    private ConfigContext config = null;
    private Packages packagesApi = null;
    private CloudApi cloudApi = null;

    @BeforeClass
    public void setup() {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new StandardConfigContext()
                        .setNoAuth(true)

        );
        this.cloudApi = new CloudApi(this.config);
        this.packagesApi = this.cloudApi.packages();
    }

    public void canListWithNoPackages() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        response.setEntity(new StringEntity("[]"));

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Collection<Package> packages = packagesApi.list(context);
            assertTrue(packages.isEmpty(), "This should be an empty collection");
        }
    }

    public void canListPackages() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/packages/packages.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Collection<Package> packages = packagesApi.list(context);
            assertFalse(packages.isEmpty(), "This should not be an empty collection");
            assertEquals(packages.size(), 70, "Expected 70 package types");
        }
    }

    public void canFindTheSmallestMemoryPackages() throws IOException {
        final StatusLine statusLine = new BasicStatusLine(HTTP_1_1, HttpStatus.SC_OK, "OK");
        final HttpResponse response = new BasicHttpResponse(statusLine);
        final File file = new File("src/test/data/packages/packages.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        try (CloudApiConnectionContext context = createMockContext(response)) {
            Collection<Package> packages = packagesApi.smallestMemory(context);
            assertFalse(packages.isEmpty(), "This should not be an empty collection");
            assertEquals(packages.size(), 1);

            final Package pkg = packages.iterator().next();
            assertEquals(pkg.getName(), "t4-standard-128M",
                    "Package name is unexpected. Actual package:\n" + pkg);
        }
    }
}
