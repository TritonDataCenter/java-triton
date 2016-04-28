package com.joyent.triton.domain;

import com.joyent.triton.CloudApiUtils;
import com.joyent.triton.json.CloudApiObjectMapper;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class ErrorDetailTest {

    private static final CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    public void canDeserialize() throws Exception {
        File file = new File("src/test/data/error/suberrors.json");
        ErrorDetail detail = mapper.readValue(file, ErrorDetail.class);

        assertEquals(detail.getCode(), "InvalidHeader");
        assertEquals(detail.getMessage(), "Authorization header invalid: "
                + "clock skew of 2296.141s was greater than 300s");

        Map<String, String> subError1 = detail.getErrors().get(0);
        assertEquals(subError1.get("key1"), "first sub error key1");
        assertEquals(subError1.get("key2"), "first sub error key2");

        Map<String, String> subError2 = detail.getErrors().get(1);
        assertEquals(subError2.get("key1"), "second sub error key1");
        assertEquals(subError2.get("key2"), "second sub error key2");
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnObjectMap() throws Exception {
        File file = new File("src/test/data/error/suberrors.json");
        ErrorDetail detail = mapper.readValue(file, ErrorDetail.class);

        Map<String, Object> map = detail.asMap();

        assertEquals(map.get("code"), detail.getCode());
        assertEquals(map.get("message"), detail.getMessage());
        assertEquals(map.get("errors"), detail.getErrors());
    }

    @Test(dependsOnMethods = "canDeserialize")
    public void canConvertToAnStringMap() throws Exception {
        File file = new File("src/test/data/error/suberrors.json");
        ErrorDetail detail = mapper.readValue(file, ErrorDetail.class);

        Map<String, String> map = detail.asStringMap();

        assertEquals(map.get("code"), detail.getCode());
        assertEquals(map.get("message"), detail.getMessage());
        assertEquals(map.get("errors"), CloudApiUtils.csv(detail.getErrors()));
    }
}
