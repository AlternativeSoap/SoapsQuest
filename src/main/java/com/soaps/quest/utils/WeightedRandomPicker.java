/*
 * Decompiled with CFR 0.152.
 */
package com.soaps.quest.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;

public class WeightedRandomPicker<T> {
    private static final Random RANDOM = new Random();
    private final ToIntFunction<T> weightExtractor;

    public WeightedRandomPicker(ToIntFunction<T> weightExtractor) {
        this.weightExtractor = weightExtractor;
    }

    public T pick(Collection<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (T item : items) {
            totalWeight += this.weightExtractor.applyAsInt(item);
        }
        if (totalWeight <= 0) {
            return items.iterator().next();
        }
        int roll = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;
        for (T item : items) {
            if (roll >= (currentWeight += this.weightExtractor.applyAsInt(item))) continue;
            return item;
        }
        return items.iterator().next();
    }

    public T pick(List<T> items) {
        return this.pick((Collection<T>)items);
    }

    public static <K> K pickFromMap(Map<K, Integer> weightedMap) {
        if (weightedMap == null || weightedMap.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (int w : weightedMap.values()) {
            totalWeight += w;
        }
        if (totalWeight <= 0) {
            return weightedMap.keySet().iterator().next();
        }
        int roll = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;
        for (Map.Entry<K, Integer> entry : weightedMap.entrySet()) {
            if (roll >= (currentWeight += entry.getValue().intValue())) continue;
            return entry.getKey();
        }
        return weightedMap.keySet().iterator().next();
    }
}

