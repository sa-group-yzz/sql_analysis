package transform;

import java.util.Objects;

public class MyValue {
    public enum Type{
        NAC, UNDEF, VALUE
    }

    private Type type;
    private int value;


    public MyValue(int value) {
        this.value = value;
        this.type = Type.VALUE;
    }

    public MyValue(Type type) {
        this.value = -1;
        this.type = type;
    }

    public int getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }

    public static MyValue getNAC(){
        return new MyValue(Type.NAC);
    }

    public static MyValue getUNDEF(){
        return new MyValue(Type.UNDEF);
    }

    public static MyValue makeConstant(int value) {
        return new MyValue(value);
    }

    public static MyValue makeConstant(boolean value) {
        return value ? makeConstant(1) : makeConstant(0);
    }

    public static MyValue meetValue(MyValue value1, MyValue value2) {
        if (value1.type == Type.NAC || value2.type == Type.NAC) {
            return getNAC();
        }

        if (value1.type == Type.UNDEF) {
            return value2;
        }

        if (value2.type == Type.UNDEF) {
            return value1;
        }

        if (value1.getValue() == value2.getValue()) {
            return makeConstant(value1.getValue());
        }

        return getNAC();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof MyValue)) return false;
        MyValue value = (MyValue) o;
        return value.getValue() == this.value && value.getType() == this.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        if (this.type == Type.NAC) {
            return "NAC";
        }
        if (this.type == Type.UNDEF) {
            return "UNDEF";
        }
        return String.valueOf(this.value);
    }
}
