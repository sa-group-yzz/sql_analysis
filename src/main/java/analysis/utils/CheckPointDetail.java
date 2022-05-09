package analysis.utils;

import soot.Unit;
import soot.Value;

import java.util.*;

public class CheckPointDetail {
    public static int CONSTANT_ANALYSIS = 0x1;
    public static int DEFINITION_ANALYSIS = 0x10;
    public static int EXPRESSION_ANALYSIS = 0x100;
    public static int LIVENESS_ANALYSIS = 0x1000;
    public static int[] analyses = {CONSTANT_ANALYSIS, DEFINITION_ANALYSIS, EXPRESSION_ANALYSIS,LIVENESS_ANALYSIS };

    protected int analysisBit = 0;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private Unit unit;
    private Value value;
    private String id;

    public CheckPointDetail(Unit unit, String  id, Value value) {
        this.unit = unit;
        this.id = id;
        this.value = value;
    }

    public int getAnalysisBit() {
        return analysisBit;
    }

    public boolean contains(int bit) {
        return (analysisBit & bit) == bit;
    }

    public void setAnalysisBit(int analysisBit) {
        this.analysisBit = analysisBit;
    }

    @Override
    public String toString() {
        return String.format("[%s]%s:%s,%d", id, unit, value, analysisBit);
    }

    public void set2Map(Map<Integer, Set<CheckPointDetail>> ret) {
        for(int a : analyses) {
            if(contains(a)) {
                Set<CheckPointDetail> cl = ret.computeIfAbsent(a, k -> new HashSet<>());
                cl.add(this);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof CheckPointDetail)) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        CheckPointDetail o = (CheckPointDetail) obj;
        return o.analysisBit == this.analysisBit &&
                o.unit == this.unit &&
                o.value == this.value &&
                Objects.equals(o.id, this.id);
    }

    @Override
    public int hashCode() {
        String format = String.format("%d%s", this.analysisBit, this.id);
        return Integer.parseInt(format);
    }
}
