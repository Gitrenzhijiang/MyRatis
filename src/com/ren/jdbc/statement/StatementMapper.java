package com.ren.jdbc.statement;
/**
 * 这个类映射了实际要执行的
 * Connection,
 * BoundSql, 已经完全的
 * SQL,实际执行的sql
 * cascade 是否级联,
 * @author REN
 *
 */

import java.sql.Connection;

import com.ren.jdbc.sql.BoundSql;

public class StatementMapper {
    
//    public StatementMapper(Connection conn, BoundSql boundSql, boolean cascade) {
//        super();
//        this.conn = conn;
//        this.boundSql = boundSql;
//        this.sql = boundSql.getSql();
//        this.cascade = cascade;
//    }
    public StatementMapper(Connection conn, BoundSql boundSql) {
        super();
        this.conn = conn;
        this.boundSql = boundSql;
        this.sql = boundSql.getSql();
    }
    private Connection conn;
    private BoundSql boundSql;
    private String sql = null;
    private boolean cascade = false;
    
    public boolean isCascade() {
        return cascade;
    }
    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }
    public Connection getConn() {
        return conn;
    }
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    public BoundSql getBoundSql() {
        return boundSql;
    }
    public void setBoundSql(BoundSql boundSql) {
        this.boundSql = boundSql;
    }
    public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (cascade ? 1231 : 1237);
        result = prime * result + ((sql == null) ? 0 : sql.hashCode());
        return result;
    }
    /**
     * statementMapper是否相同是根据sql和cascade来判断的,
     * 本来需要考虑connection,但是对于结果集来说，没有任何影响
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatementMapper other = (StatementMapper) obj;
        if (cascade != other.cascade)
            return false;
        if (sql == null) {
            if (other.sql != null)
                return false;
        } else if (!sql.equals(other.sql))
            return false;
        return true;
    }
    
}
