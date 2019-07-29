package com.ren.jdbc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampTypeHandler implements TypeHandler<Timestamp> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Timestamp parameter) throws SQLException {
        ps.setTimestamp(i, parameter);
    }

    @Override
    public Timestamp getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName);
    }

    @Override
    public Timestamp getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex);
    }

}
