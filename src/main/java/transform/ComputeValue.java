package transform;

import soot.JastAddJ.GEExpr;
import soot.JastAddJ.LEExpr;
import soot.Local;
import soot.Value;
import soot.jimple.*;

import java.util.Map;

public class ComputeValue {
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
