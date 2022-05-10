package SQLTest.test;

import com.alibaba.druid.stat.TableStat;
import top.viewv.abstraction.Use;

import java.util.HashMap;
import java.util.Set;

public class SQLTestResult{
    private HashMap<String, Object> result = new HashMap<>();

    public SQLTestResult(Set<Use> useSet, HashMap<String, TableStat.Condition> conditionHashMap){
        result.put("use",useSet);
        result.put("condition",conditionHashMap);
    }

    public HashMap<String, Object> getResult() {
        return result;
    }

    public Set<Use> getUseSet(){
        return (Set<Use>) result.get("use");
    }

    public HashMap<String, TableStat.Condition> getCondition(){
        return (HashMap<String, TableStat.Condition>) result.get("condition");
    }
}
