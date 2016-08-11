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

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.blockjam.core.bungee.BungeeManager;
import org.blockjam.core.config.ConfigKeys;
import org.blockjam.core.config.ConfigManager;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Plugin(id = "blockjamcore", name = "BlockJamCore")
public final class BlockJamCorePlugin {

    private static BlockJamCorePlugin instance;

    @Inject @DefaultConfig(sharedRoot = false) private File configFile;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    private ConfigManager configManager;
    private BungeeManager bungeeManager;

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        instance = this;

        try {
            configManager = new ConfigManager(configFile, configLoader);
            configManager.loadDefaults();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load config");
        }

        bungeeManager = new BungeeManager();
    }

    public static BlockJamCorePlugin instance() {
        return instance;
    }

    public static ConfigManager config() {
        return instance().configManager;
    }

    public static BungeeManager bungeeManager() {
        return instance().bungeeManager;
    }

    public static byte[] getFromAuthority(String key) throws IOException {
        String url = config().get(ConfigKeys.AUTHORITY_URL);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            byte[] params = ("key=" + key).getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(params.length);
            OutputStream os = connection.getOutputStream();
            os.write(params);
            // connection.connect() can be omitted because getResponseCode() calls it automatically
            if (connection.getResponseCode() / 100 != 2) {
                // this just gets caught down below
                throw new IOException("Received bad response code from authority server ("
                        + connection.getResponseCode() + " " + connection.getResponseMessage() + ")");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int i;
            while ((i = connection.getInputStream().read(buffer)) != -1) {
                baos.write(buffer, 0, i);
            }
            return baos.toByteArray();
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid config value for `authority-url`", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Encountered connection error while contacting authority server", ex);
        }
    }
}
