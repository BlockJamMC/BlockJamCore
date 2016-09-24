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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataTranslators;

import java.util.List;
import java.util.stream.Collectors;

public final class DataSerializableHelper {

    /**
     * Deserializes a {@link ConfigurationNode} to a {@link DataSerializable}.
     *
     * @param node The {@link ConfigurationNode} to deserialize
     * @return The deserialized {@link DataSerializable} of type T
     */
    @SuppressWarnings("unchecked")
    public static <T extends DataSerializable> T deserializeNode(Class<T> classOfT, ConfigurationNode node) {
        return Sponge.getDataManager()
                .deserialize(classOfT, DataTranslators.CONFIGURATION_NODE.translate(node))
                .orElseThrow(() -> new RuntimeException(("Couldn't deserialize DataSerializable!")));
    }

    /**
     * Deserializes the children of a {@link ConfigurationNode} to a List of {@link DataSerializable}.
     *
     * @param node The {@link ConfigurationNode} to deserialize
     * @return The deserialized List of {@link DataSerializable} of type T
     */
    @SuppressWarnings("unchecked")
    public static <T extends DataSerializable> List<T> deserializeListNode(Class<T> classOfT, ConfigurationNode node) {
        return node.getChildrenList()
                .stream()
                .map(n -> DataSerializableHelper.deserializeNode(classOfT, n))
                .collect(Collectors.toList());
    }
}
