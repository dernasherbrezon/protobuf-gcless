package protobuf_gcless_import;

import java.io.IOException;
public final class ImportMessageSerializer {
public static byte[] serialize(protobuf_gcless_import.ImportMessage message) {
try {
int totalSize = 0;
if (message.hasD()) {
totalSize += ProtobufOutputStream.computeInt32Size(1, message.getD());
}
final byte[] result = new byte[totalSize];
int position = 0;
if (message.hasD()) {
position = ProtobufOutputStream.writeInt32(1, message.getD(), result, position);
}
ProtobufOutputStream.checkNoSpaceLeft(result, position);
return result;
} catch (Exception e) {
throw new RuntimeException(e);
}
}
public static void serialize(protobuf_gcless_import.ImportMessage message, java.io.OutputStream os) {
try {
if (message.hasD()) {
ProtobufOutputStream.writeInt32(1, message.getD(), os);
}
} catch (IOException e) {
throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
}
}
public static protobuf_gcless_import.ImportMessage parseFrom(byte[] data) throws java.io.IOException {
CurrentCursor cursor = new CurrentCursor();
return parseFrom(data, cursor);
}
public static protobuf_gcless_import.ImportMessage parseFrom(byte[] data, int offset, int length) throws java.io.IOException {
CurrentCursor cursor = new CurrentCursor();
cursor.addToPosition(offset);
cursor.setProcessUpToPosition(offset + length);
return parseFrom(data, cursor);
}
public static protobuf_gcless_import.ImportMessage parseFrom(byte[] data, CurrentCursor cursor) throws java.io.IOException {
protobuf_gcless_import.ImportMessage message = new protobuf_gcless_import.ImportMessage();
while(true) {
if (ProtobufInputStream.isAtEnd(data, cursor)) {
return message;
}
int varint = ProtobufInputStream.readRawVarint32(data, cursor);
int tag = ProtobufInputStream.getTagFieldNumber(varint);
switch(tag) {
case 0: 
return message;
 default: 
 ProtobufInputStream.skipUnknown(varint, data, cursor);
 break;
case 1: 
message.setD(ProtobufInputStream.readInt32(data,cursor));
break;
}
}
}
public static protobuf_gcless_import.ImportMessage parseFrom(java.io.InputStream is) throws java.io.IOException {
CurrentCursor cursor = new CurrentCursor();
return parseFrom(is, cursor);
}
public static protobuf_gcless_import.ImportMessage parseFrom(java.io.InputStream is, int offset, int length) throws java.io.IOException {
CurrentCursor cursor = new CurrentCursor();
cursor.addToPosition(offset);
cursor.setProcessUpToPosition(offset + length);
return parseFrom(is, cursor);
}
public static protobuf_gcless_import.ImportMessage parseFrom(java.io.InputStream is, CurrentCursor cursor) throws java.io.IOException {
protobuf_gcless_import.ImportMessage message = new protobuf_gcless_import.ImportMessage();
while(true) {
if( cursor.getCurrentPosition() == cursor.getProcessUpToPosition() ) {
return message;
}
int varint = ProtobufInputStream.readRawVarint32(is, cursor);
int tag = ProtobufInputStream.getTagFieldNumber(varint);
if (ProtobufInputStream.isAtEnd(cursor)) {
return message;
}
switch(tag) {
case 0: 
return message;
 default: 
 ProtobufInputStream.skipUnknown(varint, is, cursor);
 break;case 1: 
message.setD(ProtobufInputStream.readInt32(is,cursor));
break;
}
}
}
}
