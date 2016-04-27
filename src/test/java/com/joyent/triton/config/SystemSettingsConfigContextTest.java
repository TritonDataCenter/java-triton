package com.joyent.triton.config;

import org.testng.annotations.Test;

import java.util.Properties;

import static com.joyent.triton.config.MapConfigContext.KEY_ID_KEY;
import static com.joyent.triton.config.MapConfigContext.KEY_PATH_KEY;
import static com.joyent.triton.config.MapConfigContext.TIMEOUT_KEY;
import static com.joyent.triton.config.MapConfigContext.URL_KEY;
import static com.joyent.triton.config.MapConfigContext.USER_KEY;
import static org.testng.Assert.assertEquals;

@Test(groups = { "unit" })
public class SystemSettingsConfigContextTest {

    public void systemPropsOverwriteDefaults() {
        Properties properties = new Properties();
        properties.setProperty(URL_KEY, "https://triton.zone:443");
        properties.setProperty(USER_KEY, "username");
        properties.setProperty(KEY_ID_KEY, "00:00");
        properties.setProperty(KEY_PATH_KEY, "/home/username/.ssh/foo_rsa");
        properties.setProperty(TIMEOUT_KEY, "12");

        ConfigContext config = new SystemSettingsConfigContext(false, properties);

        assertEquals(config.getCloudAPIURL(),
                properties.getProperty(URL_KEY));
        assertEquals(config.getUser(),
                properties.getProperty(USER_KEY));
        assertEquals(config.getKeyId(),
                properties.getProperty(KEY_ID_KEY));
        assertEquals(config.getKeyPath(),
                properties.getProperty(KEY_PATH_KEY));
        assertEquals(String.valueOf(config.getTimeout()),
                properties.getProperty(TIMEOUT_KEY));
    }

    public void environmentVarsTakePrecendenceOverSystemProps() {
        Properties properties = new Properties();
        properties.setProperty(URL_KEY, "https://triton.zone:443");
        properties.setProperty(USER_KEY, "username");
        properties.setProperty(KEY_ID_KEY, "00:00");
        properties.setProperty(KEY_PATH_KEY, "/home/username/.ssh/foo_rsa");
        properties.setProperty(TIMEOUT_KEY, "12");

        EnvVarConfigContext envConfig = new EnvVarConfigContext();

        ConfigContext config = new SystemSettingsConfigContext(true, properties);

        if (envConfig.getKeyId() != null) {
            assertEquals(config.getKeyId(), envConfig.getKeyId());
        }

        if (envConfig.getCloudAPIURL() != null) {
            assertEquals(config.getCloudAPIURL(), envConfig.getCloudAPIURL());
        }

        if (envConfig.getKeyPath() != null) {
            assertEquals(config.getKeyPath(), envConfig.getKeyPath());
        }

        if (envConfig.getUser() != null) {
            assertEquals(config.getUser(), envConfig.getUser());
        }

        if (envConfig.getTimeout() != null) {
            assertEquals(config.getTimeout(), envConfig.getTimeout());
        }
    }
}
