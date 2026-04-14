package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.TargetClass;
import java.util.Map;

/**
 * Prevents the native-image build error "Detected a started Thread in the image
 * heap".
 *
 * The cause is the use of {@link org.graalvm.polyglot.Context} in the
 * statically reachable class {@link org.pkl.core.runtime.StdLibModule}.
 *
 * Copied from
 * pkl-commons-cli/src/svm/java/org/pkl/commons/cli/svm/PolyglotContextImplTarget.java.
 *
 * Note: pkl-cli uses onlyWith={TruffleFeature.IsEnabled.class} because Gradle
 * correctly handles truffle-runtime's ForceOnModulePath directive, activating
 * TruffleFeature via --macro:truffle-svm. The native-maven-plugin passes all
 * jars via -cp and cannot honour ForceOnModulePath, so TruffleFeature is never
 * activated. The substitution is therefore unconditional here.
 */
@SuppressWarnings({ "unused", "ClassName" })
@TargetClass(className = "com.oracle.truffle.polyglot.PolyglotContextImpl")
public final class PolyglotContextImplTarget {

	@Alias
	@RecomputeFieldValue(
			kind = Kind.NewInstance,
			declClassName = "java.util.HashMap"
	)
	public Map<?, ?> threads;

	@Alias
	@RecomputeFieldValue(kind = Kind.Reset)
	public WeakAssumedValueTarget singleThreadValue;

	@Alias
	@RecomputeFieldValue(kind = Kind.Reset)
	public PolyglotThreadInfoTarget cachedThreadInfo;

}
