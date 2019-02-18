package com.ren.jdbc.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigIntegerTypeHandler implements TypeHandler<BigInteger>{

    @Override
    public void setParameter(PreparedStatement ps, int i, BigInteger parameter) throws SQLException {
        ps.setBigDecimal(i, new BigDecimal(parameter));
    }

    @Override
    public BigInteger getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getBigDecimal(columnName).toBigInteger();
    }

    @Override
    public BigInteger getResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getBigDecimal(columnIndex).toBigInteger();
    }
    
}
