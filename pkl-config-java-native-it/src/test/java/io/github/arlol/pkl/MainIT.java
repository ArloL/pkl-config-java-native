package io.github.arlol.pkl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MainIT {

	@Test
	void pklEvaluatesInlineModule() throws Exception {
		String path = System.getProperty("nativeBinaryPath");
		Process proc = new ProcessBuilder(path).start();
		String out = new String(proc.getInputStream().readAllBytes());
		assertEquals(0, proc.waitFor());
		assertEquals("Hello, pkl-maven native image!", out.strip());
	}

}
