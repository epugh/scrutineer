package com.aconex.scrutineer.v2;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.aconex.scrutineer.config.ConfigurationProvider;

@SuppressWarnings("PMD.NcssMethodCount")
public class ScrutineerCommandLineOptionsV2Extension implements ConfigurationProvider {
    private ScrutineerCommandLineOptionsV2 commandLineOptions;

    public ScrutineerCommandLineOptionsV2Extension(ScrutineerCommandLineOptionsV2 commandLineOptions) {
        this.commandLineOptions = commandLineOptions;
    }

    @Override
    public boolean numeric() {
        return this.commandLineOptions.numeric;
    }

    @Override
    public boolean versionsAsTimestamps() {
        return this.commandLineOptions.versionsAsTimestamps;
    }

    @Override
    public boolean ignoreTimestampsDuringRun() {
        return this.commandLineOptions.ignoreTimestampsDuringRun;
    }

    @Override
    public Map<String, String> getPrimaryConnectorConfigs() {
        return readPropertyFromClassPath(commandLineOptions.primaryConfig);
    }

    @Override
    public Map<String, String> getSecondaryConnectorConfigs() {
        return readPropertyFromClassPath(commandLineOptions.secondaryConfig);
    }

    private Map<String, String> readPropertyFromClassPath(String configProperty) {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(configProperty)) {
            if (is == null) {
                throw new IllegalArgumentException(format("config property file: '%s' not found in classpath, stream config files should be placed under 'config' folder", configProperty));
            }

            Properties properties = new Properties();
            properties.load(is);
            return new HashMap(properties);
        } catch (IOException e) {
            throw new RuntimeException(format("fail to load config property file: '%s'", configProperty), e);
        }
    }
}
