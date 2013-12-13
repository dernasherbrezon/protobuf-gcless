package com.google.code.proto.gcless;

public class GenerateIssue5 {

	public static void main(String[] args) throws Exception {
		
		System.setProperty("generate.list.helpers", "true");
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug5.proto" });
		System.setProperty("interface.based", "true");
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug5-interface.proto" });

		
	}
	
}
