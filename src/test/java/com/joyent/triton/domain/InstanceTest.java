package com.joyent.triton.domain;

import com.joyent.triton.json.CloudApiObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;
import org.threeten.bp.Instant;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.joyent.triton.CloudApiUtils.csv;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Test(groups = { "unit" })
public class InstanceTest {
    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    private Instance testMap() throws UnknownHostException {
        Map<String, String> tags = ImmutableMap.of(
                "name1", "value1",
                "name2", "value2"
        );

        Map<String, String> metadata = ImmutableMap.of(
                "meta1", "mvalue1",
                "meta2", "mvalue2"
        );

        return new Instance()
                .setBrand("lx")
                .setState("running")
                .setName("test-instance")
                .setComputeNode(new UUID(12L, 24L))
                .setCreated(Instant.ofEpochMilli(22222222L))
                .setUpdated(Instant.ofEpochMilli(22222244L))
                .setDisk(256)
                .setMemory(2048)
                .setDnsNames(ImmutableSet.of("dns.one", "dns.two"))
                .setFirewallEnabled(false)
                .setId(new UUID(1024L, 2048L))
                .setPackageId(new UUID(128L, 512L))
                .setImage(new UUID(4096L, 4096L))
                .setIps(ImmutableSet.of(
                        InetAddress.getByName("10.10.11.12"),
                        InetAddress.getByName("72.45.44.12")
                ))
                .setPrimaryIp(InetAddress.getByName("72.45.44.12"))
                .setLocality(new Locality()
                        .setFar(ImmutableSet.of(new UUID(6L, 8L))))
                .setNetworks(ImmutableSet.of(
                        new UUID(88L, 220L),
                        new UUID(45L, 777L)
                ))
                .setPackageName("my-custom-2g")
                .setMetadata(metadata)
                .setTags(tags);
    }

    public void canSerialize() throws Exception {
        Instance instance = testMap();

        String expected = "{\n" +
                "  \"name\" : \"test-instance\",\n" +
                "  \"image\" : \"00000000-0000-1000-0000-000000001000\",\n" +
                "  \"networks\" : [ \"00000000-0000-0058-0000-0000000000dc\", \"00000000-0000-002d-0000-000000000309\" ],\n" +
                "  \"locality\" : {\n" +
                "    \"strict\" : false,\n" +
                "    \"far\" : [ \"00000000-0000-0006-0000-000000000008\" ]\n" +
                "  },\n" +
                "  \"firewall_enabled\" : false,\n" +
                "  \"package\" : \"00000000-0000-0080-0000-000000000200\",\n" +
                "  \"tag.name1\" : \"value1\",\n" +
                "  \"tag.name2\" : \"value2\",\n" +
                "  \"metadata.meta1\" : \"mvalue1\",\n" +
                "  \"metadata.meta2\" : \"mvalue2\"\n" +
                "}";
        String actual = mapper.writeValueAsString(instance);

        assertEquals(actual, expected, "Serialized value is wrong");
    }

    public void canDeserialize() throws Exception {
        File file = new File("src/test/data/domain/instance.json");
        Instance instance = mapper.readValue(file, Instance.class);

        assertEquals(instance.getId(), UUID.fromString("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9"));
        assertEquals(instance.getName(), "jenkins-lx-test");
        assertEquals(instance.getBrand(), "lx");
        assertEquals(instance.getState(), "running");
        assertEquals(instance.getImage(), UUID.fromString("445d04f4-cad6-11e5-a1a0-9f6c0ce02707"));

        assertEquals(instance.getIps().size(), 2);
        assertTrue(instance.getIps().contains(InetAddress.getByName("167.223.138.115")));
        assertTrue(instance.getIps().contains(InetAddress.getByName("192.168.128.164")));

        assertEquals(instance.getMemory(), 4096L);
        assertEquals(instance.getDisk(), 102400L);

        assertEquals(instance.getMetadata().size(), 1);
        assertEquals(instance.getMetadata().get("root_authorized_keys"),
                "ssh-rsa XXXXXXXXXXXXXXXXXXXXXXXX jenkins@d3d0a768d56e\n");

        assertEquals(instance.getTags().size(), 1);
        assertEquals(instance.getTags().get("name"), "value");

        assertEquals(instance.getCreated(), Instant.parse("2016-02-17T01:46:15.677Z"));
        assertEquals(instance.getUpdated(), Instant.parse("2016-03-31T15:17:19.000Z"));

        assertEquals(instance.getNetworks().size(), 2);
        assertTrue(instance.getNetworks().contains(UUID.fromString("feb7b2c5-0063-42f0-a4e6-b812917397f7")));
        assertTrue(instance.getNetworks().contains(UUID.fromString("898e0149-9309-4e23-b2ed-2521b70f45a6")));

        assertEquals(instance.getPrimaryIp(), InetAddress.getByName("167.223.138.115"));
        assertEquals(instance.isFirewallEnabled(), false);
        assertEquals(instance.getComputeNode(), UUID.fromString("44454c4c-4400-1054-8052-b5c04f383432"));
        assertEquals(instance.getPackageName(), "t4-standard-4G");
        assertEquals(instance.getDnsNames().size(), 4);
        assertTrue(instance.getDnsNames().contains("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9.inst.bf80aadd-8616-c9c2-a074-8bdd028e7a7b.us-east-3b.triton.zone"));
        assertTrue(instance.getDnsNames().contains("jenkins-lx-test.inst.bf80aadd-8616-c9c2-a074-8bdd028e7a7b.us-east-3b.triton.zone"));
        assertTrue(instance.getDnsNames().contains("c872d3bf-cbaa-4165-8e18-f6e3e1d94da9.inst.bf80aadd-8616-c9c2-a074-8bdd028e7a7b.us-east-3b.cns.joyent.us"));
        assertTrue(instance.getDnsNames().contains("jenkins-lx-test.inst.bf80aadd-8616-c9c2-a074-8bdd028e7a7b.us-east-3b.cns.joyent.us"));

        assertNull(instance.getLocality());
        assertNull(instance.getPackageId());
    }

    public void canFindPrivateIps() throws UnknownHostException {
        Set<InetAddress> privateIps = ImmutableSet.of(
                InetAddress.getByName("127.0.0.1"),
                InetAddress.getByName("192.168.77.44"),
                InetAddress.getByName("10.22.33.22")
        );

        Set<InetAddress> publicIps = ImmutableSet.of(
                InetAddress.getByName("72.2.119.167"),
                InetAddress.getByName("72.2.119.162")
        );

        ImmutableSet.Builder<InetAddress> builder = ImmutableSet.builder();

        Set<InetAddress> ips = builder.addAll(privateIps).addAll(publicIps).build();

        Instance instance = new Instance()
                .setIps(ips);

        Set<InetAddress> foundPrivateIps = instance.privateIPs();

        assertEquals(foundPrivateIps.size(), privateIps.size(), "There should be the same number of ips");

        for (InetAddress ip : privateIps) {
            assertTrue(foundPrivateIps.contains(ip), "Instance private ips didn't contain: " + ip);
        }
    }

    public void canConvertToAnObjectMap() throws Exception {
        Instance instance = testMap();

        Map<String, Object> map = instance.asMap();

        assertEquals(map.get("brand"), instance.getBrand());
        assertEquals(map.get("compute_node"), instance.getComputeNode());
        assertEquals(map.get("created"), instance.getCreated());
        assertEquals(map.get("dns_names"), instance.getDnsNames());
        assertEquals(map.get("id"), instance.getId());
        assertEquals(map.get("image"), instance.getImage());
        assertEquals(map.get("ips"), instance.getIps());
        assertEquals(map.get("locality"), instance.getLocality());
        assertEquals(map.get("metadata"), instance.getMetadata());
        assertEquals(map.get("name"), instance.getName());
        assertEquals(map.get("networks"), instance.getNetworks());
        assertEquals(map.get("package_id"), instance.getPackageId());
        assertEquals(map.get("package_name"), instance.getPackageName());
        assertEquals(map.get("primary_ip"), instance.getPrimaryIp());
        assertEquals(map.get("state"), instance.getState());
        assertEquals(map.get("tags"), instance.getTags());
        assertEquals(map.get("updated"), instance.getUpdated());
        assertEquals(map.get("disk"), instance.getDisk());
        assertEquals(map.get("memory"), instance.getMemory());
    }

    public void canConvertToAnStringMap() throws Exception {
        Instance instance = testMap();

        Map<String, String> map = instance.asStringMap();

        assertEquals(map.get("brand"), instance.getBrand());
        assertEquals(map.get("compute_node"), instance.getComputeNode().toString());
        assertEquals(map.get("created"), instance.getCreated().toString());
        assertEquals(map.get("dns_names"), csv(instance.getDnsNames()));
        assertEquals(map.get("id"), instance.getId().toString());
        assertEquals(map.get("image"), instance.getImage().toString());
        assertEquals(map.get("ips"), csv(instance.getIps()));
        assertEquals(map.get("locality"), instance.getLocality().toString());
        assertEquals(map.get("metadata"), csv(instance.getMetadata()));
        assertEquals(map.get("name"), instance.getName());
        assertEquals(map.get("networks"), csv(instance.getNetworks()));
        assertEquals(map.get("package_id"), instance.getPackageId().toString());
        assertEquals(map.get("package_name"), instance.getPackageName());
        assertEquals(map.get("primary_ip"), instance.getPrimaryIp().getHostAddress());
        assertEquals(map.get("state"), instance.getState());
        assertEquals(map.get("tags"), csv(instance.getTags()));
        assertEquals(map.get("updated"), instance.getUpdated().toString());
        assertEquals(map.get("disk"), String.valueOf(instance.getDisk()));
        assertEquals(map.get("memory"), String.valueOf(instance.getMemory()));
    }
}
