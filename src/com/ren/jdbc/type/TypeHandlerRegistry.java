package com.ren.jdbc.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public final class TypeHandlerRegistry {
    private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<>();
    public TypeHandlerRegistry() {
        register(Date.class, new DateTypeHandler());
        register(Timestamp.class, new TimestampTypeHandler());
        register(BigDecimal.class, new BigDecimalTypeHandler());
        register(BigInteger.class, new BigIntegerTypeHandler());
        register(String.class, new StringTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(Integer.class, new IntegerTypeHandler());
        register(Object.class, new ObjectTypeHandler());
    }
    public void register(Class<?> javaClass, TypeHandler<?>handler) {
        ALL_TYPE_HANDLERS_MAP.put(javaClass, handler);
    }
    public <T> TypeHandler<T> getTypeHandler(Class<T> clazz){
        return (TypeHandler<T>) ALL_TYPE_HANDLERS_MAP.get(clazz);
    }
}
