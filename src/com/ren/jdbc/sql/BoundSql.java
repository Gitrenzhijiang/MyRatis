package com.ren.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Sql语句
 * @author REN
 *
 */
public class BoundSql {
    public static final String SELECT = "SELECT";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE";
    public static final String INSERT = "INSERT INTO";
    
    public static final String WHERE = "WHERE";
    public static final String AND = "and";
    public static final String FROM = "FROM";
    public static final String LEFT = "(";
    public static final String RIGHT = ")";
    public static final String VALUES = "values";
    public static final String SET = "set";
    public static final String DOT = ".";
    public static final String DOU = ",";
    public static final String QM = "?";
    public static final String SPACE = " ";
    
    public static final String EQUAL = "=";
    public static final String NotEQUAL = "!=";
    public static final String LIMIT = "limit";
    // sql语句的类型 SELECT DELETE UPDATE INSERT 
    private SqlType type = null; 
    public SqlType getType() {
        return type;
    }
    public void setType(SqlType type) {
        this.type = type;
    }
    /**
     * insert into person (name,age) values (?,?)
     * update person set name = ? where xx=? and yy = ?
     * select id,name,age from person where xx=?
     * delete from person where 
     */
    
 // table.name,table.name 查询时候的view和插入时候的views
    // update 的时候也需要 它是set xxx.xx 这些name
    private List<String> view; 
    private List<String> tables; // 查询时可能会有多个table
     // [and,or] name1 = ? [and,or] table.xx=tab2=yy
    private String commons;
    public BoundSql() {}
    public BoundSql(BoundSql other) {
        this.setType(other.getType());
        
        this.setTables(new ArrayList<>(other.getTables()));
        if (other.getView()!=null)
            this.setView(new ArrayList<>(other.getView()));
        this.myargs = new ArrayList<>();
    }
    /**
     * 生成SQL
     * @return
     */
    public String getSql() {
        StringBuffer sb = new StringBuffer();
        switch (type) {
        case INSERT:
            sb.append(INSERT);
            sb.append(SPACE + oneTable() + SPACE);
            sb.append(LEFT + views2String() + RIGHT);
            sb.append(SPACE + VALUES + SPACE);
            sb.append(LEFT + quesComma(view.size()) + RIGHT);
            break;
        case UPDATE:
            sb.append(UPDATE);
            sb.append(SPACE + oneTable() + SPACE);
            sb.append(viewOnUpdate());
            sb.append(commons());
            break;   
        case DELETE:
            sb.append(DELETE);
            sb.append(SPACE + FROM);
            sb.append(SPACE + oneTable() + SPACE);
            sb.append(commons());
            break; 
        case SELECT:
            sb.append(SELECT);
            sb.append(SPACE + views2String() + SPACE);
            sb.append(FROM + SPACE);
            sb.append(SPACE + tables() + SPACE);
            sb.append(commons());
            break;   
        default:
            throw new RuntimeException("未知SQL类型");
        }
        return sb.toString();
    }
    private boolean checkCommons() {
        if (commons == null || "".equals(commons.trim())) {
            return false;
        }
        return true;
    }
    private String commons() {
        if (checkCommons()) {
            if (!isOnlyLimit())
                return SPACE + WHERE + SPACE + this.commons;
            else {
                return SPACE + this.commons;
            }
        }
        return "";
    }
    private boolean isOnlyLimit() {
        String s = this.commons.replace(" ", "");
        if (s.equalsIgnoreCase("limit?,?") || "limit?".equalsIgnoreCase(s)) {
            return true;
        }
        return false;
    }
    private String tables() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < tables.size();i++) {
            sb.append(tables.get(i));
            
            if (i < tables.size()-1) {
                sb.append(DOU);
            }
            
        }
        return sb.toString();
    }
    private String oneTable() {
        if (tables.size() != 1) {
            throw new RuntimeException("错误的table:" + tables);
        }
        return tables.get(0);
    }
    private String viewOnUpdate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < view.size();i++) {
            if (i == 0)
                sb.append(SET + SPACE);
            sb.append(view.get(i));
            sb.append(EQUAL);
            sb.append(QM);
            if (i != view.size()-1)
                sb.append(DOU);
        }
        return sb.toString();
    }
    private String views2String() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < view.size();i++) {
            sb.append(view.get(i));
            if (i != view.size()-1)
                sb.append(DOU);
        }
        return sb.toString();
    }
    // ?,?,?
    private String quesComma(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < n-1;i++) {
            sb.append(QM);
            sb.append(DOU);
        }
        sb.append(QM);
        return sb.toString();
    }
    public List<String> getView() {
        return view;
    }
    public void setView(List<String> view) {
        this.view = view;
    }
    public List<String> getTables() {
        return tables;
    }
    public void setTables(List<String> tables) {
        this.tables = tables;
    }
    public void setCommons(String commons) {
        this.commons = commons;
    }
    // ==========
    private List<Object> myargs = new ArrayList<>();
    public Object[] getArgs() {
        return myargs.toArray();
    }
    public void addArg(Object obj) {
        this.myargs.add(obj);
    }
    public void addArgs(Object[] obj) {
        for (Object o:obj)
            this.myargs.add(o);
    }
    /**
     * 如果这个插入的sql 使用自增, 返回true.
     */
    private boolean useGeneratedKey;
    public boolean isUseGeneratedKey() {
        return useGeneratedKey;
    }
    public void setUseGeneratedKey(boolean useGeneratedKey) {
        this.useGeneratedKey = useGeneratedKey;
    }
    
}
