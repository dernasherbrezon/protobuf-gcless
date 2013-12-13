package com.google.code.proto.gcless;

import java.util.ArrayList;
import java.util.List;

final class ProtobufEnum {

	private String name;
	private String fullyClarifiedJavaName;
	private String fullyClarifiedProtoName;
	
	public String getFullyClarifiedProtoName() {
		return fullyClarifiedProtoName;
	}
	
	public void setFullyClarifiedProtoName(String fullyClarifiedProtoName) {
		this.fullyClarifiedProtoName = fullyClarifiedProtoName;
	}

	private List<EnumValue> values = new ArrayList<EnumValue>();

	public String getFullyClarifiedJavaName() {
		return fullyClarifiedJavaName;
	}

	public void setFullyClarifiedJavaName(String fullyClarifiedName) {
		this.fullyClarifiedJavaName = fullyClarifiedName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addValue(EnumValue value) {
		values.add(value);
	}

	public List<EnumValue> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return name;
	}
}
