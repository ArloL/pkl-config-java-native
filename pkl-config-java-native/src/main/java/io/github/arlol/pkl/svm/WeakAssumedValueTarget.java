package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.TargetClass;

/**
 * Makes non-public class WeakAssumedValue usable in PolyglotContextImplTarget.
 */
@TargetClass(className = "com.oracle.truffle.polyglot.WeakAssumedValue")
public final class WeakAssumedValueTarget {
}
