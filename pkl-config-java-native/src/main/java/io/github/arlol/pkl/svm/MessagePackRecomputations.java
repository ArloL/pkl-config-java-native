package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * native-image can't determine how calls to
 * {@link sun.misc.Unsafe#arrayBaseOffset(Class)} affect static fields in
 * msgpack's {@code MessageBuffer}.
 *
 * Copied from
 * pkl-commons-cli/src/svm/java/org/pkl/commons/cli/svm/MessagePackRecomputations.java.
 */
@SuppressWarnings("unused")
@TargetClass(className = "org.msgpack.core.buffer.MessageBuffer")
final class MessagePackRecomputations {

	@Alias
	@RecomputeFieldValue(
			kind = RecomputeFieldValue.Kind.ArrayBaseOffset,
			declClass = byte[].class
	)
	static int ARRAY_BYTE_BASE_OFFSET;

}
