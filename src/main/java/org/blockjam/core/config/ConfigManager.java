/*
 * This file is part of BlockJamCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, BlockJam <https://blockjam.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockjam.core.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

/**
 * A manager for the configuration.
 */
public class ConfigManager {

    private final File configFile;
    private final ConfigurationLoader<?> loader;
    private ConfigurationNode config;

    public ConfigManager(File configFile, ConfigurationLoader<?> loader) throws IOException {
        this.configFile = configFile;
        this.loader = loader;
        this.config = loader.load();
    }

    public void loadDefaults() throws IOException {
        URL defaultsInJarURL = ConfigManager.class.getResource("/default.conf");
        ConfigurationLoader defaultsLoader = HoconConfigurationLoader.builder().setURL(defaultsInJarURL).build();
        ConfigurationNode defaults = defaultsLoader.load();

        if (!configFile.exists()) {
            Files.createFile(configFile.toPath());
        }
        config.mergeValuesFrom(defaults);
        loader.save(config);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(ConfigKey<T> key) {
        return Optional.ofNullable((T) this.config.getNode((Object) key.getPath()).getValue());
    }

}
