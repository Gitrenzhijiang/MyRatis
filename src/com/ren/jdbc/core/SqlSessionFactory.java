package com.ren.jdbc.core;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.ren.jdbc.config.Configuration;
import com.ren.jdbc.config.ConfigBuilder;
import com.ren.jdbc.sql.BoundSqlBuilder;

public class SqlSessionFactory {
    /**
     * 构建核心配置对象
     */
    private Configuration configuration = null;
    private DataSource dataSource = null;
    public SqlSession openSession() {
        return openSession(false, true);
    }
    public SqlSession openSession(boolean autocommit) {
        return openSession(autocommit, true);
    }
    public SqlSession openSession(boolean autocommit, boolean usecache) {
        SqlSession sqlSession = null;
        try {
            sqlSession =  new DefaultSqlSession(dataSource.getConnection(), configuration, usecache, autocommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sqlSession;
    }
    Configuration getConfiguration() {
        return configuration;
    }
    void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
}
