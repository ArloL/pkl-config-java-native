package io.github.arlol.pkl.svm;

import com.oracle.svm.core.annotate.TargetClass;

/**
 * Makes non-public class PolyglotThreadInfo usable in
 * PolyglotContextImplTarget.
 */
@TargetClass(className = "com.oracle.truffle.polyglot.PolyglotThreadInfo")
public final class PolyglotThreadInfoTarget {
}
