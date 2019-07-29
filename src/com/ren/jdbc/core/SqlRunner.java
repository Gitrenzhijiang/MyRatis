package com.ren.jdbc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.type.TypeHandler;
import com.ren.jdbc.type.TypeHandlerRegistry;

public class SqlRunner {
    private Connection conn;
    private TypeHandlerRegistry typeHandlerRegistry;
    public SqlRunner(Connection conn) {
        this.conn = conn;
        this.typeHandlerRegistry = new TypeHandlerRegistry();
    }

    /**
     * 如果结果集为空，返回null;如果结果集的数量有多个，返回第一个
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public Map<String, Object> selectOne(String sql, Object... args) throws SQLException {
        System.out.println("selectOne:" + sql);
        PreparedStatement ps = conn.prepareStatement(sql);
        setParameters(ps, args);
        List<Map<String, Object>> ret = getResults(ps.executeQuery());
        if (ret.size() <= 0) {
            return null;
        }
        return ret.get(0);
    }
    /**
     * 如果结果集为空，返回一个空的List.
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> selectAll(String sql, Object... args) throws SQLException {
        System.out.println("selectAll:" + sql);
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            setParameters(ps, args);
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> ret = getResults(rs);
            return ret;
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 如果返回Null 表示使用自增主键但是主键没有/不存在
     * 
     * @param sql
     * @param args
     * @return
     * @throws SQLException
     */
    public Integer insert(String sql, boolean useGeneratedKey, Object... args) throws SQLException {
        System.out.println("Insert:" + sql);
        PreparedStatement ps;
        if (useGeneratedKey) {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            ps = conn.prepareStatement(sql);
        }

        try {
            setParameters(ps, args);
            int updateCount = ps.executeUpdate();
            if (useGeneratedKey) {
                List<Map<String, Object>> keys = getResults(ps.getGeneratedKeys());
                if (keys.size() == 1) {
                    Map<String, Object> key = keys.get(0);
                    Iterator<Object> i = key.values().iterator();
                    if (i.hasNext()) {
                        Object genkey = i.next();
                        if (genkey != null) {
                            try {
                                return Integer.parseInt(genkey.toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return null;
            }
            return updateCount;
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int update(String sql, Object... args) throws SQLException {
        System.out.println("update:" + sql);
        PreparedStatement stmt = conn.prepareStatement(sql);
        try {
            setParameters(stmt, args);
            
            return stmt.executeUpdate();
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void run(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.execute(sql);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setParameters(PreparedStatement ps, Object... args) throws SQLException {
        if (args == null)
            return;
        for (int i = 0, n = args.length; i < n; i++) {
            if (args[i] == null) {
                ps.setObject(i + 1, null);
            }else {
//                TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(args[i].getClass());
//                typeHandler.setParameter(ps, i + 1, args[i]);
                ps.setObject(i + 1, args[i]);
            }
            
        }
    }
    /**
     * 如果没有一行结果，将返回一个空的List
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<Map<String, Object>> getResults(ResultSet rs) throws SQLException {
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<String> columns = new ArrayList<String>();
            List<TypeHandler<?>> typeHandlers = new ArrayList<TypeHandler<?>>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 0, n = rsmd.getColumnCount(); i < n; i++) {
                columns.add(rsmd.getColumnLabel(i + 1));
                try {
                    Class<?> type = Class.forName(rsmd.getColumnClassName(i + 1));
                    TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(type);
                    if (typeHandler == null) {
                        typeHandler = typeHandlerRegistry.getTypeHandler(Object.class);
                    }
                    typeHandlers.add(typeHandler);
                } catch (Exception e) {
                    typeHandlers.add(typeHandlerRegistry.getTypeHandler(Object.class));
                }
            }
            while (rs.next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                for (int i = 0, n = columns.size(); i < n; i++) {
                    String name = columns.get(i);
                    Object value = rs.getObject(i+1);
                    // 处理数据库Null值
                    if (value == null) {
                        row.put(name, null);
                        continue;
                    }
                    TypeHandler<?> handler = typeHandlers.get(i);
                    row.put(name, handler.getResult(rs, name));
                }
                list.add(row);
            }
            return list;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
