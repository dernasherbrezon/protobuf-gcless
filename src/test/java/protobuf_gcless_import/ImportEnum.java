package protobuf_gcless_import;

public enum ImportEnum {
IMPORT_FOO(7),
IMPORT_BAR(8),
IMPORT_BAZ(9),
;
public static ImportEnum valueOf(int value) {
switch (value) {
case 7: return IMPORT_FOO;
case 8: return IMPORT_BAR;
case 9: return IMPORT_BAZ;
default: return null;
}
}
private ImportEnum(int value) {
this.value = value;
}
private int value;
public int getValue() {
return value;
}
}

