package transform;

import soot.Body;
import sql.FetchSQLUsage;

public class DeleteUnreachableBranch {
    private FetchSQLUsage sqlUsage;
    private Body body;

    public DeleteUnreachableBranch(FetchSQLUsage sqlUsage, Body body) {
        this.sqlUsage = sqlUsage;
    }


    public Body delete() {
        return null;
    }
}
