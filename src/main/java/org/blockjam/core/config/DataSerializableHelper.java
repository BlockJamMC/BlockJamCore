/*
 * Copyright (c) 2016, BlockJam - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.blockjam.core.config;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataTranslators;

import java.util.List;
import java.util.stream.Collectors;

public class DataSerializableHelper {

    /**
     * Deserializes a {@link ConfigurationNode} to a {@link DataSerializable}.
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
