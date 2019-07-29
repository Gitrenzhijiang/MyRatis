package com.ren.jdbc.cache;

import java.util.LinkedHashMap;
/**
 * 最近最少使用 置换算法
 * @author REN
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends AbstractCache<K, V>{
    public static void main(String[] args) {
        LRUCache<String, String> cache = new LRUCache<>(10);
        for (int i = 0;i < 10;i++) {
            cache.put(""+i, ""+i);
        }
        System.out.println(cache.isFull());
        System.out.println(cache.get("0"));
        cache.put("11", "11");
        System.out.println(cache.get("0")); // 0 被移除, 最近最少使用的移除
        System.out.println(cache.size()); //
    }
    public LRUCache(int capacity) {
        super(capacity);
        map = new LinkedHashMap<K, CacheValue<K, V>>(capacity + 1, 1.0f, true) {
            
            private static final long serialVersionUID = 134679065L;
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, CacheValue<K, V>> eldest) {
                return LRUCache.this.removeEldestEntry(size());
            }
        };
    }
    private boolean removeEldestEntry(int curSize) {
        if (curSize == 0) {
            return false;
        }
        return curSize > capacity;
    }
    @Override
    protected void eliminate() {
        // do nothing but implement in linked hash map
    }
    
}
