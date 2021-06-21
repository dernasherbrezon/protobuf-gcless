About [![Build Status](https://travis-ci.com/dernasherbrezon/protobuf-gcless.svg?branch=master)](https://travis-ci.com/dernasherbrezon/protobuf-gcless) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.google.code%3Aprotobuf-gcless&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.google.code%3Aprotobuf-gcless)
--------

Protobuf out-of-the-box is not a simple a way of encoding data. It forces to use additional features like:
 * immutable data model pattern
 * transfer object pattern

These patterns are not designed to be gc-friendly. They require additional objects creation.

Protobuf-gcless aims to be the best choice for highload systems and android platforms. 

Features
--------

 * complete backward compatibility with protobuf specification 2.3.0
 * own code generator which generates fully standalone code. Not need to include additional libraries. 
 * Very efficient encoding algorithm. Gives ~70% less gc while serializing messaing vs protobuf java implementation 2.3.0
 * Gives ~80% gc reduction while deserializing from `InputStream` vs protobuf java implementation 2.3.0

HowTo
-----

Generate java code
======================

```
java -Dinterface.based=true -jar protobuf-gcless-2.3.0.6.jar ./src/main/java /src/main/resources/messages.proto
```

where:
 * "src/main/java" - output directory for generated classes
 * "/src/main/resources/messages.proto" - protobuf file with messages

Usage
======================

Generated classes based on interfaces rather than Builders. For example consider the following protobuf message description:
```
message Test {
    optional string text = 1;
}
```

Generated class will be:
```
public interface Test {
    boolean hasText();
    void setText(String text);
    String getText();
}
```

Currently it's up to developer to decide how to implement this interface. Based on current code there are two scenarios:
 * use current POJO objects and make them implement this interface. Actually any object could implement this interface. There is no restriction. Suitable for migration from Serializable to protobuf.
 * create implementation from scratch. Suitable for new messages and projects without legacy code.

Here is implementation of basic message:
```
public class TestImpl implements Test {
    private String text;
    
    boolean hasText() {
        if( text != null ) {
            return true;
        }
        return false;
    }

    void setText(String text) {
        this.text = text;
    }

    String getText() {
        return text;
    }
}
```

Here is the example code of serialization:
```
    Test message = ...
    byte[] data = TestSerializer.serialize(message);
```

That'is it. No more Builders.

And next is the example of deserialization from stream:
```
    InputStream is = ...
    Test message = TestSerializer.parseFrom(new MessageFactoryImpl(), is);
```

There is one point that needs to be clarified. `MessageFactoryImpl`. Since serialization based on interfaces there is no way to know how to instantate message implementation. `MessageFactoryImpl` should implement `MessageFactory` that would be generated alongside with Test class. It has only one method: Object create(String fullMessageName); Based on this message name new object should be created. Here is the sample code:
```
    public Object create(String fullMessageName) {
        if( fullMessageName.equals("example.package.Test")) {
            return new TestImpl();
        }
        throw new IllegalArgumentException("Unknown message name: " + fullMessageName);
    }
```

Configuration options
======================

For configuration the following system properties are used:
  * "interface.based" - generates only interfaces without implementation. You have to manually implement POJO interfaces and implement MessageFactory. (see above). If not specified then public classes will be created. No need to implement MessageFactory in this case. *HINT*: use interfaces for better flexibility
  * "generate.static.fields" - generates ```public final static int <FIELDNAME>_FIELD_NUMBER = <FIELDNUMBER>;```. Default protobuf implementation generates such fields, however these static numbers aren't part of POJO notation
  * "generate.list.helpers" - generates additional methods for repeated fields:
    * "addField(int index, Field value)" - if field is empty then create it and add new value. Method "hasField" will return true
    * "addAllField(java.lang.Iterable<? extends Field> values)" - same as above, but add the whole iterable
    * "clearField()" - set field to null and hasField to false
    * "getField(int index)" - doesn't check for null
    * "getFieldCount()" - doesn't check for null
    * "setField(int index, Field value)" - doesn't check for null
  * "generate.chaining" - generates chaining methods. For example: ``` void setField(); ``` will be ``` Message setField(); ```. Reduce code needed to initialize message. For example: ``` new Message().setFoo(1).setBar("bar").setFooBar(false); ```. Disable by default, because doesn't follow POJO notation
  * "message.extends.class" - adds property value after keyword "extends". For example: ``` public static class Message extends com.example.AbstractMessage ```
  * "generate.tostring" - generates the following methods:
    * ``` public String toString() ``` - which overrides default
    * ``` public void toString(java.lang.Appendable a) throws java.io.IOException ``` Appends message. Format is the same as in Eclipse. (command "Source -> Generate toString()...")

Microbenchmark results
======================

com.google.code.proto.gcless.`SerializationTest`:
 * Optimized version(serialize): 9351
 * Default version(serialize): 9284
 * Optimized streamed version(serialize): 4498
 * Default streamed version(serialize): 8694
 * Optimized version(de-serialize): 4564
 * Default version(de-serialize): 6725
 * Optimized streamed version(de-serialize): 7368
 * Default streamed version(de-serialize): 7291


