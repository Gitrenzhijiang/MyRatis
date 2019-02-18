package com.ren.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.ren.jdbc.cache.Cache;
import com.ren.jdbc.cache.LRUCache;

public class CacheTest1 {
    public static void main(String[] args) {
//        test_fifo();
        test_linkedlist();
    }
    public static void test_linkedlist() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>(30, 1.0f, true);
        for (int i = 0; i < 30; i++) {
            map.put(i+"", i+"");
        }
        // linkedhashmap 在开启access 次数排序时，每一次访问一个元素都会导致hashmap结构的变化，导致modcount++,从而下一次迭代器检查后抛出异常
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String string = (String) iterator.next();
//            System.out.println(map.get(string));
            System.out.println(string);
        }
    }
    
    private static void test_fifo() {
//        Cache<String, String> cache = new FIFOCache<>(10);
        Cache<String, String> cache = new LRUCache<>(10);
        for (int i = 0;i < 10;i++) {
            cache.put(""+i, ""+i);
        }
        System.out.println(cache.isFull());
//        System.out.println(cache.get("0"));
        cache.put("11", "11");
        System.out.println(cache.get("0"));
        System.out.println(cache.size());
        
    }
    static List<String> list;
    static {
        list = new ArrayList<>();
        for (int i = 0;i < 30;i++) {
            list.add("imp:"+i);
        }
    }
}
