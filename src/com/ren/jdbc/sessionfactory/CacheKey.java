package com.ren.jdbc.sessionfactory;
/**
 * 这个类定义了是否是同一条查询的条件
 * @author REN
 *
 */

import java.util.Arrays;

import com.ren.jdbc.statement.StatementMapper;

public class CacheKey {
    private StatementMapper statementMapper; 
    private Class clazz; // 结果集元素的class
    private Object[] objs;// 参数数组
    
    
    
    
    public CacheKey(StatementMapper statementMapper, Class clazz, Object[] objs) {
        super();
        this.statementMapper = statementMapper;
        this.clazz = clazz;
        this.objs = objs;
    }
    public StatementMapper getStatementMapper() {
        return statementMapper;
    }
    public void setStatementMapper(StatementMapper statementMapper) {
        this.statementMapper = statementMapper;
    }
    public Class getClazz() {
        return clazz;
    }
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
    public Object[] getObjs() {
        return objs;
    }
    public void setObjs(Object[] objs) {
        this.objs = objs;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + Arrays.hashCode(objs);
        result = prime * result + ((statementMapper == null) ? 0 : statementMapper.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CacheKey other = (CacheKey) obj;
        if (clazz == null) {
            if (other.clazz != null)
                return false;
        } else if (!clazz.equals(other.clazz))
            return false;
        if (!Arrays.equals(objs, other.objs))
            return false;
        if (statementMapper == null) {
            if (other.statementMapper != null)
                return false;
        } else if (!statementMapper.equals(other.statementMapper))
            return false;
        return true;
    }
    
    
}
