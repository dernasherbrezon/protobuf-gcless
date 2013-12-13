package com.google.code.proto.gcless;

public class GenerateIssue10 {

	public static void main(String[] args) throws Exception {
		
		System.setProperty("generate.tostring", "true");
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug10.proto" });

		
	}
	
}

