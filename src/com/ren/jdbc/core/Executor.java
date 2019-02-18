package com.ren.jdbc.core;

import java.util.List;

import com.ren.jdbc.statement.StatementMapper;

public interface Executor {
    /**
     * 如果无查询结果,返回一个空的列表
     * @param sm
     * @param clazz
     * @param objs
     * @return
     */
    <E> List<E> query(StatementMapper sm, Class<E> clazz, Object ...objs);
    
    int update(StatementMapper sm, Object...objects);
    /**
     * 如果无查询结果，返回一个null;否则，有多个，返回第一个
     * @param sm
     * @param clazz
     * @param objects
     * @return
     */
    <E> E queryOne(StatementMapper sm, Class<E> clazz, Object...objects);
    
    void run(StatementMapper sm);
    
    Transaction getTransaction();
    
    void close(boolean forceRollback);

    boolean isClosed();
}
