package transform;

import com.alibaba.druid.stat.TableStat;
import soot.JastAddJ.GEExpr;
import soot.JastAddJ.LEExpr;
import soot.Local;
import soot.Value;
import soot.jimple.*;

import java.util.Map;

public class ComputeValue {
    public static MyValue computeWithSQL(Map<Local, MyValue> in, Value value, Map<String, TableStat.Condition> conditions) {
        if (value instanceof Local) {
            Local rightLocal = (Local) value;
            if (conditions.containsKey(rightLocal.getName())) {
                TableStat.Condition condition = conditions.get(rightLocal.getName());
                if (condition.getValues().get(0) instanceof Integer) {
                    int rvalue = (Integer) condition.getValues().get(0);
                    return MyValue.makeRange(rvalue, condition.getOperator());
                }

            } else if (in.containsKey(rightLocal)) {
                return in.get(rightLocal);
            } else {
                return MyValue.getUNDEF();
            }
        } else if (value instanceof IntConstant) {
            return MyValue.makeConstant(((IntConstant) value).value);
        } else if (value instanceof BinopExpr) {
            BinopExpr binopExpr = (BinopExpr) value;
            MyValue op1Value = computeWithSQL(in, binopExpr.getOp1(), conditions);
            MyValue op2Value = computeWithSQL(in, binopExpr.getOp2(), conditions);
            if (conditions != null && conditions.size() > 0) {
                TableStat.Condition ifCondition = (TableStat.Condition) conditions.values().toArray()[0];
                if (op1Value.getType() == MyValue.Type.VALUE && op2Value.getType() != MyValue.Type.VALUE) {
                    op2Value = MyValue.makeRange((Integer) ifCondition.getValues().get(0), ifCondition.getOperator());
                } else if (op2Value.getType() == MyValue.Type.VALUE && op1Value.getType() != MyValue.Type.VALUE) {
                    op1Value = MyValue.makeRange((Integer) ifCondition.getValues().get(0), ifCondition.getOperator());
                }
            }


            if (op1Value.getType() == MyValue.Type.UNDEF && op2Value.getType() == MyValue.Type.UNDEF) {
                return MyValue.getUNDEF();
            } else if (op1Value.getType() == MyValue.Type.NAC || op2Value.getType() == MyValue.Type.NAC) {
                return MyValue.getNAC();
            } else if (op1Value.getType() == MyValue.Type.UNDEF || op2Value.getType() == MyValue.Type.UNDEF) {
                return MyValue.getNAC();
            }

            if (binopExpr instanceof AddExpr) {
                return MyValue.makeConstant(op1Value.getValue() + op2Value.getValue());
            } else if (binopExpr instanceof SubExpr) {
                return MyValue.makeConstant(op1Value.getValue() - op2Value.getValue());
            } else if (binopExpr instanceof MulExpr) {
                return MyValue.makeConstant(op1Value.getValue() * op2Value.getValue());
            } else if (binopExpr instanceof DivExpr) {
                return MyValue.makeConstant(op1Value.getValue() / op2Value.getValue());
            } else if (binopExpr instanceof EqExpr) {
                return MyValue.makeConstant(op1Value.equals(op2Value));
            } else if (binopExpr instanceof NeExpr) {
                return MyValue.makeConstant(!op1Value.equals(op2Value));
            } else if (binopExpr instanceof GeExpr) {
                return computeRangeValue(op1Value, op2Value, ">=");
            } else if (binopExpr instanceof GtExpr) {
                return computeRangeValue(op1Value, op2Value, ">");
            } else if (binopExpr instanceof LeExpr) {
                return computeRangeValue(op1Value, op2Value, "<=");
            } else if (binopExpr instanceof LtExpr) {
                return computeRangeValue(op1Value, op2Value, "<");
            } else if (binopExpr instanceof XorExpr) {
                return MyValue.makeConstant(op1Value.getValue() ^ op2Value.getValue());
            } else if (binopExpr instanceof AndExpr) {
                return MyValue.makeConstant(op1Value.getValue() & op2Value.getValue());
            } else if (binopExpr instanceof OrExpr) {
                return MyValue.makeConstant(op1Value.getValue() | op2Value.getValue());
            }
        }
        return MyValue.getNAC();
    }

    private static MyValue computeRangeValue(MyValue value1, MyValue value2, String op) {
        switch (op) {
            case ">":
                if (value1.getMinValue() > value2.getValue()) {
                    return MyValue.makeConstant(1);
                } else if (value1.getMaxValue() <= value2.getValue()) {
                    return MyValue.makeConstant(0);
                }
            case ">=":
                if (value1.getMinValue() >= value2.getValue()) {
                    return MyValue.makeConstant(1);
                } else if (value1.getMaxValue() < value2.getValue()) {
                    return MyValue.makeConstant(0);
                }
            case "<":
                if (value1.getMaxValue() < value2.getValue()) {
                    return MyValue.makeConstant(1);
                } else if (value1.getMinValue() >= value2.getValue()) {
                    return MyValue.makeConstant(0);
                }
            case "<=":
                if (value1.getMaxValue() <= value2.getValue()) {
                    return MyValue.makeConstant(1);
                } else if (value1.getMinValue() > value2.getValue()) {
                    return MyValue.makeConstant(0);
                }
        }

        return MyValue.getNAC();
    }

    public static MyValue compute(Map<Local, MyValue> in, Value value) {
        if (value instanceof Local) {
            Local rightLocal = (Local) value;
            if (in.containsKey(rightLocal)) {
                return in.get(rightLocal);
            } else {
                return MyValue.getUNDEF();
            }
        } else if (value instanceof IntConstant) {
            return MyValue.makeConstant(((IntConstant) value).value);
        } else if (value instanceof BinopExpr) {
            BinopExpr binopExpr = (BinopExpr) value;
            MyValue op1Value = compute(in, binopExpr.getOp1());
            MyValue op2Value = compute(in, binopExpr.getOp2());

            if (op1Value.getType() == MyValue.Type.UNDEF && op2Value.getType() == MyValue.Type.UNDEF) {
                return MyValue.getUNDEF();
            } else if (op1Value.getType() == MyValue.Type.NAC || op2Value.getType() == MyValue.Type.NAC) {
                return MyValue.getNAC();
            } else if (op1Value.getType() == MyValue.Type.UNDEF || op2Value.getType() == MyValue.Type.UNDEF) {
                return MyValue.getNAC();
            }

            if (binopExpr instanceof AddExpr) {
                return MyValue.makeConstant(op1Value.getValue() + op2Value.getValue());
            } else if (binopExpr instanceof SubExpr) {
                return MyValue.makeConstant(op1Value.getValue() - op2Value.getValue());
            } else if (binopExpr instanceof MulExpr) {
                return MyValue.makeConstant(op1Value.getValue() * op2Value.getValue());
            } else if (binopExpr instanceof DivExpr) {
                return MyValue.makeConstant(op1Value.getValue() / op2Value.getValue());
            } else if (binopExpr instanceof EqExpr) {
                return MyValue.makeConstant(op1Value.getValue() == op2Value.getValue());
            } else if (binopExpr instanceof NeExpr) {
                return MyValue.makeConstant(op1Value.getValue() != op2Value.getValue());
            } else if (binopExpr instanceof GEExpr) {
                return MyValue.makeConstant(op1Value.getValue() >= op2Value.getValue());
            } else if (binopExpr instanceof GtExpr) {
                return MyValue.makeConstant(op1Value.getValue() > op2Value.getValue());
            } else if (binopExpr instanceof LEExpr) {
                return MyValue.makeConstant(op1Value.getValue() <= op2Value.getValue());
            } else if (binopExpr instanceof LtExpr) {
                return MyValue.makeConstant(op1Value.getValue() < op2Value.getValue());
            } else if (binopExpr instanceof XorExpr) {
                return MyValue.makeConstant(op1Value.getValue() ^ op2Value.getValue());
            } else if (binopExpr instanceof AndExpr) {
                return MyValue.makeConstant(op1Value.getValue() & op2Value.getValue());
            } else if (binopExpr instanceof OrExpr) {
                return MyValue.makeConstant(op1Value.getValue() | op2Value.getValue());
            }
        }
        return MyValue.getNAC();
    }
}
