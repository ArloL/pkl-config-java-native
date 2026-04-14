package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.TargetClass;
import java.util.Map;

@SuppressWarnings("unused")
@TargetClass(className = "com.oracle.truffle.api.impl.ThreadLocalHandshake")
public final class ThreadLocalHandshakeTarget {

	@Alias
	@RecomputeFieldValue(
			kind = Kind.NewInstance,
			declClassName = "java.util.HashMap"
	)
	static Map<?, ?> SAFEPOINTS;

}
