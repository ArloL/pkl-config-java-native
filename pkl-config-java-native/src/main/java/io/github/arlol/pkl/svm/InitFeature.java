package io.github.arlol.pkl.svm;

import org.graalvm.nativeimage.hosted.Feature;
import org.pkl.core.runtime.BaseModule;

/**
 * Registered via --features in native-image.properties.
 *
 * Enforces that {@link BaseModule#getModule()}'s static initializer completes
 * before depending static initializers are invoked. Necessary to avoid
 * deadlocks in native-image's multi-threaded execution of static initializers.
 *
 * Copied from
 * pkl-commons-cli/src/svm/java/org/pkl/commons/cli/svm/InitFeature.java.
 */
@SuppressWarnings({ "unused", "ResultOfMethodCallIgnored" })
public final class InitFeature implements Feature {

	public void duringSetup(DuringSetupAccess access) {
		BaseModule.getModule();
	}

}
