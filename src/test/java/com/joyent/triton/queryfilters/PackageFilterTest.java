package com.joyent.triton.queryfilters;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = { "unit" })
public class PackageFilterTest {
    public void isEmptyReturnsTrueWhenNothingIsSet() {
        PackageFilter filter = new PackageFilter();
        assertTrue(filter.isEmpty(), "Is empty should be true because nothing is set");
    }

    public void isEmptyReturnsFalseWhenSomethingIsSet() {
        PackageFilter filter = new PackageFilter()
                .setDisk(1024L);
        assertFalse(filter.isEmpty(), "Is empty should be true because nothing is set");
    }
}
