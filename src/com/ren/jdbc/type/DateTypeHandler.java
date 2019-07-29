package com.ren.jdbc.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DateTypeHandler implements TypeHandler<Date> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Date parameter) throws SQLException {
        ps.setTimestamp(i, new Timestamp(parameter.getTime()));
    }

    @Override
    public Date getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getDate(columnName);
    }

    @Override
    public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getDate(columnIndex);
    }

}
