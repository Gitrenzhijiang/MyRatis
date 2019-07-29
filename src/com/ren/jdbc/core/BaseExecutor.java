package com.ren.jdbc.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.sql.SqlType;
import com.ren.jdbc.statement.CascadeBeanHandler;
import com.ren.jdbc.statement.StatementMapper;

public class BaseExecutor implements Executor {
    
    private Configuration config;
    
    public BaseExecutor(Configuration config) {
        super();
        this.config = config;
    }
    @Override
    public <E> E queryOne(StatementMapper sm, Class<E> clazz, Object... objects) {
        SqlRunner sr = new SqlRunner(sm.getConn());
        try {
            Map<String, Object> map = sr.selectOne(sm.getSql(), objects);
            if (map == null) {
                return null;
            }
            E e = config.getTableConfigMap().get(clazz)
                    .mapToBean(map, clazz);
            new CascadeBeanHandler<>(config, sm, e, map).fillCascade();
            return e;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <E> List<E> query(StatementMapper sm, Class<E>clazz, Object... objs) {
        SqlRunner sr = new SqlRunner(sm.getConn());
        List<E> ret = new ArrayList<>();
        try {
            List<Map<String, Object>> lists = sr.selectAll(sm.getSql(), objs);
            for (int i = 0;i < lists.size();i++) {
                E e = config.getTableConfigMap().get(clazz)
                        .mapToBean(lists.get(i), clazz);
                // 把配置的级联查询的部分填充
                new CascadeBeanHandler<>(config, sm, e, lists.get(i)).fillCascade();
                ret.add(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    @Override
    public int update(StatementMapper sm, Object... objects) {
        SqlRunner sr = new SqlRunner(sm.getConn());
        try {
            if (SqlType.INSERT.equals(sm.getBoundSql().getType())) {
                return sr.insert(sm.getSql(), sm.getBoundSql().isUseGeneratedKey(),  objects);
            }
            return sr.update(sm.getSql(), objects);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void run(StatementMapper sm) {
        SqlRunner sr = new SqlRunner(sm.getConn());
        try {
            sr.run(sm.getSql());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Transaction getTransaction() {
        
        return null;
    }

    @Override
    public void close(boolean forceRollback) {
        
        
    }

    @Override
    public boolean isClosed() {
        
        return false;
    }

}
