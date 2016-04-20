package com.joyent.triton;

import com.google.common.collect.ImmutableSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.UnknownHostException;
import java.util.Set;

/**
 * Tests related to verifying the behavior of IP address functionality.
 */
@Test(groups = { "unit" })
public class CloudApiUtilsIpTest {
    public void validatesPublicIpAddressV4() throws UnknownHostException {
        Set<String> ips = ImmutableSet.of(
            "50.31.209.254",
            "98.139.183.24",
            "173.194.33.161"
        );

        for (String ip : ips) {
            Assert.assertFalse(CloudApiUtils.isPrivateIp(ip),
                    String.format("Address [%s] should not be private", ip));
        }
    }

    public void recognizesPrivateAddressV4() throws UnknownHostException {
        Set<String> ips = ImmutableSet.of(
            "127.0.0.1",
            "10.1.10.28",
            "172.16.1.1",
            "192.168.12.42"
        );

        for (String ip : ips) {
            Assert.assertTrue(CloudApiUtils.isPrivateIp(ip),
                    String.format("Address [%s] should be private", ip));
        }
    }

    public void validatesPublicIpAddressV6() throws UnknownHostException {
        Set<String> ips = ImmutableSet.of(
            "2607:f8b0:4000:808::1000",
            "2001:db8:3333:4444:5555:6666:7777:8888"
        );

        for (String ip : ips) {
            Assert.assertFalse(CloudApiUtils.isPrivateIp(ip),
                    String.format("Address [%s] should not be private", ip));
        }
    }

    public void recognizesPrivateAddressV6() throws UnknownHostException {
        Set<String> ips = ImmutableSet.of(
            "fe80::ca60:ff:fe6c:fb91",
            "FE80:0000:0000:0000:0202:B3FF:FE1E:8329",
            "fe80::a0a:0",
            "::"
        );

        for (String ip : ips) {
            Assert.assertTrue(CloudApiUtils.isPrivateIp(ip),
                    String.format("Address [%s] should be private", ip));
        }
    }
}
