package com.ren.jdbc.core;

import java.util.List;

import com.ren.jdbc.sessionfactory.CacheKey;
import com.ren.jdbc.sessionfactory.SessionFactoryCache;
import com.ren.jdbc.sessionfactory.SessionFactoryCacheFactory;
import com.ren.jdbc.statement.StatementMapper;
/**
 * 装饰者模式应用
 * @author REN
 * 主要考虑到 系统运行时查的时候更多，这种设定有一定的作用把
 * 解决重复查询的问题：这里没有解决, 需要使用并发缓存，Map + future的模式，给出计算过程 === 缓存击穿
 * 缓存穿透:大量请求查询一个不存在的数据，利用布隆过滤器来过滤请求
 * 缓存雪崩:大量的缓存的失效时间趋于同一时间,导致短时间内数据库压力太大
 * 缓存击穿:和重复查询的类似，对于一个key的并发请求太高，导致重复查询, 所以使用互斥锁,只让一个线程取查数据，其他线程等待
 */
public class CacheExecutor implements Executor{
    /**
     * 全局缓存增强Executor
     */
    private SessionFactoryCache sessionFactoryCache =
            SessionFactoryCacheFactory.getInstance();
    private final Executor executor;
    public CacheExecutor(Executor executor) {
        this.executor = executor;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> query(StatementMapper sm, Class<E> clazz, Object... objs) {
        // 拿到缓存中去查
        CacheKey ck = new CacheKey(sm, clazz, objs);
        Object ret = sessionFactoryCache.get(ck);
        // 缓存中有直接给出
        if (ret != null) {
            return (List<E>) ret;
        }else { // 从数据库查出来后，放入缓存
            List<E> retList = executor.query(sm, clazz, objs);
            sessionFactoryCache.put(ck, retList);
            return retList;
        }
    }
    @Override
    public int update(StatementMapper sm, Object... objects) {
        /**
         * sqlsession级别缓存的清除，这种方式来说，没有任何意义.
         * 但是没有更好的方案提出
         */
        sessionFactoryCache.clear();
        return executor.update(sm, objects);
    }
    @SuppressWarnings("unchecked")
    @Override
    public <E> E queryOne(StatementMapper sm, Class<E> clazz, Object... objects) {
     // 拿到缓存中去查
        CacheKey ck = new CacheKey(sm, clazz, objects);
        Object ret = sessionFactoryCache.get(ck);
        // 缓存中有直接给出
        if (ret != null) {
            return (E) ret;
        }else { // 从数据库查出来后，放入缓存
            E res = executor.queryOne(sm, clazz, objects);
            sessionFactoryCache.put(ck, res);
            return res;
        }
    }
    @Override
    public void run(StatementMapper sm) {
        sessionFactoryCache.clear();
        executor.run(sm);
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
