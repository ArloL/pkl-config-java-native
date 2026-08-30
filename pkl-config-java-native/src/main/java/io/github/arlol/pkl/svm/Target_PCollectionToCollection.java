package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

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
	@SuppressFBWarnings(
			value = "UPM_UNCALLED_PRIVATE_METHOD",
			justification = "This replaces a method in another class and that calls this method"
	)
	private <T> Optional<Function<Integer, Collection<T>>> createInstantiator(
			Class<T> clazz
	) {
		return ConstructorInstantiators.forCollection(clazz);
	}

}
