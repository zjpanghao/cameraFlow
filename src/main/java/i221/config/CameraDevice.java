package i221.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Configuration
@PropertySource("classpath:camera.properties")
@ConfigurationProperties(prefix = "ky.camera")
public class CameraDevice {
    private String test;
    private List<Map<String, String>> device;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public List<Map<String, String>> getDevice() {
        return device;
    }

    public void setDevice(List<Map<String, String>> device) {
        this.device = device;
    }
}
