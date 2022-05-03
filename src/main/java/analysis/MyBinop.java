package analysis;

import soot.jimple.BinopExpr;
import soot.toolkits.scalar.ArraySparseSet;

public class MyBinop {
    public MyBinop(BinopExpr binopExpr) {
        this.binopExpr = binopExpr;
    }

    public BinopExpr getBinopExpr() {
        return binopExpr;
    }

    private BinopExpr binopExpr;

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof MyBinop)) {
            return false;
        }
        return this.binopExpr.equivTo(((MyBinop) obj).getBinopExpr());
    }

    public static MyBinop getInstance(BinopExpr be) {
        return new MyBinop(be);
    }
}
