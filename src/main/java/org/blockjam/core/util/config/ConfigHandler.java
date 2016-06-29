/*
 * Copyright (c) 2016, BlockJam - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.blockjam.core.util.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public final class ConfigHandler {

    private final File configFile;
    private final ConfigurationLoader<?> loader;
    private ConfigurationNode config;

    public ConfigHandler(File configFile, ConfigurationLoader<?> loader) throws IOException {
        this.configFile = configFile;
        this.loader = loader;
        this.config = loader.load();
    }

    public void loadDefaults() throws IOException {
        URL defaultsInJarURL = ConfigHandler.class.getResource("/default.conf");
        ConfigurationLoader defaultsLoader = HoconConfigurationLoader.builder().setURL(defaultsInJarURL).build();
        ConfigurationNode defaults = defaultsLoader.load();

        if (!configFile.exists()) {
            Files.createFile(configFile.toPath());
        }
        config.mergeValuesFrom(defaults);
        loader.save(config);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigKey<T> key) {
        return (T) config.getNode((Object[]) key.getPath()).getValue();
    }

}
