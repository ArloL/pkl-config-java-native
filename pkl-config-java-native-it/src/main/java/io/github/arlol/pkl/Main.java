package io.github.arlol.pkl;

import org.pkl.config.java.ConfigEvaluator;
import org.pkl.core.ModuleSource;

public class Main {

	public static void main(String[] args) {
		try (var evaluator = ConfigEvaluator.preconfigured()) {
			var root = evaluator.evaluate(ModuleSource.text("""
					greeting = "Hello"
					subject = "pkl-maven native image"
					"""));
			String greeting = root.get("greeting").as(String.class);
			String subject = root.get("subject").as(String.class);
			System.out.println(greeting + ", " + subject + "!");
		}
	}

}
