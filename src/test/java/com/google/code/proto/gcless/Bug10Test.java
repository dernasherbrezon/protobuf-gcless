package com.google.code.proto.gcless;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.protobuf.Bug10;

public class Bug10Test {

	@Test
	public void testToString() {
		List<Bug10.B> entry = new ArrayList<Bug10.B>();
		Bug10.B b1 = new Bug10.B();
		b1.setVal("value1");
		Bug10.B b2 = new Bug10.B();
		b2.setVal("value2");
		entry.add(b1);
		entry.add(b2);

		Bug10.A pojo = new Bug10.A();
		pojo.setEntry2(2);
		pojo.setEntry(entry);
		assertEquals("A [[B [ val=value1], B [ val=value2]], entry2=2]", pojo.toString());
	}

}
