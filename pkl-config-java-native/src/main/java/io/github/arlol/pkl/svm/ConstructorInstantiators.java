package io.github.arlol.pkl.svm;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.pkl.config.java.mapper.ConversionException;

/**
 * Constructor lookup shared by the {@code @TargetClass} substitutions in this
 * package.
 *
 * <p>
 * It lives outside them because native-image only ever processes a
 * {@code @TargetClass} class at image build time: its bytecode is never loaded
 * on HotSpot, so nothing inside it can be exercised by a JVM test.
 */
final class ConstructorInstantiators {

	private static final String ERROR_INVOKING_CONSTRUCTOR = "Error invoking constructor of class `%s`.";

	private static final float LOAD_FACTOR = .75f;

	/**
	 * Tries the capacity and load factor constructor (e.g. {@code HashSet}),
	 * then the size constructor (e.g. {@code ArrayList}), then the default
	 * constructor.
	 */
	static <T> Optional<Function<Integer, Collection<T>>> forCollection(
			Class<T> clazz
	) {
		return instantiator(clazz, true);
	}

	/**
	 * Tries the capacity and load factor constructor (e.g. {@code HashMap}),
	 * then the default constructor. Maps have no size constructor.
	 */
	static <K, V> Optional<Function<Integer, Map<K, V>>> forMap(
			Class<?> clazz
	) {
		return instantiator(clazz, false);
	}

	private static <R> Optional<Function<Integer, R>> instantiator(
			Class<?> clazz,
			boolean withSizeConstructor
	) {
		Optional<Function<Integer, R>> instantiator = declaredConstructor(
				clazz,
				int.class,
				float.class
		).map(
				constructor -> length -> newInstance(
						clazz,
						constructor,
						capacity(length),
						LOAD_FACTOR
				)
		);
		if (instantiator.isEmpty() && withSizeConstructor) {
			instantiator = declaredConstructor(clazz, int.class).map(
					constructor -> length -> newInstance(
							clazz,
							constructor,
							length
					)
			);
		}
		if (instantiator.isEmpty()) {
			instantiator = declaredConstructor(clazz).map(
					constructor -> length -> newInstance(clazz, constructor)
			);
		}
		return instantiator;
	}

	private static int capacity(int length) {
		return (int) (length / LOAD_FACTOR) + 1;
	}

	private static Optional<Constructor<?>> declaredConstructor(
			Class<?> clazz,
			Class<?>... parameterTypes
	) {
		try {
			Constructor<?> constructor = clazz
					.getDeclaredConstructor(parameterTypes);
			constructor.setAccessible(true);
			return Optional.of(constructor);
		} catch (NoSuchMethodException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	private static <R> R newInstance(
			Class<?> clazz,
			Constructor<?> constructor,
			Object... arguments
	) {
		try {
			return (R) constructor.newInstance(arguments);
		} catch (Throwable t) {
			throw new ConversionException(
					ERROR_INVOKING_CONSTRUCTOR.formatted(clazz),
					t
			);
		}
	}

	private ConstructorInstantiators() {
	}

}
