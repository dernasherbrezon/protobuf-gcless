package com.google.code.proto.gcless;

public class GenerateIssue6 {

	public static void main(String[] args) throws Exception {
		
		System.setProperty("generate.list.helpers", "true");
		System.setProperty("generate.chaining", "true");
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug6.proto" });

		
	}
	
}

