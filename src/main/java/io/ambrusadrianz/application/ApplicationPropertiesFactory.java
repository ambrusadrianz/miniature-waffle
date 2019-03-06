package io.ambrusadrianz.application;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;

public class ApplicationPropertiesFactory {

    private final YAMLMapper yamlMapper;
    private ApplicationProperties applicationProperties = null;

    public ApplicationPropertiesFactory(YAMLMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    public ApplicationProperties getApplicationProperties() {
        if (applicationProperties == null) {
            applicationProperties = loadApplicationProperties();
        }

        return applicationProperties;
    }

    private ApplicationProperties loadApplicationProperties() {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("application.yml")) {
            applicationProperties = yamlMapper.readValue(inputStream, ApplicationProperties.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read application.yml");
        }

        return applicationProperties;
    }
}
