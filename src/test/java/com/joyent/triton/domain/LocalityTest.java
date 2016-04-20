package com.joyent.triton.domain;

import com.joyent.triton.json.CloudApiObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = { "unit" })
public class LocalityTest {
    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    private Locality testLocality() {
        return new Locality()
                .setFar(ImmutableSet.of(new UUID(0, 64), new UUID(0, 128)))
                .setNear(ImmutableSet.of(new UUID(112, 33), new UUID(1, 2)))
                .setStrict(true);
    }

    public void canSerialize() throws Exception {
        final Locality locality = testLocality();

        String expected = "{\n" +
                "  \"strict\" : true,\n" +
                "  \"near\" : [ \"00000000-0000-0070-0000-000000000021\", \"00000000-0000-0001-0000-000000000002\" ],\n" +
                "  \"far\" : [ \"00000000-0000-0000-0000-000000000040\", \"00000000-0000-0000-0000-000000000080\" ]\n" +
                "}";
        String actual = mapper.writeValueAsString(locality);

        assertEquals(actual, expected, "Serialized value is wrong");
    }

    // We don't deserialize the locality entity anywhere, so we don't test it

    public void canConvertToAnObjectMap() throws Exception {
        final Locality locality = testLocality();

        Map<String, Object> map = locality.asMap();
        Set<UUID> far = ImmutableSet.of(new UUID(0, 64), new UUID(0, 128));
        Set<UUID> near = ImmutableSet.of(new UUID(112, 33), new UUID(1, 2));

        assertEquals(map.get("far"), far);
        assertEquals(map.get("near"), near);
        assertTrue((Boolean)map.get("is_strict"));
    }

    public void canConvertToAnStringMap() throws Exception {
        final Locality locality = testLocality();

        Map<String, String> map = locality.asStringMap();

        assertEquals(map.get("far"), "00000000-0000-0000-0000-000000000040, 00000000-0000-0000-0000-000000000080");
        assertEquals(map.get("near"), "00000000-0000-0070-0000-000000000021, 00000000-0000-0001-0000-000000000002");
        assertEquals(map.get("is_strict"), "true");
    }
}
