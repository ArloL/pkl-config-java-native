package io.github.arlol.pkl.svm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.pkl.config.java.mapper.ConversionException;

class ConstructorInstantiatorsTest {

	private static final int LENGTH = 10;

	static class RecordingList extends ArrayList<Object> {

		private static final long serialVersionUID = 1L;

		final String constructor;
		final int capacity;
		final float loadFactor;

		RecordingList(int capacity, float loadFactor) {
			this.constructor = "capacityAndLoadFactor";
			this.capacity = capacity;
			this.loadFactor = loadFactor;
		}

		RecordingList(int size) {
			this.constructor = "size";
			this.capacity = size;
			this.loadFactor = 0;
		}

		RecordingList() {
			this.constructor = "default";
			this.capacity = 0;
			this.loadFactor = 0;
		}

	}

	static class SizedList extends ArrayList<Object> {

		private static final long serialVersionUID = 1L;

		final String constructor;

		SizedList(int size) {
			this.constructor = "size";
		}

		SizedList() {
			this.constructor = "default";
		}

	}

	static class SizedMap extends HashMap<Object, Object> {

		private static final long serialVersionUID = 1L;

		final String constructor;

		SizedMap(int size) {
			this.constructor = "size";
		}

		SizedMap() {
			this.constructor = "default";
		}

	}

	static class ThrowingList extends ArrayList<Object> {

		private static final long serialVersionUID = 1L;

		ThrowingList() {
			throw new IllegalStateException("boom");
		}

	}

	@Test
	void collectionPrefersCapacityAndLoadFactorConstructor() {
		RecordingList list = (RecordingList) instantiate(
				ConstructorInstantiators.forCollection(RecordingList.class)
		);

		assertEquals("capacityAndLoadFactor", list.constructor);
		assertEquals(14, list.capacity);
		assertEquals(.75f, list.loadFactor);
	}

	@Test
	void collectionFallsBackToSizeConstructor() {
		SizedList list = (SizedList) instantiate(
				ConstructorInstantiators.forCollection(SizedList.class)
		);

		assertEquals("size", list.constructor);
	}

	@Test
	void collectionFallsBackToDefaultConstructor() {
		Object instance = instantiate(
				ConstructorInstantiators
						.forCollection(CopyOnWriteArrayList.class)
		);

		assertInstanceOf(CopyOnWriteArrayList.class, instance);
		assertTrue(((Collection<?>) instance).isEmpty());
	}

	@Test
	void hashSetIsInstantiated() {
		Object instance = instantiate(
				ConstructorInstantiators.forCollection(HashSet.class)
		);

		assertInstanceOf(HashSet.class, instance);
		assertTrue(((Collection<?>) instance).isEmpty());
	}

	@Test
	void collectionWithoutUsableConstructorIsEmpty() {
		assertTrue(
				ConstructorInstantiators.forCollection(List.class).isEmpty()
		);
	}

	@Test
	void mapPrefersCapacityAndLoadFactorConstructor() {
		Object instance = instantiate(
				ConstructorInstantiators.forMap(HashMap.class)
		);

		assertInstanceOf(HashMap.class, instance);
		assertTrue(((Map<?, ?>) instance).isEmpty());
	}

	@Test
	void mapSkipsSizeConstructor() {
		SizedMap map = (SizedMap) instantiate(
				ConstructorInstantiators.forMap(SizedMap.class)
		);

		assertEquals("default", map.constructor);
	}

	@Test
	void mapFallsBackToDefaultConstructor() {
		Object instance = instantiate(
				ConstructorInstantiators.forMap(TreeMap.class)
		);

		assertInstanceOf(TreeMap.class, instance);
		assertTrue(((Map<?, ?>) instance).isEmpty());
	}

	@Test
	void mapWithoutUsableConstructorIsEmpty() {
		assertTrue(ConstructorInstantiators.forMap(Map.class).isEmpty());
	}

	@Test
	void failingConstructorIsWrappedInConversionException() {
		Function<Integer, Collection<ThrowingList>> instantiator = ConstructorInstantiators
				.forCollection(ThrowingList.class)
				.orElseThrow();

		ConversionException exception = assertThrows(
				ConversionException.class,
				() -> instantiator.apply(LENGTH)
		);

		assertEquals(
				"Error invoking constructor of class `" + ThrowingList.class
						+ "`.",
				exception.getMessage()
		);
		assertInstanceOf(
				IllegalStateException.class,
				exception.getCause().getCause()
		);
	}

	private static Object instantiate(
			Optional<? extends Function<Integer, ?>> instantiator
	) {
		return instantiator.orElseThrow().apply(LENGTH);
	}

}
