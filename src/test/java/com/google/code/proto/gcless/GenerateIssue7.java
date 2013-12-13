package com.google.code.proto.gcless;

public class GenerateIssue7 {

	public static void main(String[] args) throws Exception {
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug7.proto" });
		MemlessGenerator.main(new String[] { "src/test/java", "src/test/resources/bug7-unknown.proto" });
	}
	
}

