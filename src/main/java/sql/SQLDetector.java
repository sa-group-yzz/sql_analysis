package sql;

import top.viewv.abstraction.Silica;
import top.viewv.function.SilicaFinder;

import java.util.Set;

public class SQLDetector {
    public Set<Silica> detect(Set<String> classFilter){
        Set<Silica> relevantSilicas = SilicaFinder.find("^(select (?!(count|avg|sum|min|max)(\\(| \\())(?!.* limit 0)).*", classFilter);
        return relevantSilicas;
    }
}
