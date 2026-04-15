package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.pkl.config.java.mapper.ConversionException;

/**
 * Substitutes {@code PMapToMap.createInstantiator()} for the same reason as
 * {@link Target_PCollectionToCollection}:
 * {@code MethodHandles.Lookup.findConstructor()} fails in native image when the
 * {@code Class<?>} is obtained via a reflection path rather than a compile-time
 * class literal.
 */
@SuppressWarnings("unused")
@TargetClass(className = "org.pkl.config.java.mapper.PMapToMap")
final class Target_PMapToMap {

	@Substitute
	@SuppressWarnings("unchecked")
	@SuppressFBWarnings(
			value = "UPM_UNCALLED_PRIVATE_METHOD",
			justification = "This replaces a method in another class and that calls this method"
	)
	private <K, V> Optional<Function<Integer, Map<K, V>>> createInstantiator(
			Class<?> clazz
	) {
		try {
			// constructor with capacity and load factor arguments, e.g. HashMap
			Constructor<?> ctor2 = clazz
					.getDeclaredConstructor(int.class, float.class);
			ctor2.setAccessible(true);
			return Optional.of(length -> {
				try {
					return (Map<K, V>) ctor2
							.newInstance((int) (length / .75f) + 1, .75f);
				} catch (Throwable t) {
					throw new ConversionException(
							String.format(
									"Error invoking constructor of class `%s`.",
									clazz
							),
							t
					);
				}
			});
		} catch (NoSuchMethodException e2) {
			try {
				// default constructor
				Constructor<?> ctor0 = clazz.getDeclaredConstructor();
				ctor0.setAccessible(true);
				return Optional.of(length -> {
					try {
						return (Map<K, V>) ctor0.newInstance();
					} catch (Throwable t) {
						throw new ConversionException(
								String.format(
										"Error invoking constructor of class `%s`.",
										clazz
								),
								t
						);
					}
				});
			} catch (NoSuchMethodException e0) {
				return Optional.empty();
			}
		}
	}

}
