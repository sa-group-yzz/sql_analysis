package transform;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.util.Objects;

public class MyValue {
    public enum Type {
        NAC, UNDEF, VALUE, RANGE
    }

    private Type type;
    private int value;

    public String getRangeOP() {
        return rangeOP;
    }

    private String rangeOP;


    public MyValue(int value) {
        this.value = value;
        this.type = Type.VALUE;
        this.rangeOP = null;
    }

    public MyValue(Type type) {
        this.value = -1;
        this.type = type;
        this.rangeOP = null;
    }

    public MyValue(int value, String rangeOP) {
        this.value = value;
        this.type = Type.RANGE;
        this.rangeOP = rangeOP;
    }

    public int getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }

    public static MyValue getNAC() {
        return new MyValue(Type.NAC);
    }

    public static MyValue getUNDEF() {
        return new MyValue(Type.UNDEF);
    }

    public static MyValue makeConstant(int value) {
        return new MyValue(value);
    }

    public static MyValue makeRange(int value, String op) {
        return new MyValue(value, op);
    }

    public int getMinValue() {
        if (this.type == Type.RANGE) {
            if (this.rangeOP.equals(">") || this.rangeOP.equals(">=")) {
                return this.value;
            } else {
                return Integer.MIN_VALUE;
            }
        } else if (this.type == Type.VALUE) {
            return this.value;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public int getMaxValue() {
        if (this.type == Type.RANGE) {
            if (this.rangeOP.equals("<") || this.rangeOP.equals("<=")) {
                return this.value;
            } else {
                return Integer.MAX_VALUE;
            }
        } else if (this.type == Type.VALUE) {
            return this.value;
        } else {
            return Integer.MAX_VALUE;
        }
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

        if (value1.type == Type.RANGE && value2.type == Type.RANGE && value1.equals(value2)) {
            return makeRange(value1.getValue(), value1.getRangeOP());
        }

        if (value1.getValue() == value2.getValue()) {
            return makeConstant(value1.getValue());
        }

        return getNAC();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyValue myValue = (MyValue) o;
        return value == myValue.value && type == myValue.type && Objects.equals(rangeOP, myValue.rangeOP);
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
