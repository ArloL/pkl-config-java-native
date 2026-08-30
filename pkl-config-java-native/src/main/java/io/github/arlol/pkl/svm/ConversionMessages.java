package io.github.arlol.pkl.svm;

/**
 * Messages shared by the {@code @TargetClass} substitutions in this package.
 * They live outside the substitution classes because native-image requires
 * every field of a {@code @TargetClass} class to be annotated with
 * {@code @Alias}, {@code @Inject} or {@code @Delete}.
 */
final class ConversionMessages {

	static final String ERROR_INVOKING_CONSTRUCTOR = "Error invoking constructor of class `%s`.";

	private ConversionMessages() {
	}

}
