package sql;

import top.viewv.abstraction.Silica;
import top.viewv.function.SilicaFinder;

import java.util.Set;

public class SQLDetector {
    public Set<Silica> detect(Set<String> classFilter){
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
//        for (Silica silica : relevantSilicas) {
//            Set<Use> useSet = Analyzer.getUseSet(silica);
//            for (Use use : useSet) {
//                if (use.getSelectedColumn() != null) {
//                    if (use.getCodepoint().getStatement() instanceof IfStmt) {
//                        System.out.println(use);
//                    }
//                }
//            }
//        }
        return relevantSilicas;
    }
}
