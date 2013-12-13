package com.google.code.proto.gcless;

import java.util.Properties;

final class GeneratorConfiguration {

	private final boolean interfaceBased;
	private final boolean generateStaticFields;
	private final boolean generateListHelpers;
	private final boolean generateChaining;
	private final String messageExtendsClass;
	private final boolean generateToString;

	GeneratorConfiguration(Properties props) {
		String interfaceBased = props.getProperty("interface.based");
		if (interfaceBased != null && interfaceBased.equals("true")) {
			this.interfaceBased = true;
		} else {
			this.interfaceBased = false;
		}
		if (!this.interfaceBased) {
			String genateStaticFields = props.getProperty("generate.static.fields");
			if (genateStaticFields != null && genateStaticFields.equals("true")) {
				generateStaticFields = true;
			} else {
				generateStaticFields = false;
			}
		} else {
			generateStaticFields = false;
		}
		String generateRepeatedHelpers = props.getProperty("generate.list.helpers");
		if (generateRepeatedHelpers != null && generateRepeatedHelpers.equals("true")) {
			this.generateListHelpers = true;
		} else {
			this.generateListHelpers = false;
		}
		String generateChaining = props.getProperty("generate.chaining");
		if( generateChaining != null && generateChaining.equals("true") ) {
			this.generateChaining = true;
		} else {
			this.generateChaining = false;
		}
		this.messageExtendsClass = props.getProperty("message.extends.class");
		String generateToStringStr = props.getProperty("generate.tostring");
		if( generateToStringStr != null && generateToStringStr.equals("true") ) {
			this.generateToString = true;
		} else {
			this.generateToString = false;
		}
	}
	
	public boolean isGenerateToString() {
		return generateToString;
	}
	
	public String getMessageExtendsClass() {
		return messageExtendsClass;
	}
	
	public boolean isGenerateChaining() {
		return generateChaining;
	}

	public boolean isInterfaceBased() {
		return interfaceBased;
	}
	
	public boolean isGenerateListHelpers() {
		return generateListHelpers;
	}
	
	public boolean isGenerateStaticFields() {
		return generateStaticFields;
	}
}
