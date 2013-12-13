package com.google.code.proto.gcless;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Tokens {

	static final String PROTO_PACKAGE = "package";
	static final String SYNTAX = "syntax";
	static final String JAVA_PACKAGE = "java_package";
	static final String MESSAGE = "message";
	static final String OPTIONAL_FIELD = "optional";
	static final String REPEATED_FIELD = "repeated";
	static final String REQUIRED_FIELD = "required";
	static final String BRACE_START = "{";
	static final String BRACE_END = "}";
	static final String ENUM_TOKEN = "enum";
	static final String SQUARE_BRACE_START = "[";
	static final String SQUARE_BRACE_END = "]";
	static final String DEFAULT = "default";
	static final String PACKED = "packed";
	static final String DEPRECATED = "deprecated";
	static final String OPTION = "option";
	static final String IMPORT_TOKEN = "import";
	static final String JAVA_OUTER_CLASSNAME = "java_outer_classname";

	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z_]+");
	private static final Set<String> FIELD_TYPES = new HashSet<String>();

	static {
		FIELD_TYPES.add("int32");
		FIELD_TYPES.add("int64");
		FIELD_TYPES.add("uint32");
		FIELD_TYPES.add("uint64");
		FIELD_TYPES.add("sint32");
		FIELD_TYPES.add("sint64");
		FIELD_TYPES.add("fixed32");
		FIELD_TYPES.add("fixed64");
		FIELD_TYPES.add("sfixed32");
		FIELD_TYPES.add("sfixed64");
		FIELD_TYPES.add("float");
		FIELD_TYPES.add("double");
		FIELD_TYPES.add("bool");
		FIELD_TYPES.add("string");
		FIELD_TYPES.add("bytes");
	}

	static boolean isIdentifier(String str) {
		Matcher m = IDENTIFIER_PATTERN.matcher(str);
		return m.find();
	}

	static boolean isValidFieldType(String str) {
		if (FIELD_TYPES.contains(str)) {
			return true;
		}
		if (isIdentifier(str)) {
			return true;
		}
		return false;
	}
	
	static boolean isValidTag(long tag) {
		if( tag > 268435455 || tag < -268435455 ) {
			return false;
		}
		return true;
	}

}
