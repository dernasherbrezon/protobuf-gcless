package protobuf_gcless_import;

public  class ImportMessage {
private int d;
private boolean hasD;
public boolean hasD() {
return hasD;
}
public int getD() {
return d;
}
public void setD(int D) {
this.d = D;
this.hasD = true;
}
@Override
public String toString() {
java.lang.StringBuilder builder = new java.lang.StringBuilder();
try {
toString(builder);
return builder.toString();
} catch (java.io.IOException e) {
throw new RuntimeException("Unable toString", e);
}
}
public void toString(java.lang.Appendable a_) throws java.io.IOException {
a_.append("ImportMessage [");
a_.append(" d=" + d);
a_.append("]");
}
}
