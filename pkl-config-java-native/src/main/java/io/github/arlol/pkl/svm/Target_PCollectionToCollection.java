package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import org.pkl.config.java.mapper.ConversionException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Substitutes {@code PCollectionToCollection.createInstantiator()} to avoid the
 * GraalVM native-image bug where {@code MethodHandles.Lookup.findConstructor()}
 * fails when the {@code Class<?>} argument is obtained via a reflection path
 * (e.g. {@code Reflection.toRawType()}) rather than a compile-time class
 * literal.
 *
 * <p>
 * Root cause: {@code PCollectionToCollection} holds a
 * {@code static final Lookup} initialised at build time via
 * {@code --initialize-at-build-time=org.pkl}. At runtime in the native image
 * the {@code Class<?>} objects returned by {@code Reflection.getExactSubtype()}
 * / {@code Reflection.toRawType()} have a different object identity than the
 * build-time interned class literals, and {@code Lookup.findConstructor()} uses
 * {@code ==} comparison internally, causing it to throw
 * {@code NoSuchMethodException} for every constructor probe.
 *
 * <p>
 * {@code Class.getDeclaredConstructor()} does not have this identity comparison
 * issue and is a correct, equivalent replacement.
 */
@SuppressWarnings("unused")
@TargetClass(className = "org.pkl.config.java.mapper.PCollectionToCollection")
final class Target_PCollectionToCollection {

	@Substitute
	@SuppressWarnings("unchecked")
	@SuppressFBWarnings(
			value = "UPM_UNCALLED_PRIVATE_METHOD",
			justification = "This replaces a method in another class and that calls this method"
	)
	private <T> Optional<Function<Integer, Collection<T>>> createInstantiator(
			Class<T> clazz
	) {
		try {
			// constructor with capacity and load factor parameters, e.g.
			// HashSet
			Constructor<T> ctor2 = clazz
					.getDeclaredConstructor(int.class, float.class);
			ctor2.setAccessible(true);
			return Optional.of(length -> {
				try {
					return (Collection<T>) ctor2
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
				// constructor with size parameter, e.g. ArrayList
				Constructor<T> ctor1 = clazz.getDeclaredConstructor(int.class);
				ctor1.setAccessible(true);
				return Optional.of(length -> {
					try {
						return (Collection<T>) ctor1.newInstance(length);
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
			} catch (NoSuchMethodException e1) {
				try {
					// default constructor
					Constructor<T> ctor0 = clazz.getDeclaredConstructor();
					ctor0.setAccessible(true);
					return Optional.of(length -> {
						try {
							return (Collection<T>) ctor0.newInstance();
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

}
