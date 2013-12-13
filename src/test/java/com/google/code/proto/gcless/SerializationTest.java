package com.google.code.proto.gcless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.x.protobuf.SyncEnumsPB.SyncEnums.PageTransition;
import com.x.protobuf.TabNavigationPB.TabNavigation;
import com.x.protobuf.TabNavigationPB.TabNavigationSerializer;

import protobuf_gcless_unittest.UnittestProto.ForeignMessage;
import protobuf_gcless_unittest.UnittestProto.TestAllTypes;
import protobuf_gcless_unittest.UnittestProto.TestAllTypesSerializer;

public class SerializationTest {
	
	@Test
	public void testSerializationForIssue2() throws Exception {
		TabNavigation nvigation = new TabNavigation();
		nvigation.setState("123");
		nvigation.setPageTransition(PageTransition.TYPED);
		
		byte[] data = TabNavigationSerializer.serialize(nvigation);
		
		TabNavigation result = TabNavigationSerializer.parseFrom(data);
		
		assertEquals(nvigation.getState(), result.getState());
		assertEquals(nvigation.getPageTransition(), result.getPageTransition());
	}
	
	@Test
	public void testDefaultSerializationOptimizedStreamedDeserialization() throws Exception {
		TestAllTypes message = createSampleMessage();
		byte[] data = TestAllTypesSerializer.serialize(message);
		
		protobuf_unittest.UnittestProto.TestAllTypes result = protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(data);
		
		byte[] defaultSerializationData = result.toByteArray();
		
		TestAllTypes optimizedResult = TestAllTypesSerializer.parseFrom(new ByteArrayInputStream(defaultSerializationData));
		assertDeepEquals(result, optimizedResult);
		assertEquals(2, optimizedResult.getRepeatedForeignMessage().size());
		assertEquals(1, optimizedResult.getRepeatedForeignMessage().get(0).getC());
		assertEquals(2, optimizedResult.getRepeatedForeignMessage().get(1).getC());
		assertEquals(message.getDefaultString(), optimizedResult.getDefaultString());
	}
	
	@Test 
	public void testDefaultSerializationOptimizedDeserialization() throws Exception {
		TestAllTypes message = createSampleMessage();
		byte[] data = TestAllTypesSerializer.serialize(message);
		
		protobuf_unittest.UnittestProto.TestAllTypes result = protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(data);
		
		byte[] defaultSerializationData = result.toByteArray();
		
		TestAllTypes optimizedResult = TestAllTypesSerializer.parseFrom(defaultSerializationData);
		assertDeepEquals(result, optimizedResult);
		assertEquals(2, optimizedResult.getRepeatedForeignMessage().size());
		assertEquals(1, optimizedResult.getRepeatedForeignMessage().get(0).getC());
		assertEquals(2, optimizedResult.getRepeatedForeignMessage().get(1).getC());
	}
	
	@Test
	public void testOptimizedSerializationDeserialization() throws Exception {
		TestAllTypes message = createSampleMessage();

		byte[] data = TestAllTypesSerializer.serialize(message);

		TestAllTypes result = TestAllTypesSerializer.parseFrom(data);
		assertNotNull(result);
		assertDeepEquals(message, result);
	}
	
	@Test
	public void testOptimizedSerializationToStreamedDeserialization() throws Exception {
		TestAllTypes message = createSampleMessage();

		byte[] data = TestAllTypesSerializer.serialize(message);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		TestAllTypes result = TestAllTypesSerializer.parseFrom(bais);
		assertNotNull(result);
		assertDeepEquals(message, result);
	}
	
	@Test
	public void testOptimizedSerializationDefaultDeserialization() throws Exception {

		TestAllTypes message = createSampleMessage();

		byte[] data = TestAllTypesSerializer.serialize(message);

		protobuf_unittest.UnittestProto.TestAllTypes result = protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(data);
		assertNotNull(result);
		assertDeepEquals(result, message);
		assertTrue(result.getUnknownFields().asMap().isEmpty());
	}

	@Test
	public void testOptimizedSerializeToStreamDefaultDeserialization() throws Exception {

		TestAllTypes message = createSampleMessage();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TestAllTypesSerializer.serialize(message, baos);
		
		protobuf_unittest.UnittestProto.TestAllTypes result = protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(baos.toByteArray());
		assertNotNull(result);
		assertDeepEquals(result, message);
		assertTrue(result.getUnknownFields().asMap().isEmpty());
	}

	public static void main(String[] args) throws Exception {
		
		long times = 1000000;

		long start;
		
		TestAllTypes message = createSampleMessage();
		byte[] data = TestAllTypesSerializer.serialize(message);

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			TestAllTypesSerializer.serialize(message);
		}
		System.out.println(" * Optimized version(serialize): " + (System.currentTimeMillis() - start));
		protobuf_unittest.UnittestProto.TestAllTypes result = protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(data);

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			result.toByteArray();
		}
		System.out.println(" * Default version(serialize): " + (System.currentTimeMillis() - start));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length + 10); //little trick to remove stream io issues and measure pure serialization power.
		
		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			TestAllTypesSerializer.serialize(message, baos);
			baos.reset();
		}
		System.out.println(" * Optimized streamed version(serialize): " + (System.currentTimeMillis() - start));
		

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			result.writeTo(baos);
			baos.reset();
		}
		System.out.println(" * Default streamed version(serialize): " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			TestAllTypesSerializer.parseFrom(data);
		}
		System.out.println(" * Optimized version(de-serialize): " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(data);
		}
		System.out.println(" * Default version(de-serialize): " + (System.currentTimeMillis() - start));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			TestAllTypesSerializer.parseFrom(bais);
			bais.reset();
		}
		System.out.println(" * Optimized streamed version(de-serialize): " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			protobuf_unittest.UnittestProto.TestAllTypes.parseFrom(bais);
			bais.reset();
		}
		System.out.println(" * Default streamed version(de-serialize): " + (System.currentTimeMillis() - start));
	}
	
	private static void assertDeepEquals(protobuf_unittest.UnittestProto.TestAllTypes defaultImpl, TestAllTypes optimized) {
		assertEquals(defaultImpl.getOptionalInt32(), optimized.getOptionalInt32());
		assertEquals(defaultImpl.getOptionalInt64(), optimized.getOptionalInt64());
		assertEquals(defaultImpl.getOptionalUint32(), optimized.getOptionalUint32());
		assertEquals(defaultImpl.getOptionalUint64(), optimized.getOptionalUint64());
		assertEquals(defaultImpl.getOptionalSint32(), optimized.getOptionalSint32());
		assertEquals(defaultImpl.getOptionalSint64(), optimized.getOptionalSint64());
		assertEquals(defaultImpl.getOptionalFixed32(), optimized.getOptionalFixed32());
		assertEquals(defaultImpl.getOptionalFixed64(), optimized.getOptionalFixed64());
		assertEquals(defaultImpl.getOptionalSfixed32(), optimized.getOptionalSfixed32());
		assertEquals(defaultImpl.getOptionalSfixed64(), optimized.getOptionalSfixed64());
		assertEquals(defaultImpl.getOptionalFloat(), optimized.getOptionalFloat(), 0.0);
		assertEquals(defaultImpl.getOptionalDouble(), optimized.getOptionalDouble(), 0.0);
		assertEquals(defaultImpl.getOptionalBool(), optimized.getOptionalBool());
		assertEquals(defaultImpl.getOptionalString(), optimized.getOptionalString());
		assertTrue(Arrays.equals(defaultImpl.getOptionalBytes().toByteArray(), optimized.getOptionalBytes()));
		assertEquals(defaultImpl.getOptionalStringPiece(), optimized.getOptionalStringPiece());
		assertEquals(defaultImpl.getOptionalCord(), optimized.getOptionalCord());
		assertTrue(Arrays.equals(defaultImpl.getRepeatedInt32List().toArray(), optimized.getRepeatedInt32().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedInt64List().toArray(), optimized.getRepeatedInt64().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedUint32List().toArray(), optimized.getRepeatedUint32().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedUint64List().toArray(), optimized.getRepeatedUint64().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedSint32List().toArray(), optimized.getRepeatedSint32().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedSint64List().toArray(), optimized.getRepeatedSint64().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedFixed32List().toArray(), optimized.getRepeatedFixed32().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedFixed64List().toArray(), optimized.getRepeatedFixed64().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedSfixed32List().toArray(), optimized.getRepeatedSfixed32().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedSfixed64List().toArray(), optimized.getRepeatedSfixed64().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedFloatList().toArray(), optimized.getRepeatedFloat().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedDoubleList().toArray(), optimized.getRepeatedDouble().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedBoolList().toArray(), optimized.getRepeatedBool().toArray()));
		assertTrue(Arrays.equals(defaultImpl.getRepeatedStringList().toArray(), optimized.getRepeatedString().toArray()));
		assertEquals(defaultImpl.getRepeatedForeignMessageCount(), optimized.getRepeatedForeignMessage().size());
		assertEquals(defaultImpl.getRepeatedForeignMessage(0).getC(), optimized.getRepeatedForeignMessage().get(0).getC());
		assertEquals(defaultImpl.getRepeatedForeignMessage(1).getC(), optimized.getRepeatedForeignMessage().get(1).getC());
	}
	
	private static void assertDeepEquals(TestAllTypes original, TestAllTypes result) {
		assertEquals(original.getOptionalInt32(), result.getOptionalInt32());
		assertEquals(original.getOptionalInt64(), result.getOptionalInt64());
		assertEquals(original.getOptionalUint32(), result.getOptionalUint32());
		assertEquals(original.getOptionalUint64(), result.getOptionalUint64());
		assertEquals(original.getOptionalSint32(), result.getOptionalSint32());
		assertEquals(original.getOptionalSint64(), result.getOptionalSint64());
		assertEquals(original.getOptionalFixed32(), result.getOptionalFixed32());
		assertEquals(original.getOptionalFixed64(), result.getOptionalFixed64());
		assertEquals(original.getOptionalSfixed32(), result.getOptionalSfixed32());
		assertEquals(original.getOptionalSfixed64(), result.getOptionalSfixed64());
		assertEquals(original.getOptionalFloat(), result.getOptionalFloat(), 0.0);
		assertEquals(original.getOptionalDouble(), result.getOptionalDouble(), 0.0);
		assertEquals(original.getOptionalBool(), result.getOptionalBool());
		assertEquals(original.getOptionalString(), result.getOptionalString());
		assertTrue(Arrays.equals(original.getOptionalBytes(), result.getOptionalBytes()));
		assertEquals(original.getOptionalStringPiece(), result.getOptionalStringPiece());
		assertEquals(original.getOptionalCord(), result.getOptionalCord());
		assertTrue(Arrays.equals(original.getRepeatedInt32().toArray(), result.getRepeatedInt32().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedInt64().toArray(), result.getRepeatedInt64().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedUint32().toArray(), result.getRepeatedUint32().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedUint64().toArray(), result.getRepeatedUint64().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedSint32().toArray(), result.getRepeatedSint32().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedSint64().toArray(), result.getRepeatedSint64().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedFixed32().toArray(), result.getRepeatedFixed32().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedFixed64().toArray(), result.getRepeatedFixed64().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedSfixed32().toArray(), result.getRepeatedSfixed32().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedSfixed64().toArray(), result.getRepeatedSfixed64().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedFloat().toArray(), result.getRepeatedFloat().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedDouble().toArray(), result.getRepeatedDouble().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedBool().toArray(), result.getRepeatedBool().toArray()));
		assertTrue(Arrays.equals(original.getRepeatedString().toArray(), result.getRepeatedString().toArray()));
		assertEquals(original.getRepeatedForeignMessage().size(), result.getRepeatedForeignMessage().size());
		assertEquals(original.getRepeatedForeignMessage().get(0).getC(), result.getRepeatedForeignMessage().get(0).getC());
		assertEquals(original.getRepeatedForeignMessage().get(1).getC(), result.getRepeatedForeignMessage().get(1).getC());
	}

	private static TestAllTypes createSampleMessage() {
		TestAllTypes message = new TestAllTypes();
		message.setOptionalInt32(1);
		message.setOptionalInt64(1l);
		message.setOptionalUint32(1);
		message.setOptionalUint64(1l);
		message.setOptionalSint32(1);
		message.setOptionalSint64(1l);
		message.setOptionalFixed32(1);
		message.setOptionalFixed64(1l);
		message.setOptionalSfixed32(1);
		message.setOptionalSfixed64(1l);
		message.setOptionalFloat(1.0f);
		message.setOptionalDouble(1.1);
		message.setOptionalBool(true);
		message.setOptionalString("123");
		message.setOptionalBytes(new byte[] {(byte) 1, (byte) 2});
		message.setOptionalStringPiece("123");
		message.setOptionalCord("123");
		List<Integer> valuesRepeated_int32 = new ArrayList<Integer>();
		valuesRepeated_int32.add(1);
		valuesRepeated_int32.add(1);
		message.setRepeatedInt32(valuesRepeated_int32);
		List<Long> valuesRepeated_int64 = new ArrayList<Long>();
		valuesRepeated_int64.add(1l);
		valuesRepeated_int64.add(1l);
		message.setRepeatedInt64(valuesRepeated_int64);
		List<Integer> valuesRepeated_uint32 = new ArrayList<Integer>();
		valuesRepeated_uint32.add(1);
		valuesRepeated_uint32.add(1);
		message.setRepeatedUint32(valuesRepeated_uint32);
		List<Long> valuesRepeated_uint64 = new ArrayList<Long>();
		valuesRepeated_uint64.add(1l);
		valuesRepeated_uint64.add(1l);
		message.setRepeatedUint64(valuesRepeated_uint64);
		List<Integer> valuesRepeated_sint32 = new ArrayList<Integer>();
		valuesRepeated_sint32.add(1);
		valuesRepeated_sint32.add(1);
		message.setRepeatedSint32(valuesRepeated_sint32);
		List<Long> valuesRepeated_sint64 = new ArrayList<Long>();
		valuesRepeated_sint64.add(1l);
		valuesRepeated_sint64.add(1l);
		message.setRepeatedSint64(valuesRepeated_sint64);
		List<Integer> valuesRepeated_fixed32 = new ArrayList<Integer>();
		valuesRepeated_fixed32.add(1);
		valuesRepeated_fixed32.add(1);
		message.setRepeatedFixed32(valuesRepeated_fixed32);
		List<Long> valuesRepeated_fixed64 = new ArrayList<Long>();
		valuesRepeated_fixed64.add(1l);
		valuesRepeated_fixed64.add(1l);
		message.setRepeatedFixed64(valuesRepeated_fixed64);
		List<Integer> valuesRepeated_sfixed32 = new ArrayList<Integer>();
		valuesRepeated_sfixed32.add(1);
		valuesRepeated_sfixed32.add(1);
		message.setRepeatedSfixed32(valuesRepeated_sfixed32);
		List<Long> valuesRepeated_sfixed64 = new ArrayList<Long>();
		valuesRepeated_sfixed64.add(1l);
		valuesRepeated_sfixed64.add(1l);
		message.setRepeatedSfixed64(valuesRepeated_sfixed64);
		List<Float> valuesRepeated_float = new ArrayList<Float>();
		valuesRepeated_float.add(1.0f);
		valuesRepeated_float.add(1.0f);
		message.setRepeatedFloat(valuesRepeated_float);
		List<Double> valuesRepeated_double = new ArrayList<Double>();
		valuesRepeated_double.add(1.1);
		valuesRepeated_double.add(1.1);
		message.setRepeatedDouble(valuesRepeated_double);
		List<Boolean> valuesRepeated_bool = new ArrayList<Boolean>();
		valuesRepeated_bool.add(true);
		valuesRepeated_bool.add(true);
		message.setRepeatedBool(valuesRepeated_bool);
		List<String> valuesRepeated_string = new ArrayList<String>();
		valuesRepeated_string.add("123");
		valuesRepeated_string.add("456");
		message.setRepeatedString(valuesRepeated_string);
		message.setRepeatedBytes(new byte[] {(byte) 1, (byte) 2});
		List<protobuf_gcless_unittest.UnittestProto.TestAllTypes.NestedMessage> valuesRepeated_nested_message = new ArrayList<protobuf_gcless_unittest.UnittestProto.TestAllTypes.NestedMessage>();
		message.setRepeatedNestedMessage(valuesRepeated_nested_message);
		List<protobuf_gcless_unittest.UnittestProto.ForeignMessage> valuesRepeated_foreign_message = new ArrayList<protobuf_gcless_unittest.UnittestProto.ForeignMessage>();
		ForeignMessage foreignMessage1 = new ForeignMessage();
		foreignMessage1.setC(1);
		ForeignMessage foreignMessage2 = new ForeignMessage();
		foreignMessage2.setC(2);
		valuesRepeated_foreign_message.add(foreignMessage1);
		valuesRepeated_foreign_message.add(foreignMessage2);
		message.setRepeatedForeignMessage(valuesRepeated_foreign_message);
		List<protobuf_gcless_import.ImportMessage> valuesRepeated_import_message = new ArrayList<protobuf_gcless_import.ImportMessage>();
		message.setRepeatedImportMessage(valuesRepeated_import_message);
		List<protobuf_gcless_unittest.UnittestProto.TestAllTypes.NestedEnum> valuesRepeated_nested_enum = new ArrayList<protobuf_gcless_unittest.UnittestProto.TestAllTypes.NestedEnum>();
		message.setRepeatedNestedEnum(valuesRepeated_nested_enum);
		List<protobuf_gcless_unittest.UnittestProto.ForeignEnum> valuesRepeated_foreign_enum = new ArrayList<protobuf_gcless_unittest.UnittestProto.ForeignEnum>();
		message.setRepeatedForeignEnum(valuesRepeated_foreign_enum);
		List<protobuf_gcless_import.ImportEnum> valuesRepeated_import_enum = new ArrayList<protobuf_gcless_import.ImportEnum>();
		message.setRepeatedImportEnum(valuesRepeated_import_enum);
		List<String> valuesRepeated_string_piece = new ArrayList<String>();
		valuesRepeated_string_piece.add("123");
		valuesRepeated_string_piece.add("123");
		message.setRepeatedStringPiece(valuesRepeated_string_piece);
		List<String> valuesRepeated_cord = new ArrayList<String>();
		valuesRepeated_cord.add("123");
		valuesRepeated_cord.add("123");
		message.setRepeatedCord(valuesRepeated_cord);
		message.setDefaultInt32(1);
		message.setDefaultInt64(1l);
		message.setDefaultUint32(1);
		message.setDefaultUint64(1l);
		message.setDefaultSint32(1);
		message.setDefaultSint64(1l);
		message.setDefaultFixed32(1);
		message.setDefaultFixed64(1l);
		message.setDefaultSfixed32(1);
		message.setDefaultSfixed64(1l);
		message.setDefaultFloat(1.0f);
		message.setDefaultDouble(1.1);
		message.setDefaultBool(true);
		message.setDefaultString("123");
		message.setDefaultBytes(new byte[] {(byte) 1, (byte) 2});
		message.setDefaultStringPiece("123");
		message.setDefaultCord("123");
		return message;
	}

}

