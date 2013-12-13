package com.google.code.proto.gcless;

public class GenerateIssue3 {

	public static void main(String[] args) throws Exception {
		System.setProperty("generate.static.fields", "true");
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug3.proto" });
	}

}
