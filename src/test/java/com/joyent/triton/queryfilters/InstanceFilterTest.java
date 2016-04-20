package com.joyent.triton.queryfilters;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = { "unit" })
public class InstanceFilterTest {
    public void isEmptyReturnsTrueWhenNothingIsSet() {
        InstanceFilter filter = new InstanceFilter();
        assertTrue(filter.isEmpty(), "Is empty should be true because nothing is set");
    }

    public void isEmptyReturnsFalseWhenSomethingIsSet() {
        InstanceFilter filter = new InstanceFilter()
                .setBrand("lx");
        assertFalse(filter.isEmpty(), "Is empty should be true because nothing is set");
    }
}
