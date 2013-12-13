package com.google.code.proto.gcless;

class ProtobufField {

	private String name;
	private String beanName;
	private String javaFieldName;
	private String type;
	private String streamBeanType;
	private String fullyClarifiedJavaType;
	private boolean complexType = false;
	private String nature;
	private long tag;
	private String defaults;
	private boolean isDeprecated;
	private boolean isPacked;
	private boolean isEnumType;
	private boolean group;
	
	public boolean isGroup() {
		return group;
	}
	
	public void setGroup(boolean group) {
		this.group = group;
	}
	
	public boolean isEnumType() {
		return isEnumType;
	}

	public void setEnumType(boolean isEnumType) {
		this.isEnumType = isEnumType;
	}

	public String getStreamBeanType() {
		return streamBeanType;
	}

	public void setStreamBeanType(String streamBeanType) {
		this.streamBeanType = streamBeanType;
	}

	public boolean isComplexType() {
		return complexType;
	}

	public void setComplexType(boolean complexType) {
		this.complexType = complexType;
	}

	public String getJavaFieldName() {
		return javaFieldName;
	}
	
	public void setJavaFieldName(String javaFieldName) {
		this.javaFieldName = javaFieldName;
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setFullyClarifiedJavaType(String fullyClarifiedJavaType) {
		this.fullyClarifiedJavaType = fullyClarifiedJavaType;
	}

	public String getFullyClarifiedJavaType() {
		return fullyClarifiedJavaType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getTag() {
		return tag;
	}

	public boolean isDeprecated() {
		return isDeprecated;
	}

	public void setDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	public boolean isPacked() {
		return isPacked;
	}

	public void setPacked(boolean isPacked) {
		this.isPacked = isPacked;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getDefaults() {
		return defaults;
	}

	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	
	@Override
	public String toString() {
		return "ProtobufField [name=" + name + ", beanName=" + beanName + ", type=" + type + ", streamBeanType=" + streamBeanType + ", fullyClarifiedJavaType=" + fullyClarifiedJavaType + ", complexType=" + complexType + ", nature=" + nature
				+ ", tag=" + tag + ", defaults=" + defaults + ", isDeprecated=" + isDeprecated + ", isPacked=" + isPacked + ", isEnumType=" + isEnumType + ", group=" + group + "]";
	}

	public boolean isListType() {
		if (getNature().equals("repeated") && !getType().equals("bytes")) {
			return true;
		}
		return false;
	}

}
