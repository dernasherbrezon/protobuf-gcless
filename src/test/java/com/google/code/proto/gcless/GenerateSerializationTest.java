package com.google.code.proto.gcless;

import java.util.List;

import com.google.code.proto.gcless.MemlessParser;
import com.google.code.proto.gcless.ProtobufField;
import com.google.code.proto.gcless.ProtobufMessage;

public class GenerateSerializationTest {

	public static void main(String[] args) throws Exception {

		MemlessParser parser = new MemlessParser();
		parser.process("src/test/resources/unittest.proto");
		List<ProtobufMessage> messages = parser.getMessages();

		ProtobufMessage message = getMessage("TestAllTypes", messages);
		if (message == null) {
			throw new IllegalStateException();
		}

		for (ProtobufField curField : message.getFields()) {
			String data = null;
			if (curField.getNature().equals("repeated") && !curField.getType().equals("bytes")) {
				System.out.println("List<" + curField.getFullyClarifiedJavaType() + "> values" + curField.getBeanName() + " = new ArrayList<" + curField.getFullyClarifiedJavaType() + ">();");
				for (int i = 0; i < 2; i++) {
					System.out.println("values" + curField.getBeanName() + ".add(" + getJavaTypeValue(curField) + ");");
				}
				System.out.println("message.set" + curField.getBeanName() + "(values" + curField.getBeanName() + ");");
			} else {
				data = getJavaTypeValue(curField);
				if (data == null) {
					continue;
				}
				System.out.println("message.set" + curField.getBeanName() + "(" + data + ");");
			}
		}
	}

	private static ProtobufMessage getMessage(String name, List<ProtobufMessage> messages) {
		for (ProtobufMessage curMessage : messages) {
			if (curMessage.getName().equals(name)) {
				return curMessage;
			}
		}
		return null;
	}

	private static String getJavaTypeValue(ProtobufField curField) {
		if (curField.getType().equals("int32") || curField.getType().equals("uint32") || curField.getType().equals("sint32") || curField.getType().equals("fixed32") || curField.getType().equals("sfixed32")) {
			if (curField.getNature().equals("repeated")) {
				return "1";
			} else {
				return "1";
			}
		}
		if (curField.getType().equals("int64") || curField.getType().equals("uint64") || curField.getType().equals("sint64") || curField.getType().equals("fixed64") || curField.getType().equals("sfixed64")) {
			if (curField.getNature().equals("repeated")) {
				return "1l";
			} else {
				return "1l";
			}
		}
		if (curField.getType().equals("double")) {
			if (curField.getNature().equals("repeated")) {
				return "1.1";
			} else {
				return "1.1";
			}
		}
		if (curField.getType().equals("bool")) {
			if (curField.getNature().equals("repeated")) {
				return "true";
			} else {
				return "true";
			}
		}
		if (curField.getType().equals("string")) {
			return "\"123\"";
		}
		if (curField.getType().equals("bytes")) {
			return "new byte[]{(byte)1, (byte)2}";
		}
		if (curField.getType().equals("float")) {
			if (curField.getNature().equals("repeated")) {
				return "1.0f";
			} else {
				return "1.0f";
			}
		}
		return null;
	}

}
