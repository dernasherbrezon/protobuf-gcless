package com.google.code.proto.gcless;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MemlessParserTest {

	@Test
	public void test1Issue4() {
		assertEquals("DataTypeId", MemlessParser.convertNameToJavabean("data_type_id"));
	}
	
	@Test
	public void test2Issue4() {
		assertEquals("DataType", MemlessParser.convertNameToJavabean("data_type_"));
	}
	
	@Test
	public void test3Issue4() {
		assertEquals("", MemlessParser.convertNameToJavabean(""));
	}

	@Test
	public void test4Issue4() {
		assertEquals("DType", MemlessParser.convertNameToJavabean("d_type"));
	}
}
