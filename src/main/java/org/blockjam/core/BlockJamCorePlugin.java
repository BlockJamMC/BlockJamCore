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

package org.blockjam.core;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.blockjam.core.util.config.ConfigHandler;
import org.blockjam.core.util.config.CoreConfigKey;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Plugin(id = "blockjamcore", name = "BlockJamCore")
public final class BlockJamCorePlugin {

    public static BlockJamCorePlugin instance;

    @Inject @DefaultConfig(sharedRoot = false) private File configFile;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    private ConfigHandler configHandler;

    //TODO: make this extendable?
    @Listener
    public void onPreInitlization(GameInitializationEvent event) {
        instance = this;

        try {
            configHandler = new ConfigHandler(configFile, configLoader);
            configHandler.loadDefaults();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load config");
        }
    }

    public static BlockJamCorePlugin instance() {
        return instance;
    }

    public static ConfigHandler config() {
        return instance().configHandler;
    }

    public static String getFromAuthority(String key) {
        try {
            HttpURLConnection connection = (HttpURLConnection)
                    new URL(config().get(CoreConfigKey.AUTHORITY_URL)).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            byte[] params = ("key=" + key).getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(params.length);
            connection.connect();
            OutputStream os = connection.getOutputStream();
            os.write(params);
            return CharStreams.toString(
                    new InputStreamReader(connection.getInputStream(),
                            StandardCharsets.UTF_8));
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid value for `authority-url` in config", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to connect to authority service", ex);
        }
    }
}
