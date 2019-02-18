package com.ren.jdbc.core;


import com.ren.ds.pool.DataSource;
import com.ren.jdbc.config.ConfigBuilder;

public class SqlSessionFactoryBuilder {
    public static SqlSessionFactory build(String file_src) {
        SqlSessionFactory ssf = new SqlSessionFactory();
        ssf.setConfiguration(new ConfigBuilder().build(file_src));
        ssf.setDataSource(new DataSource());
        return ssf;
    }
    public static SqlSessionFactory build() {
        SqlSessionFactory ssf = new SqlSessionFactory();
        ssf.setConfiguration(new ConfigBuilder().build());
        ssf.setDataSource(new DataSource());
        return ssf;
    }
}   
