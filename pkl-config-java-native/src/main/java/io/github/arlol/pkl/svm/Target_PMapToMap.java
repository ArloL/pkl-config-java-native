package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
	@SuppressFBWarnings(
			value = "UPM_UNCALLED_PRIVATE_METHOD",
			justification = "This replaces a method in another class and that calls this method"
	)
	private <K, V> Optional<Function<Integer, Map<K, V>>> createInstantiator(
			Class<?> clazz
	) {
		return ConstructorInstantiators.forMap(clazz);
	}

}
