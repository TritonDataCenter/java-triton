package com.joyent.triton.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.joyent.triton.json.CloudApiObjectMapper;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class ImageTest {

    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    public void canDeserialize() throws Exception {
        File file = new File("src/test/data/domain/image.json");
        Image image = mapper.readValue(file, Image.class);

        assertEquals(image.getId(), UUID.fromString("2b683a82-a066-11e3-97ab-2faa44701c5a"));
        assertEquals(image.getName(), "base");
        assertEquals(image.getVersion(), "13.4.0");
        assertEquals(image.getOs(), "smartos");
        assertEquals(image.getRequirements(), ImmutableMap.of("password", "required"));
        assertEquals(image.getType(), "zone-dataset");
        assertEquals(image.getDescription(), "A 32-bit SmartOS image with just essential "
                + "packages installed. Ideal for users who are comfortable with setting "
                + "up their own environment and tools.");

        Set<ImageFiles> expectedFiles = ImmutableSet.of(
                new ImageFiles()
                    .setCompression("gzip")
                    .setSize(110742036)
                    .setSha1("3bebb6ae2cdb26eef20cfb30fdc4a00a059a0b7b")
        );

        assertEquals(image.getFiles(), expectedFiles);

        Map<String, String> expectedTags = ImmutableMap.of(
                "role", "os",
                "group", "base-32"
        );

        assertEquals(image.getTags(), expectedTags);

        assertEquals(image.getHomepage(), "https://docs.joyent.com/images/smartos/base");
        assertEquals(image.getPublishedAt(), Instant.parse("2014-02-28T10:50:42Z"));
        assertEquals(image.getOwner(), UUID.fromString("930896af-bf8c-48d4-885c-6573a94b1853"));
        assertEquals(image.isPubliclyAvailable(), true);
        assertEquals(image.getState(), "active");
    }
}
