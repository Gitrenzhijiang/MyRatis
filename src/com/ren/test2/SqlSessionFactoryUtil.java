package com.ren.test2;

import com.ren.jdbc.core.SqlSessionFactory;
import com.ren.jdbc.core.SqlSessionFactoryBuilder;
/**
 * 单例工具类, 保证只有一个SqlSessionFactory
 * @author REN
 *
 */
public class SqlSessionFactoryUtil {
    private SqlSessionFactoryUtil() {}

    public static SqlSessionFactory getSessionFactory() {
        return Inner.ssf;
    }
    
    private static class Inner {
        @SuppressWarnings("static-access")
        final static SqlSessionFactory ssf = new SqlSessionFactoryBuilder()
                .build("MyRatis.properties");
    }
}
