package com.ren.jdbc.sessionfactory;
/**
 * 全局缓存 sessionFactoryCache级别缓存的单例工厂
 * @author REN
 *
 */
public class SessionFactoryCacheFactory {
    private SessionFactoryCacheFactory() {}
    private volatile static SessionFactoryCache sfc;
    
    public static SessionFactoryCache getInstance() {
        if (sfc == null) {
            synchronized (SessionFactoryCacheFactory.class) {
                if (sfc != null) {
                    return sfc;
                }
                sfc = new SessionFactoryCache();
            }
        }
        return sfc;
    }
}
