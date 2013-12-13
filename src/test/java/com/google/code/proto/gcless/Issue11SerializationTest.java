package com.google.code.proto.gcless;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.protobuf.Bug11;

public class Issue11SerializationTest {

	@Test
	public void testSerializeDeserialize() throws Exception {
		Bug11.A message = getMessage();

		byte[] data = Bug11.ASerializer.serialize(message);
		Bug11.A result = Bug11.ASerializer.parseFrom(data);
		assertNotNull(result);

		assertMessagesEqual(message, result);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bug11.ASerializer.serialize(message, baos);
		result = Bug11.ASerializer.parseFrom(new ByteArrayInputStream(baos.toByteArray()));

		assertMessagesEqual(message, result);
	}

	private static Bug11.A getMessage() {
		Bug11.B.Result r1 = new Bug11.B.Result();
		r1.setUrl("u1");
		Bug11.B.Result r2 = new Bug11.B.Result();
		r2.setUrl("u2");
		List<Bug11.B.Result> results = new ArrayList<Bug11.B.Result>();
		results.add(r1);
		results.add(r2);

		Bug11.C c1 = new Bug11.C();
		c1.setVal("test");

		Bug11.B b1 = new Bug11.B();
		b1.setVal("1");
		b1.setCval(c1);
		b1.setResult(results);
		Bug11.B b2 = new Bug11.B();
		b2.setVal("2");
		b2.setCval(c1);
		b2.setResult(results);
		List<Bug11.B> entry = new ArrayList<Bug11.B>();
		entry.add(b1);
		entry.add(b2);
		Bug11.A message = new Bug11.A();
		message.setEntry2(12);
		message.setEntry(entry);
		return message;
	}

	private static void assertMessagesEqual(Bug11.A gcless, Bug11.A orig) {
		assertEquals(gcless.getEntry2(), orig.getEntry2());
		assertEquals(gcless.getEntry().size(), orig.getEntry().size());
		for (int i = 0; i < gcless.getEntry().size(); i++) {
			Bug11.B expected = gcless.getEntry().get(i);
			Bug11.B got = orig.getEntry().get(i);
			assertMessagesEqual(expected, got);
		}
	}

	private static void assertMessagesEqual(Bug11.B expected, Bug11.B got) {
		assertEquals(expected.getVal(), got.getVal());
		assertEquals(expected.getCval().getVal(), got.getCval().getVal());
		assertEquals(expected.getResult().size(), got.getResult().size());
		for (int i = 0; i < expected.getResult().size(); i++) {
			Bug11.B.Result eResult = expected.getResult().get(i);
			Bug11.B.Result gResult = got.getResult().get(i);
			assertEquals(eResult.getUrl(), gResult.getUrl());
		}
	}
}
