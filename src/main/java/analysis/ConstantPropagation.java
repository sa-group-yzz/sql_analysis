package analysis;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.List;

public class ConstantPropagation extends ForwardFlowAnalysis<Unit, CPFact> {
    public ConstantPropagation(DirectedGraph<Unit> graph) {
        super(graph);
        doAnalysis();
    }

    @Override
    protected void flowThrough(CPFact in, Unit u, CPFact out) {
        copy(in, out);
        Value defValue = null;
        for(ValueBox vb: u.getDefBoxes()) {
            Value v = vb.getValue();
            if(!(v instanceof Local)) {
                continue;
            }
            if(!(v.getType() instanceof IntType)) {
                continue;
            }
            defValue = v;
            break;
        }
        if(defValue == null) {
            return ;
        }
        BinopExpr binopExpr = null;
        Local localExpr = null;
        IntConstant intConstant = null;

        for(ValueBox ub : u.getUseBoxes()) {
            Value v = ub.getValue();
            if((v instanceof BinopExpr)) {
                binopExpr = (BinopExpr) v;
            }
            if(v instanceof Local) {
                localExpr = (Local) v;
            }
            if(v instanceof IntConstant) {
                intConstant = (IntConstant) v;
            }

        }
        Value expr = binopExpr;
        if(expr == null) {
            expr = localExpr;
        }
        if(expr == null) {
            expr = intConstant;
        }
        if(expr == null) {
            return ;
        }
        ConstValue cv = evaluate(expr, in);
        out.update(defValue, cv);
    }

    @Override
    protected CPFact newInitialFlow() {
        return new CPFact();
    }

    @Override
    protected void merge(CPFact a1, CPFact a2, CPFact target) {
        target.clear();;
        for(Value v: a1.keySet()) {
            target.update(v, meetValue(a1.get(v), target.get(v)));
        }
        for(Value v: a2.keySet()) {
            target.update(v, meetValue(a2.get(v), target.get(v)));
        }
    }

    public ConstValue meetValue(ConstValue v1, ConstValue v2) {
        ConstValue v = v2;
        if(v1.isConstant() && v2.isConstant()) {
            if(v1.getConstant() != v2.getConstant()) {
                v = ConstValue.getNAC();
            }
        } else if(v1.isNAC() || v2.isNAC()) {
            v = ConstValue.getNAC();
        } else {
            v = v1.isConstant() ? v1 : v2;
        }
        return v;
    }

    @Override
    protected void copy(CPFact cpFact, CPFact a1) {
        a1.clear();
        a1.copyFrom(cpFact);
    }

    public static ConstValue evaluate(Value oExp, CPFact in) {
        if(oExp instanceof IntConstant) {
            return ConstValue.makeConstant(oExp.hashCode());
        }
        if(oExp instanceof Local) {
            if(!(oExp.getType() instanceof IntType))
                return ConstValue.getNAC();
            return in.get(oExp);
        }
        if(!(oExp instanceof BinopExpr)) {
            return ConstValue.getNAC();
        }
        BinopExpr exp = (BinopExpr)oExp;
        Value v1 = exp.getOp1();
        Value v2 = exp.getOp2();
        ConstValue value1 = in.get(v1);
        ConstValue value2 = in.get(v2);
        if(value1.isNAC() || value2.isNAC()) {
            if(exp instanceof DivExpr || exp instanceof RemExpr) {
                if (value2.isConstant() && value2.getConstant() == 0) {
                    return ConstValue.getUndef();
                }
            }
            return ConstValue.getNAC();
        }
        if(value1.isUndef() || value2.isUndef()) {
            return ConstValue.getUndef();
        }
        int result_integer = 0;
        do {
            if ((exp instanceof DivExpr || exp instanceof RemExpr) && value2.getConstant() == 0) {
                return ConstValue.getUndef();
            }
            if(exp instanceof AddExpr) {
                result_integer = value1.getConstant() + value2.getConstant();
                break;
            }
            if(exp instanceof MulExpr) {
                result_integer =  value1.getConstant() * value2.getConstant();
                break;
            }
            if(exp instanceof DivExpr) {
                result_integer = value1.getConstant() / value2.getConstant();
                break;
            }
            if(exp instanceof SubExpr) {
                result_integer = value1.getConstant() - value2.getConstant();
                break;
            }
            if(exp instanceof RemExpr) {
                result_integer = value1.getConstant() % value2.getConstant();
                break;
            }
            if(exp instanceof ShlExpr) {
                result_integer =  value1.getConstant() << value2.getConstant();
                break;
            }
            if(exp instanceof ShrExpr) {
                result_integer = value1.getConstant() >> value2.getConstant();
                break;
            }
            if(exp  instanceof UshrExpr) {
                result_integer = value1.getConstant() >>> value2.getConstant();
                break;
            }
            if(exp instanceof OrExpr) {
                result_integer = value1.getConstant() | value2.getConstant();
                break;
            }
            if(exp instanceof AndExpr) {
                result_integer = value1.getConstant() & value2.getConstant();
                break;
            }
            if(exp instanceof XorExpr) {
                result_integer = value1.getConstant() ^ value2.getConstant();
                break;
            }
            return ConstValue.getNAC();
        }while (false);
        return ConstValue.makeConstant(result_integer);
    }
}
