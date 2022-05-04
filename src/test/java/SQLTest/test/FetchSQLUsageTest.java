package SQLTest.test;

import com.alibaba.druid.stat.TableStat;
import org.junit.Test;
import sql.FetchSQLUsage;

import java.util.Collections;

public class FetchSQLUsageTest extends BaseTest{
    @Test
    public void testFetchSQLUsage() {
        FetchSQLUsage fetchSQLUsage = new FetchSQLUsage(Collections.singletonList("SQLTest.testcase.Case1"));
        for (String sql : fetchSQLUsage.getQuerys()) {
            System.out.println(sql);
        }

        for (TableStat.Condition condition : fetchSQLUsage.getConditions()) {
            System.out.println(condition);
        }
    }
}
