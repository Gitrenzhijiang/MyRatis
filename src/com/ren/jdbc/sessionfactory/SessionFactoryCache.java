package com.ren.jdbc.sessionfactory;

import com.ren.jdbc.cache.Cache;
import com.ren.jdbc.cache.LRUCache;

public class SessionFactoryCache {
    
    private static final int DEFAULT_CACHE_SIZE = 77;
    
    protected Cache<CacheKey, Object> cache;
    public SessionFactoryCache() {
        this.cache = new LRUCache<>(DEFAULT_CACHE_SIZE);
    }
    public SessionFactoryCache(Cache<CacheKey, Object> cache) {
        this.cache = cache;
    }
    
    
    /**   ================下面是提供给Executor的线程安全的方法============**/
    
    /**
     * 不因为过期而被清除
     * @param ck
     * @param ret
     */
    public void put(CacheKey ck, Object ret) {
        cache.put(ck, ret);
    }
    /**
     * 
     * @param ck
     * @param ret
     * @param actTime -1 表示不会过期；actTime>0表示会话存储的时间，类似与jee session, 毫秒值
     */
    public void put(CacheKey ck, Object ret, long actTime) {
        cache.put(ck, ret, actTime);
    }
    /**
     * 从缓存中取出
     * @param ck
     * @return
     */
    public Object get(CacheKey ck) {
        if (ck == null) {
            return null;
        }
        return cache.get(ck);
    }
    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
    }
    /**
     * 存在的话，更新并返回旧值；否则返回null
     * 
     * @param ck
     * @param newV
     * @return
     */
    public Object putIfExist(CacheKey ck, Object newV, long actTime) {
        synchronized (this) {
            Object oldV = get(ck);
            if (oldV != null) {
                cache.put(ck, newV, actTime);
            }
            return oldV;
        }
    }
    /**
     * 永不过期
     * @param ck
     * @param newV
     * @return
     */
    public Object putIfExist(CacheKey ck, Object newV) {
        synchronized (this) {
            Object oldV = get(ck);
            if (oldV != null) {
                cache.put(ck, newV);
            }
            return oldV;
        }
    }
}
