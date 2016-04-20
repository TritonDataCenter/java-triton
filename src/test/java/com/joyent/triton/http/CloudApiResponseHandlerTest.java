package com.joyent.triton.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.joyent.triton.exceptions.CloudApiResponseException;
import com.joyent.triton.json.CloudApiObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertEquals;

@Test(groups = { "unit" })
public class CloudApiResponseHandlerTest {
    private CloudApiObjectMapper mapper = new CloudApiObjectMapper(true);

    public void canHandleVoidType() throws IOException {
        CloudApiResponseHandler<Void> handler = new CloudApiResponseHandler<>(
                "void test", mapper, new TypeReference<Void>() {}, SC_OK, false
        );

        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, SC_OK, "OK"));
        Void result = handler.handleResponse(response);
        assertNull(result, "Result should always be null for Void type");
    }

    public void canHandleHeaderMapType() throws IOException {
        CloudApiResponseHandler<Map<String, Header>> handler = new CloudApiResponseHandler<>(
                "header list test", mapper, new TypeReference<Map<String, Header>>() {}, SC_OK, true
        );

        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(HTTP_1_1, SC_OK, "OK"));
        Header header = new BasicHeader("test", "value");
        response.addHeader(header);
        Map<String, Header> result = handler.handleResponse(response);
        assertNotNull(result, "Result should always be a list of headers");
        assertEquals(result.get(header.getName()), header);
    }

    public void canHandleRestErrors() throws IOException {
        CloudApiResponseHandler<Map<String, Header>> handler = new CloudApiResponseHandler<>(
                "error test", mapper, new TypeReference<Map<String, Header>>() {}, SC_OK, true
        );

        StatusLine statusLine = new BasicStatusLine(HTTP_1_1, SC_BAD_REQUEST, "Bad Request");
        HttpResponse response = new BasicHttpResponse(statusLine);
        response.setHeader(CloudApiHttpHeaders.REQUEST_ID, new UUID(0L, 0L).toString());
        final File file = new File("src/test/data/error/bad_request.json");
        final HttpEntity entity = new FileEntity(file);
        response.setEntity(entity);

        boolean thrown = false;

        try {
            handler.handleResponse(response);
        } catch (CloudApiResponseException e) {
            if (!e.getMessage().contains("requestID=00000000-0000-0000-0000-000000000000")) {
                fail("Request id not logged as part of error. Actual:\n" +
                     e.getMessage());
            }

            thrown = true;
        }

        assertTrue(thrown, "Expected CloudApiResponseException to be thrown");
    }

    public void canHandleNonJsonOutputOnError() throws IOException {
        CloudApiResponseHandler<Map<String, Header>> handler = new CloudApiResponseHandler<>(
                "error test", mapper, new TypeReference<Map<String, Header>>() {}, SC_OK, true
        );

        StatusLine statusLine = new BasicStatusLine(HTTP_1_1, SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
        HttpResponse response = new BasicHttpResponse(statusLine);
        response.setHeader(CloudApiHttpHeaders.REQUEST_ID, new UUID(0L, 0L).toString());
        final HttpEntity entity = new StringEntity("I'm an error message");
        response.setEntity(entity);

        boolean thrown = false;

        try {
            handler.handleResponse(response);
        } catch (CloudApiResponseException e) {
            if (!e.getMessage().contains("requestID=00000000-0000-0000-0000-000000000000")) {
                fail("Request id not logged as part of error. Actual message:\n" +
                     e.getMessage());
            }
            if (!e.getMessage().contains("entityText=I'm an error message")) {
                fail("Entity text not outputted. Actual message:\n" +
                        e.getMessage());
            }

            thrown = true;
        }

        assertTrue(thrown, "Expected CloudApiResponseException to be thrown");
    }
}
