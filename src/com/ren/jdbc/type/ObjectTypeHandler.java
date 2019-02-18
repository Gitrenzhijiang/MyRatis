package com.ren.jdbc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectTypeHandler implements TypeHandler<Object>{

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter) throws SQLException {
        ps.setObject(i, parameter);
    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName);
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex);
    }

}
