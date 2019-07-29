package com.ren.jdbc.config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.ren.jdbc.sql.BoundSql;
import com.ren.jdbc.sql.SqlType;

/**
 * 线程安全类;
 */
public class Configuration {


    /**
     * POJO类型-> 基本最初的四种类型的 BoundSQL
     */
    private final Map<Class, Map<SqlType, BoundSql>> sqlsMap = new ConcurrentHashMap<>();

    /**
     * POJO类型 -> 表的配置类对象
     */
    private final Map<Class, TableConfig> tableConfigMap = new ConcurrentHashMap<>();




    public void registerSqlMap(Class clazz, Map<SqlType, BoundSql> sqls) {
        this.sqlsMap.put(clazz, sqls);
    }
    public Map<SqlType, BoundSql> getSqlsByClass(Class clazz){
        return sqlsMap.get(clazz);
    }
    public Map<Class, TableConfig> getTableConfigMap() {
        return tableConfigMap;
    }
}
