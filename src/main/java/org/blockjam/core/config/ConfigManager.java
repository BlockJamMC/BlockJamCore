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

import static com.google.common.base.Preconditions.checkNotNull;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

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

    /**
     * Returns the bare {@link ConfigurationNode} without checking if it contains a value.
     *
     * @param key The {@link ConfigKey} to get
     * @return The requested node; Node#getValue() may return null if a value is not set
     */
    public ConfigurationNode getNodeUnsafe(ConfigKey key) {
        return this.config.getNode((Object[]) key.getPath());
    }

    /**
     * Returns the bare {@link ConfigurationNode} which definitely is non-null.
     *
     * @param key The {@link ConfigKey} to get
     * @return The requested node; Node#getValue() definitely is non-null
     */
    public ConfigurationNode getNode(ConfigKey key) {
        ConfigurationNode node = getNodeUnsafe(key);
        checkNotNull(node.getValue(), "Cannot retrieve non-existent config key!");
        return node;
    }

    /**
     * Returns the value of type T from the config.
     *
     * @param key The {@link ConfigKey} to get
     * @return The config value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigKey<T> key) {
        return (T) getNode(key).getValue();
    }

}
