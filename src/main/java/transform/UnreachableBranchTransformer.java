package transform;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.util.Chain;

import java.util.List;
import java.util.Map;

public class UnreachableBranchTransformer extends BodyTransformer {
    private Map<Stmt, GotoStmt> stmtToReplacement;
    private List<IfStmt> deadStmts;
    private String className;
    private Body sb;
    private Body eliminatedBody;

    public UnreachableBranchTransformer(Map<Stmt, GotoStmt> stmtToReplacement, List<IfStmt> deadStmts, String className) {
        this.stmtToReplacement = stmtToReplacement;
        this.deadStmts = deadStmts;
        this.className = className;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        this.sb = body;
        if (this.sb.getMethod().getDeclaringClass().getName().equals(this.className)) {
            removeStmts(this.deadStmts);
            replaceStmts(this.stmtToReplacement);
            this.eliminatedBody = body;
        }
    }

    protected void removeStmts(List<IfStmt> deadStmts) {
        Chain<Unit> units = sb.getUnits();

        for (IfStmt dead : deadStmts) {
            if (units.contains(dead)) {
                System.out.println("remove stmt");
                units.remove(dead);
                dead.clearUnitBoxes();
            }
        }
    }

    protected void replaceStmts(Map<Stmt, GotoStmt> stmtsToReplace) {
        Chain<Unit> units = sb.getUnits();
        for (Map.Entry<Stmt, GotoStmt> e : stmtsToReplace.entrySet()) {
            if (units.contains(e.getKey())) {
                System.out.println("replace stmt");
                units.swapWith(e.getKey(), e.getValue());
            }
        }
    }

    public Body getEliminatedBody() {
        return eliminatedBody;
    }
}
