package com.ren.jdbc.core;

import java.sql.Connection;
import java.util.List;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.statement.StatementMapper;
/**
 * @author REN
 *
 */
import com.ren.jdbc.statement.StatementMapperBuilder;
import com.ren.jdbc.utils.CommonUtils;
public class DefaultSqlSession implements SqlSession {
    // 是否自动提交
    private boolean autoCommit;
    // 是否使用缓存
    /***
     * 一级缓存忽略
     */
    private boolean usecache;
    
    public DefaultSqlSession(Connection popConnection, Configuration config,boolean usecache,boolean autoCommit) {
        
        this.conn = popConnection;
        this.config = config;
        this.usecache = usecache;
        this.autoCommit = autoCommit;
        this.smb = new StatementMapperBuilder();
        // 现在默认使用sessionFactory级别缓存
        this.executor = new CacheExecutor(new BaseExecutor(config));
    }
    
    @Override
    public <T> T selectOne(Class<T> clazz, Object id) {
        return executor.queryOne(smb.selectOneById(clazz, config, conn), clazz, id);
    }

    @Override
    public <E> List<E> selectList(Class<E> clazz, String condition, Object... values) {
        
        return executor.query(smb.selectList(clazz, config, conn, condition), clazz, values);
    }
    @Override
    public <E> List<E> selectList(Class<E> clazz) {
        
        return selectList(clazz, null, null);
    }
    private Executor executor;
    private Connection conn;
    private Configuration config;
    
    private StatementMapperBuilder smb;

    @Override
    public <E> int delete(E obj) {
        if (obj == null) {
            return 0;
        }
        try {
            // 从obj中拿到ids的参数数组
            StatementMapper sm = smb.deleteOne(obj.getClass(), config, conn, obj);
            return executor.update(sm, sm.getBoundSql().getArgs());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } 
    }

    @Override
    public <E> int update(E obj) {
        if (obj == null) {
            return 0;
        }
        try {
            StatementMapper sm = smb.updateOne(obj.getClass(), config, conn, obj);
            return executor.update(sm, sm.getBoundSql().getArgs());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public <E> int save(E obj) {
        if (obj == null) {
            return 0;
        }
        try {
            StatementMapper sm = smb.insertOne(obj.getClass(), config, conn, obj);
            return executor.update(sm, sm.getBoundSql().getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}

