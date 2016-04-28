package com.joyent.triton.domain;

import com.joyent.triton.json.CloudApiObjectMapper;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class ImageFilesTest {
    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    public void canDeserialize() throws Exception {
        File file = new File("src/test/data/domain/imagefiles.json");
        ImageFiles imageFiles = mapper.readValue(file, ImageFiles.class);

        assertEquals(imageFiles.getSize(), 110742036);
        assertEquals(imageFiles.getCompression(), "gzip");
        assertEquals(imageFiles.getSha1(), "3bebb6ae2cdb26eef20cfb30fdc4a00a059a0b7b");
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnObjectMap() throws Exception {
        File file = new File("src/test/data/domain/imagefiles.json");
        ImageFiles imageFiles = mapper.readValue(file, ImageFiles.class);

        Map<String, Object> map = imageFiles.asMap();

        assertEquals(map.get("size"), imageFiles.getSize());
        assertEquals(map.get("compression"), imageFiles.getCompression());
        assertEquals(map.get("sha1"), imageFiles.getSha1());
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnStringMap() throws Exception {
        File file = new File("src/test/data/domain/imagefiles.json");
        ImageFiles imageFiles = mapper.readValue(file, ImageFiles.class);

        Map<String, String> map = imageFiles.asStringMap();

        assertEquals(map.get("size"), String.valueOf(imageFiles.getSize()));
        assertEquals(map.get("compression"), imageFiles.getCompression());
        assertEquals(map.get("sha1"), imageFiles.getSha1());
    }
}
