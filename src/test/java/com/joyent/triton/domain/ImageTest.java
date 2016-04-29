package com.joyent.triton.domain;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.joyent.triton.CloudApiUtils;
import com.joyent.triton.json.CloudApiObjectMapper;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        assertEquals(image.getRequirements(),
                ImmutableMap.of("min_memory", 1024, "min_ram", 1024));
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

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnObjectMap() throws Exception {
        File file = new File("src/test/data/domain/image.json");
        Image image = mapper.readValue(file, Image.class);

        Map<String, Object> map = image.asMap();

        assertEquals(map.get("id"), image.getId());
        assertEquals(map.get("name"), image.getName());
        assertEquals(map.get("version"), image.getVersion());
        assertEquals(map.get("os"), image.getOs());
        assertEquals(map.get("requirements"), image.getRequirements());
        assertEquals(map.get("type"), image.getType());
        assertEquals(map.get("description"), image.getDescription());
        assertEquals(map.get("files"), image.getFiles());
        assertEquals(map.get("tags"), image.getTags());
        assertEquals(map.get("homepage"), image.getHomepage());
        assertEquals(map.get("published_at"), image.getPublishedAt());
        assertEquals(map.get("owner"), image.getOwner());
        assertEquals(map.get("public"), image.isPubliclyAvailable());
        assertEquals(map.get("state"), image.getState());
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnStringMap() throws Exception {
        File file = new File("src/test/data/domain/image.json");
        Image image = mapper.readValue(file, Image.class);

        Map<String, String> map = image.asStringMap();

        assertEquals(map.get("id"), image.getId().toString());
        assertEquals(map.get("name"), image.getName());
        assertEquals(map.get("version"), image.getVersion());
        assertEquals(map.get("os"), image.getOs());
        assertEquals(map.get("requirements"), CloudApiUtils.csv(image.getRequirements()));
        assertEquals(map.get("type"), image.getType());
        assertEquals(map.get("description"), image.getDescription());
        assertEquals(map.get("files"), CloudApiUtils.csv(image.getFiles()));
        assertEquals(map.get("tags"), CloudApiUtils.csv(image.getTags()));
        assertEquals(map.get("homepage"), image.getHomepage());
        assertEquals(map.get("published_at"), image.getPublishedAt().toString());
        assertEquals(map.get("owner"), image.getOwner().toString());
        assertEquals(map.get("public"), String.valueOf(image.isPubliclyAvailable()));
        assertEquals(map.get("state"), image.getState());
    }

    public void canSortByVersionsWithDots() {
        List<String> versions = ImmutableList.of(
                "0.0.1",
                "0.0.2",
                "0.1.0",
                "0.1.4",
                "0.2",
                "0.2.3",
                "0.2.9",
                "0.2.17",
                "1.9.2",
                "2.1",
                "2.1.9"
        );

        assertVersionsSort(versions);
    }

    public void canSortByReverseOrder() {
        List<String> versions = ImmutableList.of(
                "2.1.9",
                "2.1",
                "1.9.2",
                "0.2.17",
                "0.2.9",
                "0.2.3",
                "0.2",
                "0.1.4",
                "0.1.0",
                "0.0.2",
                "0.0.1"
        );

        assertVersionsSort(versions, false);
    }

    public void canSortDateVersions() {
        List<String> versions = ImmutableList.of(
                "20160204",
                "20160208",
                "20160217.1",
                "20160217.2",
                "20160217.2",
                "20160218",
                "20160219"
        );

        assertVersionsSort(versions);
    }

    public void canSortWithMixedStrings() {
        List<String> versions = ImmutableList.of(
                "0.0.1.beta1",
                "0.0.2.beta2",
                "0.1.0",
                "0.1.4",
                "0.2",
                "0.2.3",
                "0.2.9",
                "0.2.17",
                "0.2.poodle",
                "1.9.2",
                "2.1",
                "2.1.9",
                "dog",
                "dog.2.0"
        );

        assertVersionsSort(versions);
    }

    public void canSortWithNullsVersions() {
        List<String> versions = new ArrayList<>();
        versions.add(null);
        versions.add(null);
        versions.add("20160204");
        versions.add("20160208");
        versions.add("20160217.1");
        versions.add("20160217.2");
        versions.add("20160217.2");
        versions.add("20160218");
        versions.add("20160219");

        List<String> copy = new ArrayList<>(versions);
        Collections.shuffle(copy);

        List<Image> images = FluentIterable.from(copy).transform(
                new Function<String, Image>() {
                    @Override
                    public Image apply(final String s) {
                        return new Image().setVersion(s);
                    }
                }).toSortedList(new Image.VersionComparator());

        final List<String> actual = new ArrayList<>(versions.size());

        for (Image i : images) {
            actual.add(i.getVersion());
        }

        assertEquals(actual, versions,
                "Versions were not sorted correctly.\n"
                        + "Expected order: " + CloudApiUtils.csv(versions) + "\n"
                        + "Actual order:   " + CloudApiUtils.csv(actual) + "\n");
    }

    private static void assertVersionsSort(final List<String> versions) {
        assertVersionsSort(versions, true);
    }

    private static void assertVersionsSort(final List<String> versions,
                                           final boolean lowToHigh) {
        List<String> copy = new ArrayList<>(versions);
        Collections.shuffle(copy);

        List<Image> images = FluentIterable.from(copy).transform(
                new Function<String, Image>() {
                    @Override
                    public Image apply(final String s) {
                        return new Image().setVersion(s);
                    }
                }).toSortedList(new Image.VersionComparator(lowToHigh));

        List<String> actual = FluentIterable.from(images).transform(
                new Function<Image, String>() {
                    @Override
                    public String apply(final Image i) {
                        return i.getVersion();
                    }
                }).toList();

        assertEquals(actual, versions,
                "Versions were not sorted correctly.\n"
                        + "Expected order: " + CloudApiUtils.csv(versions) + "\n"
                        + "Actual order:   " + CloudApiUtils.csv(actual) + "\n");
    }
}
