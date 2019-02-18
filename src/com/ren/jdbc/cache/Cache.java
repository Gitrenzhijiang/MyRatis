package com.ren.jdbc.cache;

public interface Cache <K, V>{
    /**
     * 缓存是否已满
     * @return
     */
    boolean isFull();
    /**
     * 缓存的大小
     * @return
     */
    int size();
    /**
     * 获取key对应的缓存对象
     * @param key
     * @return
     */
    V get(K key);
    /**
     * 将对象放入缓存中,永不过期
     * @param key
     * @param value
     */
    void put(K key, V value);
    /**
     * @param key
     * @param value
     * @param existenceTime 生存时间 单位:毫秒值
     */
    void put(K key, V value, long existenceTime);
    /**
     * 移除指定缓存对象, 如果成功移除, 返回缓存对象的值.否则返回null
     * @param key
     */
    void removeByKey(K key);
    /**
     * 清空所有缓存对象
     */
    void clear();
    
    
}
