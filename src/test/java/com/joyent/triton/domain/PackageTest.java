package com.joyent.triton.domain;

import com.joyent.triton.json.CloudApiObjectMapper;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

@Test(groups = { "unit" })
public class PackageTest {
    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    public void canDeserialize() throws Exception {
        File file = new File("src/test/data/domain/package.json");
        Package pkg = mapper.readValue(file, Package.class);

        assertEquals(pkg.getId(), UUID.fromString("efb48acd-1816-4879-ad4d-0438e1077ae9"));
        assertEquals(pkg.getName(), "g3-highcpu-16-smartos");
        assertEquals(pkg.getDescription(), "High CPU 16 GB RAM 16 vCPUs and bursting 600 GB Disk");
        assertEquals(pkg.getGroup(), "High CPU");
        assertEquals(pkg.getVersion(), "1.1.0");
        assertEquals(pkg.getMemory(), 16384L);
        assertEquals(pkg.getDisk(), 614400L);
        assertEquals(pkg.getSwap(), 32768L);
        assertEquals(pkg.getVcpus(), 0);
        assertEquals(pkg.getLwps(), 4000);
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnObjectMap() throws Exception {
        File file = new File("src/test/data/domain/package.json");
        Package pkg = mapper.readValue(file, Package.class);

        Map<String, Object> map = pkg.asMap();

        assertEquals(map.get("id"), pkg.getId());
        assertEquals(map.get("name"), pkg.getName());
        assertEquals(map.get("description"), pkg.getDescription());
        assertEquals(map.get("group"), pkg.getGroup());
        assertEquals(map.get("version"), pkg.getVersion());
        assertEquals(map.get("memory"), pkg.getMemory());
        assertEquals(map.get("disk"), pkg.getDisk());
        assertEquals(map.get("swap"), pkg.getSwap());
        assertEquals(map.get("vcpus"), pkg.getVcpus());
        assertEquals(map.get("lwps"), pkg.getLwps());
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnStringMap() throws Exception {
        File file = new File("src/test/data/domain/package.json");
        Package pkg = mapper.readValue(file, Package.class);

        Map<String, String> map = pkg.asStringMap();

        assertEquals(map.get("id"), pkg.getId().toString());
        assertEquals(map.get("name"), pkg.getName());
        assertEquals(map.get("description"), pkg.getDescription());
        assertEquals(map.get("group"), pkg.getGroup());
        assertEquals(map.get("version"), pkg.getVersion());
        assertEquals(map.get("memory"), String.valueOf(pkg.getMemory()));
        assertEquals(map.get("disk"), String.valueOf(pkg.getDisk()));
        assertEquals(map.get("swap"), String.valueOf(pkg.getSwap()));
        assertEquals(map.get("vcpus"), String.valueOf(pkg.getVcpus()));
        assertEquals(map.get("lwps"), String.valueOf(pkg.getLwps()));
    }
}
