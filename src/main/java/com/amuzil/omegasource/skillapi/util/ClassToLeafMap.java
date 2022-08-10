package com.amuzil.omegasource.skillapi.util;

import com.amuzil.omegasource.skillapi.data.RadixLeaf;

import java.util.HashMap;
import java.util.Map;

public class ClassToLeafMap {

    //Can be copied for consumers too
    private final HashMap<Class<?>, RadixLeaf<?>> map =
            new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> RadixLeaf<? super T> put(Map<Class<?>, RadixLeaf<?>> map, Class<T> key, RadixLeaf<? super T> c) {
        return (RadixLeaf<? super T>) map.put(key, c);
    }

    @SuppressWarnings("unchecked")
    public static <T> RadixLeaf<? super T> get(Map<Class<?>, RadixLeaf<?>> map, Class<T> key) {
        return (RadixLeaf<? super T>) map.get(key);
    }
}
