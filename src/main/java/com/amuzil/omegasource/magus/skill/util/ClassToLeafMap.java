package com.amuzil.omegasource.magus.skill.util;

import com.amuzil.omegasource.magus.radix.Leaf;

import java.util.HashMap;
import java.util.Map;

public class ClassToLeafMap {

	//Can be copied for consumers too
	private final HashMap<Class<?>, Leaf<?>> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> Leaf<? super T> put(Map<Class<?>, Leaf<?>> map, Class<T> key, Leaf<? super T> c) {
		return (Leaf<? super T>) map.put(key, c);
	}

	@SuppressWarnings("unchecked")
	public static <T> Leaf<? super T> get(Map<Class<?>, Leaf<?>> map, Class<T> key) {
		return (Leaf<? super T>) map.get(key);
	}
}
