package com.ren.jdbc.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ren.jdbc.sql.BoundSql;
import com.ren.jdbc.sql.SqlType;

public class Configuration {
    
    private final Map<Class, PJConfig> pojoConfigMap = new HashMap<>();
    
    private final Map<Class, Map<SqlType, BoundSql>> sqlsMap = new HashMap<>();
    
    public Map<Class, PJConfig> getPojoConfigMap() {
        return pojoConfigMap;
    }
    public void registerSqlMap(Class clazz, Map<SqlType, BoundSql> sqls) {
        this.sqlsMap.put(clazz, sqls);
    }
    public Map<SqlType, BoundSql> getSqlsByClass(Class clazz){
        return sqlsMap.get(clazz);
    }
    
}
