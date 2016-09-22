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

package org.blockjam.core.bungee;

import static com.google.common.base.Preconditions.checkNotNull;

import org.blockjam.core.BlockJamCorePlugin;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.function.IntConsumer;

/**
 * A manager for controlling BungeeCord networks.
 */
public class BungeeManager {

    private final ChannelBinding.RawDataChannel channel;

    public BungeeManager() {
        this.channel = Sponge.getGame().getChannelRegistrar().createRawChannel(BlockJamCorePlugin.instance(), "BungeeCord");
    }

    /**
     * Transfers the given {@link Player} to the given server.
     *
     * @param player The player to transfer
     * @param server The server to transfer the {@link Player} to
     */
    public void transferPlayer(Player player, String server) {
        checkNotNull(this.channel, "channel is null!");
        checkNotNull(player, "player is null!");
        checkNotNull(server, "server is null!");
        this.channel.sendTo(player, buf -> buf.writeUTF("Connect").writeUTF(server));
    }

    /**
     * Transfers the given {@link Player} to the given {@link BungeeServers}.
     *
     * @param player The player to transfer
     * @param server The server to transfer the {@link Player} to
     */
    public void transferPlayer(Player player, BungeeServers server) {
        checkNotNull(server, "server is null!");
        this.transferPlayer(player, server.toString());
    }

    /**
     * Transfers all online {@link Player}s to the given server.
     *
     * @param server The server to transfer the {@link Player}s to
     */
    public void transferAllPlayers(String server) {
        checkNotNull(this.channel, "channel is null!");
        checkNotNull(server, "server is null!");
        this.channel.sendToAll(buf -> buf.writeUTF("Connect").writeUTF(server));
    }

    /**
     * Transfers all online {@link Player}s to the given {@link BungeeServers}.
     *
     * @param server The server to transfer the {@link Player}s to
     */
    public void transferAllPlayers(BungeeServers server) {
        checkNotNull(server, "server is null!");
        this.transferAllPlayers(server.toString());
    }

    /**
     * Gets the player count for the given server.
     *
     * @param server The server
     * @param consumer The consumer for the player count
     */
    public void getPlayerCount(String server, IntConsumer consumer) {
        checkNotNull(server, "server is null!");
        checkNotNull(consumer, "consumer is null!");
        this.channel.addListener(Platform.Type.SERVER, (data, connection, side) -> {
            if (data.readUTF().equalsIgnoreCase(server)) {
                consumer.accept(data.readInteger());
            }
        });
        this.channel.sendTo(Sponge.getServer().getOnlinePlayers().iterator().next(), buf -> buf.writeUTF("PlayerCount").writeUTF(server));
    }

    /**
     * Gets the player count for the given {@link BungeeServers}.
     *
     * @param server The server
     * @param consumer The consumer for the player count
     */
    public void getPlayerCount(BungeeServers server, IntConsumer consumer) {
        checkNotNull(server, "server is null!");
        this.getPlayerCount(server.toString(), consumer);
    }

    /**
     * Kicks the given {@link Player} for the given reason.
     *
     * @param player The player to kick from the network
     * @param reason The reason
     */
    public void kickPlayer(Player player, String reason) {
        checkNotNull(this.channel, "channel is null!");
        checkNotNull(player, "player is null!");
        checkNotNull(reason, "reason is null!");
        this.channel.sendTo(player, buf -> buf.writeUTF("KickPlayer").writeUTF(player.getName()).writeUTF(reason));
    }

    /**
     * Kicks the given {@link Player} for the given reason.
     *
     * @param player The player to kick from the network
     * @param reason The reason
     */
    public void kickPlayer(Player player, Text reason) {
        checkNotNull(player, "player is null!");
        checkNotNull(reason, "reason is null!");
        this.kickPlayer(player, TextSerializers.LEGACY_FORMATTING_CODE.serialize(reason));
    }

}
