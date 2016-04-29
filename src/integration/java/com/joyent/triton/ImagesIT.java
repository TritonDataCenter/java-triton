package com.joyent.triton;

import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.domain.Image;
import com.joyent.triton.http.CloudApiConnectionContext;
import com.joyent.triton.queryfilters.ImageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Test(groups = { "integration" })
public class ImagesIT {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConfigContext config = null;
    private Images imagesApi = null;
    private CloudApi cloudApi = null;

    @BeforeClass
    public void setup() {
        this.config = new ChainedConfigContext(
                new DefaultsConfigContext(),
                new SystemSettingsConfigContext()
        );
        this.cloudApi = new CloudApi(config);
        this.imagesApi = cloudApi.images();
    }

    public void canListImages() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final Collection<Image> images = imagesApi.list(context);

            assertFalse(images.isEmpty(), "There must be at least a single image");

            final Set<Image> imageSet = new HashSet<>(images);
            assertEquals(imageSet.size(), images.size(),
                    "There should be no duplicate images");
        }
    }

    @Test(dependsOnMethods = "canListImages")
    public void canFilterImages() throws IOException {
        try (CloudApiConnectionContext context = cloudApi.createConnectionContext()) {
            final String expectedOs = "linux";
            ImageFilter pf = new ImageFilter()
                    .setOs(expectedOs);

            final Collection<Image> images = imagesApi.list(context, pf);

            if (images.isEmpty()) {
                String msg = "Verify that there is at least a single image";
                throw new SkipException(msg);
            }

            for (Image pkg : images) {
                logger.debug("Found image: {}", pkg.getName());

                assertEquals(pkg.getOs(), expectedOs,
                        "OS didn't match filter. Images:\n" + images);
            }
        }
    }
}
