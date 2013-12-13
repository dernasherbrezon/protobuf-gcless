package com.google.code.proto.gcless;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MemlessParser {

	private final static Pattern UNDERSCORE = Pattern.compile("_");

	private String protoPackageName;
	private String outerClassName;
	private String javaPackageName;

	private final List<ProtobufMessage> messages = new ArrayList<ProtobufMessage>();
	private final List<ProtobufEnum> enums = new ArrayList<ProtobufEnum>();

	private final List<ProtobufMessage> importedMessages = new ArrayList<ProtobufMessage>();
	private final List<ProtobufEnum> importedEnums = new ArrayList<ProtobufEnum>();

	private final List<MemlessParser> importedParsers = new ArrayList<MemlessParser>();

	private int curIndex = 0;
	private String[] tokens;

	void process(String filename) throws Exception {
		String file = loadFile(filename);
		file = file.replaceAll(";", " ; ");
		file = file.replaceAll("\\[", " [ ");
		file = file.replaceAll("\\]", " ] ");
		file = file.replaceAll(",", " , ");
		file = file.replaceAll("=", " = ");
		file = file.replaceAll("\\{", " { ");
		file = file.replaceAll("\\}", " } ");
		file = file.replaceAll(" {2,}", " ");
		tokens = file.split("[ \n\r]");

		String curToken = null;
		while ((curToken = getNextIgnoreNewLine()) != null) {
			if (curToken.equals(Tokens.PROTO_PACKAGE)) {
				protoPackageName = getNextIgnoreNewLine();
				if (protoPackageName == null || !Tokens.isIdentifier(protoPackageName)) {
					throw new Exception("Invalid package definition. Expected package name");
				}
				consume(";");
				continue;
			}
			if (curToken.equals(Tokens.MESSAGE)) {
				String messageName = getNextNotEmpty();
				if (messageName == null || !Tokens.isIdentifier(messageName)) {
					throw new Exception("Invalid message name. Invalid symbols found");
				}
				ProtobufMessage curMessage = new ProtobufMessage();
				curMessage.setName(messageName);
				curMessage.setFullyClarifiedJavaName(messageName);
				curMessage.setFullyClarifiedProtoName(messageName);
				processInnerMessage(curMessage);
				messages.add(curMessage);
				continue;
			}
			if (curToken.equals(Tokens.ENUM_TOKEN)) {
				String enumName = getNextNotEmpty();
				if (enumName == null || !Tokens.isIdentifier(enumName)) {
					throw new Exception("Invalid enum name. Invalid symbols found");
				}
				ProtobufEnum curEnum = new ProtobufEnum();
				curEnum.setName(enumName);
				curEnum.setFullyClarifiedJavaName(enumName);
				curEnum.setFullyClarifiedProtoName(enumName);
				processInnerEnum(curEnum);
				enums.add(curEnum);
				continue;
			}
			if (curToken.equals(Tokens.OPTION)) {
				String optionType = getNextIgnoreNewLine();
				if (optionType != null && optionType.equals(Tokens.JAVA_OUTER_CLASSNAME)) {
					consume("=");
					outerClassName = getNextIgnoreNewLine();
					outerClassName = outerClassName.replaceAll("\"", "");
					continue;
				}
				if (optionType != null && optionType.equals(Tokens.JAVA_PACKAGE)) {
					consume("=");
					javaPackageName = getNextIgnoreNewLine();
					javaPackageName = javaPackageName.replaceAll("\"", "");
					continue;
				}
				System.out.println("option \"" + optionType + "\" is not supported");
				consumeTillMessage(";");
				continue;
			}
			if (curToken.equals(Tokens.IMPORT_TOKEN)) {
				String importFile = getNextIgnoreNewLine();
				if (importFile == null) {
					System.out.println("import file should be specified");
					continue;
				}
				importFile = importFile.replaceAll("\"", "");
				MemlessParser parser = new MemlessParser();
				parser.process(importFile);
				importedParsers.add(parser);
				importedParsers.addAll(parser.getImportedParsers());
				importedMessages.addAll(parser.getMessages());
				importedEnums.addAll(parser.getEnums());
				continue;
			}
			if (curToken.equals(Tokens.SYNTAX)) {
				System.out.println("\"syntax\" is not supported");
				consumeTillMessage(";");
			}
		}

		String strToAppend = null;
		if (javaPackageName != null) {
			strToAppend = javaPackageName;
		} else if (protoPackageName != null) {
			strToAppend = protoPackageName;
		}

		if (strToAppend != null && outerClassName != null) {
			strToAppend += "." + outerClassName;
		}

		if (strToAppend != null) {
			for (ProtobufMessage curMessage : messages) {
				appendJavaPackageName(curMessage, strToAppend);
			}
			for (ProtobufEnum curEnum : enums) {
				curEnum.setFullyClarifiedJavaName(strToAppend + "." + curEnum.getFullyClarifiedJavaName());
			}
		}

		if (protoPackageName != null) {
			for (ProtobufMessage curMessage : messages) {
				appendProtoPackageName(curMessage, protoPackageName);
			}
			for (ProtobufEnum curEnum : enums) {
				curEnum.setFullyClarifiedProtoName(protoPackageName + "." + curEnum.getFullyClarifiedProtoName());
			}
		}

		List<ProtobufMessage> allMessages = new ArrayList<ProtobufMessage>();
		allMessages.addAll(messages);
		allMessages.addAll(importedMessages);
		List<ProtobufEnum> allEnums = new ArrayList<ProtobufEnum>();
		allEnums.addAll(enums);
		allEnums.addAll(importedEnums);

		enrichFieldsInMessage(messages, protoPackageName, allMessages, allEnums);
	}

	private static void appendJavaPackageName(ProtobufMessage message, String name) {
		message.setFullyClarifiedJavaName(name + "." + message.getFullyClarifiedJavaName());
		for (ProtobufMessage curMessage : message.getNestedMessages()) {
			appendJavaPackageName(curMessage, getParent(message.getFullyClarifiedJavaName()));
		}

		for (ProtobufEnum curEnum : message.getEnums()) {
			curEnum.setFullyClarifiedJavaName(name + "." + curEnum.getFullyClarifiedJavaName());
		}
	}

	private static void appendProtoPackageName(ProtobufMessage message, String name) {
		message.setFullyClarifiedProtoName(name + "." + message.getFullyClarifiedProtoName());
		for (ProtobufMessage curMessage : message.getNestedMessages()) {
			appendProtoPackageName(curMessage, getParent(message.getFullyClarifiedProtoName()));
		}

		for (ProtobufEnum curEnum : message.getEnums()) {
			curEnum.setFullyClarifiedProtoName(name + "." + curEnum.getFullyClarifiedProtoName());
		}
	}

	private void processInnerMessage(ProtobufMessage parentMessage) throws Exception {
		String curToken = null;
		int intendtion = 0;
		while ((curToken = getNextIgnoreNewLine()) != null) {
			curToken = curToken.trim();
			if (curToken.equals(Tokens.BRACE_START)) {
				intendtion++;
				continue;
			}
			if (curToken.equals(Tokens.BRACE_END)) {
				intendtion--;
				if (intendtion == 0) {
					return;
				}
				continue;
			}
			if (curToken.equals(Tokens.MESSAGE)) {
				String messageName = getNextNotEmpty();
				if (messageName == null || !Tokens.isIdentifier(messageName)) {
					throw new Exception("Invalid message name. Invalid symbols found");
				}
				ProtobufMessage curMessage = new ProtobufMessage();
				curMessage.setName(messageName);
				curMessage.setFullyClarifiedJavaName(parentMessage.getFullyClarifiedJavaName() + "." + messageName);
				curMessage.setFullyClarifiedProtoName(parentMessage.getFullyClarifiedProtoName() + "." + messageName);
				processInnerMessage(curMessage);
				parentMessage.addNestedMessage(curMessage);
				continue;
			}
			if (curToken.equals(Tokens.OPTIONAL_FIELD)) {
				String type = getNextNotEmpty();
				if (type == null || !Tokens.isValidFieldType(type)) {
					throw new Exception("Invalid field type found: " + type);
				}
				ProtobufField curField = new ProtobufField();
				curField.setNature("optional");
				if (type.equals("group")) {
					processGroup(curField, parentMessage);
					continue;
				}
				curField.setType(type);
				String name = getNextNotEmpty();
				if (name == null || !Tokens.isIdentifier(name)) {
					throw new Exception("Invalid field name: " + name);
				}
				curField.setName(name);
				consume("=");
				long tag = consumeLong();
				if (!Tokens.isValidTag(tag)) {
					throw new Exception("Invalid tag detected: " + tag);
				}
				curField.setTag(tag);
				parentMessage.addField(curField);
				String brace = lookAhead(1);
				if (brace.equals(Tokens.SQUARE_BRACE_START)) {
					processInnerSquareBraces(curField);
				}
				consume(";");
				continue;
			}
			if (curToken.equals(Tokens.REPEATED_FIELD)) {
				String type = getNextNotEmpty();
				if (type == null || !Tokens.isValidFieldType(type)) {
					throw new Exception("Invalid field type found: " + type);
				}
				ProtobufField curField = new ProtobufField();
				curField.setNature("repeated");
				if (type.equals("group")) {
					processGroup(curField, parentMessage);
					continue;
				}
				curField.setType(type);
				String name = getNextNotEmpty();
				if (name == null || !Tokens.isIdentifier(name)) {
					throw new Exception("Invalid field name: " + name);
				}
				curField.setName(name);
				consume("=");
				long tag = consumeLong();
				if (!Tokens.isValidTag(tag)) {
					throw new Exception("Invalid tag detected: " + tag);
				}
				curField.setTag(tag);
				parentMessage.addField(curField);
				String brace = lookAhead(1);
				if (brace.equals(Tokens.SQUARE_BRACE_START)) {
					processInnerSquareBraces(curField);
				}
				consume(";");
				continue;
			}
			if (curToken.equals(Tokens.REQUIRED_FIELD)) {
				String type = getNextNotEmpty();
				if (type == null || !Tokens.isValidFieldType(type)) {
					throw new Exception("Invalid field type found: " + type);
				}
				ProtobufField curField = new ProtobufField();
				curField.setNature("required");
				if (type.equals("group")) {
					processGroup(curField, parentMessage);
					continue;
				}
				curField.setType(type);
				String name = getNextNotEmpty();
				if (name == null || !Tokens.isIdentifier(name)) {
					throw new Exception("Invalid field name: " + name);
				}
				curField.setName(name);
				consume("=");
				long tag = consumeLong();
				if (!Tokens.isValidTag(tag)) {
					throw new Exception("Invalid tag detected: " + tag);
				}
				curField.setTag(tag);
				parentMessage.addField(curField);
				String brace = lookAhead(1);
				if (brace.equals(Tokens.SQUARE_BRACE_START)) {
					processInnerSquareBraces(curField);
				}
				consume(";");
				continue;
			}
			if (curToken.equals(Tokens.ENUM_TOKEN)) {
				String enumName = getNextNotEmpty();
				if (enumName == null || !Tokens.isIdentifier(enumName)) {
					throw new Exception("Invalid enum name. Invalid symbols found");
				}
				ProtobufEnum curEnum = new ProtobufEnum();
				curEnum.setName(enumName);
				curEnum.setFullyClarifiedJavaName(parentMessage.getFullyClarifiedJavaName() + "." + enumName);
				curEnum.setFullyClarifiedProtoName(parentMessage.getFullyClarifiedProtoName() + "." + enumName);
				processInnerEnum(curEnum);
				parentMessage.addEnum(curEnum);
				continue;
			}
		}
		throw new Exception("Incomplete message: " + parentMessage);
	}
	
	private void processGroup(ProtobufField curField, ProtobufMessage parentMessage) throws Exception {
		curField.setGroup(true);
		String groupName = getNextNotEmpty();
		if (groupName == null || !Tokens.isIdentifier(groupName)) {
			throw new Exception("Invalid group name. Invalid symbols found: " + groupName);
		}
		curField.setType(groupName);
		curField.setName(groupName.toLowerCase(Locale.UK));
		consume("=");
		long tag = consumeLong();
		if (!Tokens.isValidTag(tag)) {
			throw new Exception("Invalid tag detected: " + tag);
		}
		curField.setTag(tag);
		parentMessage.addField(curField);
		ProtobufMessage curMessage = new ProtobufMessage();
		curMessage.setName(groupName);
		curMessage.setGroup(true);
		curMessage.setFullyClarifiedJavaName(parentMessage.getFullyClarifiedJavaName() + "." + groupName);
		curMessage.setFullyClarifiedProtoName(parentMessage.getFullyClarifiedProtoName() + "." + groupName);
		processInnerMessage(curMessage);
		parentMessage.addNestedMessage(curMessage);
	}

	private void processInnerEnum(ProtobufEnum pEnum) throws Exception {
		String curToken = null;
		int intendtion = 0;
		while ((curToken = getNextIgnoreNewLine()) != null) {
			if (curToken.equals(Tokens.BRACE_START)) {
				intendtion++;
				continue;
			}
			if (curToken.equals(Tokens.BRACE_END)) {
				intendtion--;
				if (intendtion == 0) {
					return;
				}
				continue;
			}
			if (!Tokens.isIdentifier(curToken)) {
				throw new Exception("Invalid enum name: " + curToken);
			}
			EnumValue curValue = new EnumValue();
			curValue.setName(curToken);
			consume("=");
			long tag = consumeLong();
			if (!Tokens.isValidTag(tag)) {
				throw new Exception("Invalid tag detected: " + tag);
			}
			curValue.setId(tag);
			pEnum.addValue(curValue);
			consume(";");
		}
		throw new Exception("incomplete enum: " + pEnum);
	}

	private void processInnerSquareBraces(ProtobufField fields) throws Exception {
		String curToken = null;
		int intendtion = 0;
		while ((curToken = getNextIgnoreNewLine()) != null) {
			if (curToken.equals(Tokens.SQUARE_BRACE_START)) {
				intendtion++;
				continue;
			}
			if (curToken.equals(Tokens.SQUARE_BRACE_END)) {
				intendtion--;
				if (intendtion == 0) {
					return;
				}
				continue;
			}
			if (curToken.equals(",")) {
				continue;
			}
			if (curToken.equals(Tokens.DEPRECATED)) {
				consume("=");
				String value = getNextIgnoreNewLine();
				if (value != null && value.equals("true")) {
					fields.setDeprecated(true);
				}
				continue;
			}
			if (curToken.equals(Tokens.PACKED)) {
				consume("=");
				String value = getNextIgnoreNewLine();
				if (value != null && value.equals("true")) {
					fields.setPacked(true);
				}
				continue;
			}
			if (curToken.equals(Tokens.DEFAULT)) {
				consume("=");
				String value = getNextIgnoreNewLine();
				fields.setDefaults(value);
				continue;
			}
		}
		throw new Exception("Incomplete square braces");
	}

	// String getProtoPackageName() {
	// return protoPackageName;
	// }
	//
	// String getJavaPackageName() {
	// return javaPackageName;
	// }

	String getPackageName() {
		if (javaPackageName != null) {
			return javaPackageName;
		}
		return protoPackageName;
	}

	List<ProtobufMessage> getMessages() {
		return messages;
	}

	List<ProtobufEnum> getEnums() {
		return enums;
	}

	String getOuterClassName() {
		return outerClassName;
	}

	List<MemlessParser> getImportedParsers() {
		return importedParsers;
	}

	private void consume(String expected) throws Exception {
		String token = getNextNotEmpty();
		if (token == null || !token.equals(expected)) {
			throw new Exception("Invalid token found: " + token + " Expected: " + expected);
		}
	}

	private long consumeLong() throws Exception {
		String token = getNextNotEmpty();
		if (token == null) {
			throw new Exception("Token not found");
		}
		try {
			return Long.valueOf(token);
		} catch (Exception e) {
			throw new Exception("Expected integer. Received: " + token, e);
		}
	}

	private String getNextIgnoreNewLine() {
		String curToken = null;
		do {
			curToken = getNext();
		} while (curToken != null && (curToken.equals("\n") || curToken.equals("\r") || curToken.equals("\n\r") || curToken.length() == 0));
		return curToken;
	}

	private String consumeTillMessage(String str) {
		String curToken = null;
		do {
			curToken = getNextIgnoreNewLine();
		} while (curToken != null && !curToken.equals(str));
		return curToken;
	}

	private String getNext() {
		if (curIndex == tokens.length) {
			return null;
		}
		String result = tokens[curIndex];
		curIndex += 1;
		return result;
	}

	private String lookAhead(int number) {
		if (curIndex == tokens.length) {
			return null;
		}
		String result = tokens[number + curIndex - 1];
		return result;
	}

	private String getNextNotEmpty() {
		String curToken = null;
		do {
			curToken = getNext();
		} while (curToken != null && curToken.length() == 0);
		return curToken;
	}

	private static void enrichFieldsInMessage(List<ProtobufMessage> messages, String externalPackage, List<ProtobufMessage> allMessages, List<ProtobufEnum> allEnums) throws Exception {
		for (ProtobufMessage curMessage : messages) {
			if (curMessage.getFields() != null) {
				for (ProtobufField curField : curMessage.getFields()) {
					enrichField(curField, curMessage.getFullyClarifiedJavaName(), externalPackage, allMessages, allEnums);
				}
			}
			if (curMessage.getNestedMessages() != null) {
				enrichFieldsInMessage(curMessage.getNestedMessages(), externalPackage, allMessages, allEnums);
			}
		}
	}

	private static void enrichField(ProtobufField curField, String prefix, String externalPackage, List<ProtobufMessage> allMessages, List<ProtobufEnum> allEnums) throws Exception {
		curField.setBeanName(convertNameToJavabean(curField.getName()));
		curField.setStreamBeanType(convertNameToJavabean(curField.getType()));
		curField.setJavaFieldName(convertBeanNameToJavaFieldName(curField.getBeanName()));

		String javaType = getJavaType(curField);
		if (javaType != null) {
			curField.setFullyClarifiedJavaType(javaType);
			return;
		}

		curField.setComplexType(true);

		String type = null;
		if (prefix != null) {
			type = prefix + "." + curField.getType();
		} else {
			type = curField.getType();
		}

		String complexFieldType = getFullyClarifiedNameBySimpleName(allMessages, type);
		if (complexFieldType != null) {
			curField.setFullyClarifiedJavaType(complexFieldType);
			if (isEnum(allMessages, type)) {
				curField.setEnumType(true);
			}
			return;
		}
		complexFieldType = getFullyClarifiedNameBySimpleName(allMessages, curField.getType());
		if (complexFieldType != null) {
			curField.setFullyClarifiedJavaType(complexFieldType);
			if (isEnum(allMessages, curField.getType())) {
				curField.setEnumType(true);
			}
			return;
		}
		if (externalPackage != null) {
			complexFieldType = getFullyClarifiedNameBySimpleName(allMessages, externalPackage + "." + curField.getType());
			if (complexFieldType != null) {
				curField.setFullyClarifiedJavaType(complexFieldType);
				if (isEnum(allMessages, externalPackage + "." + curField.getType())) {
					curField.setEnumType(true);
				}
				return;
			}
		}

		complexFieldType = getFullyClarifiedNameBySimpleNameFromEnum(allEnums, type);
		if (complexFieldType != null) {
			curField.setFullyClarifiedJavaType(complexFieldType);
			curField.setEnumType(true);
			return;
		}
		complexFieldType = getFullyClarifiedNameBySimpleNameFromEnum(allEnums, curField.getType());
		if (complexFieldType != null) {
			curField.setFullyClarifiedJavaType(complexFieldType);
			curField.setEnumType(true);
			return;
		}
		if (externalPackage != null) {
			complexFieldType = getFullyClarifiedNameBySimpleNameFromEnum(allEnums, externalPackage + "." + curField.getType());
			if (complexFieldType != null) {
				curField.setFullyClarifiedJavaType(complexFieldType);
				curField.setEnumType(true);
				return;
			}
		}

		curField.setFullyClarifiedJavaType(curField.getType());
		throw new Exception("unknown field type: " + type);
	}

	private static boolean isEnum(List<ProtobufMessage> messages, String type) {
		for (ProtobufMessage curMessage : messages) {
			if (curMessage.getNestedMessages() != null) {
				boolean result = isEnum(curMessage.getNestedMessages(), type);
				if (result) {
					return result;
				}
			}
			if (curMessage.getEnums() != null) {
				for (ProtobufEnum curEnum : curMessage.getEnums()) {
					if (curEnum.getName().equals(type) || curEnum.getFullyClarifiedProtoName().equals(type)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static String getParent(String fullyClarifiedName) {
		int index = fullyClarifiedName.lastIndexOf(".");
		if (index == -1) {
			return null;
		}
		return fullyClarifiedName.substring(0, index);
	}

	private static String getFullyClarifiedNameBySimpleName(List<ProtobufMessage> messages, String name) {
		for (ProtobufMessage curMessage : messages) {
			if (curMessage.getName().equals(name) || curMessage.getFullyClarifiedProtoName().equals(name)) {
				return curMessage.getFullyClarifiedJavaName();
			}
			if (curMessage.getNestedMessages() != null) {
				String result = getFullyClarifiedNameBySimpleName(curMessage.getNestedMessages(), name);
				if (result != null) {
					return result;
				}
			}
			if (curMessage.getEnums() != null) {
				String result = getFullyClarifiedNameBySimpleNameFromEnum(curMessage.getEnums(), name);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	private static String getFullyClarifiedNameBySimpleNameFromEnum(List<ProtobufEnum> messages, String name) {
		for (ProtobufEnum curEnum : messages) {
			if (curEnum.getName().equals(name) || curEnum.getFullyClarifiedProtoName().equals(name)) {
				return curEnum.getFullyClarifiedJavaName();
			}
		}
		return null;
	}

	static String convertNameToJavabean(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		String[] parts = UNDERSCORE.split(str);
		for (String curPart : parts) {
			if (curPart.length() == 0) {
				continue;
			}
			result.append(Character.toUpperCase(curPart.charAt(0)));
			if (curPart.length() > 1) {
				result.append(curPart.substring(1));
			}
		}
		return result.toString();
	}
	
	static String convertBeanNameToJavaFieldName(String beanName) {
		if (beanName == null || beanName.length() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append(Character.toLowerCase(beanName.charAt(0)));
		if( beanName.length() > 1 ) {
			result.append(beanName.substring(1));
		}
		return result.toString();
	}

	private static String getJavaType(ProtobufField curField) {
		if (curField.getType().equals("int32") || curField.getType().equals("uint32") || curField.getType().equals("sint32") || curField.getType().equals("fixed32") || curField.getType().equals("sfixed32")) {
			if (curField.getNature().equals("repeated")) {
				return "Integer";
			} else {
				return "int";
			}
		}
		if (curField.getType().equals("int64") || curField.getType().equals("uint64") || curField.getType().equals("sint64") || curField.getType().equals("fixed64") || curField.getType().equals("sfixed64")) {
			if (curField.getNature().equals("repeated")) {
				return "Long";
			} else {
				return "long";
			}
		}
		if (curField.getType().equals("double")) {
			if (curField.getNature().equals("repeated")) {
				return "Double";
			} else {
				return "double";
			}
		}
		if (curField.getType().equals("bool")) {
			if (curField.getNature().equals("repeated")) {
				return "Boolean";
			} else {
				return "boolean";
			}
		}
		if (curField.getType().equals("string")) {
			return "String";
		}
		if (curField.getType().equals("bytes")) {
			return "byte[]";
		}
		if (curField.getType().equals("float")) {
			if (curField.getNature().equals("repeated")) {
				return "Float";
			} else {
				return "float";
			}
		}
		return null;
	}

	private static String loadFile(String filename) throws Exception {
		// rude, but didnt find usages of double slashes in-between some valid
		// identifiers
		Pattern COMMENT = Pattern.compile("//(.*)");
		StringBuilder result = new StringBuilder();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(filename));
			String curLine = null;
			while ((curLine = r.readLine()) != null) {
				if (curLine.startsWith("//")) {
					continue;
				}
				Matcher m = COMMENT.matcher(curLine);
				if (m.find()) {
					curLine = m.replaceAll("");
				}
				result.append(curLine);
				result.append("\n");
			}
		} finally {
			if (r != null) {
				r.close();
			}
		}
		return result.toString();
	}

}
