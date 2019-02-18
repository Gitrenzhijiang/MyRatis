package com.ren.jdbc.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ren.jdbc.utils.BeanUtils;

public class PJConfig {
    /**
     * 是否使用了自增主键
     */
    private boolean useGenerate = false;
    
    private String tableName;
    private List<String> ids = new ArrayList<>(1);
    private List<String> columns = new ArrayList<>();
    
    private List<String> idJavas = new ArrayList<>();
    private List<String> colJavas = new ArrayList<>();
    
    private Map<String, String> onesMap = new LinkedHashMap<>(); // 在person表 中 teacher_id -> Teacher全名#[teacher的属性名]
    private Map<String, Class<?>> manys = new HashMap<>(); // 在teacher中的myStudents --> Student的class
    
    private Map<String, String> onesJavaName = new HashMap<>();  // 它的teacher->teacher_id
    
    private Map<String, String> onesSql = new HashMap<>(); // 加载 teacher 的关联sql
    private Map<String, String> manySql = new HashMap<>(); // 加载list<person> mystudents的关联sql
    private Map<String, Boolean> useCascadeMap = new HashMap<>(); //java属性(one,many)->级联查询
    
    private BeanMapNamePreHandler beanMapNamePreHandler = new BeanMapNamePreHandler();
    /**
     * 将数据库字段视图转换为java字段视图
     * @author REN
     * 不用管 ones
     */
    public class BeanMapNamePreHandler {
        public <T> T map(Map<String, Object> beanMap, Class<T> clazz){
            if (beanMap == null) {
                return null;
            }
            return BeanUtils.getBean(mapHand(beanMap), clazz);
        }
        
        private Map<String, Object> mapHand(Map<String, Object> beanMap){
            Map<String, Object> handMap = new HashMap<>();
            for (String key : beanMap.keySet()) {
                boolean put = false;
                for (int i = 0;i < ids.size();i++) {
                    if (ids.get(i).equals(key)) {
                        handMap.put(idJavas.get(i), beanMap.get(key));
                        put = true;
                        break;
                    }
                }
                if (!put) {
                    for (int i = 0;i < columns.size();i++) {
                        if (columns.get(i).equals(key)) {
                            handMap.put(colJavas.get(i), beanMap.get(key));
//                            put = true;
                            break;
                        }
                    }
                }
            }
            return handMap;
        }
    }
    
    public BeanMapNamePreHandler getBeanMapNamePreHandler() {
        return beanMapNamePreHandler;
    }
    public void setBeanMapNamePreHandler(BeanMapNamePreHandler beanMapNamePreHandler) {
        this.beanMapNamePreHandler = beanMapNamePreHandler;
    }
    public Map<String, Boolean> getUseCascadeMap() {
        return useCascadeMap;
    }
    public void setUseCascadeMap(Map<String, Boolean> useCascadeMap) {
        this.useCascadeMap = useCascadeMap;
    }
    public Map<String, String> getOnesSql() {
        return onesSql;
    }
    public void setOnesSql(Map<String, String> onesSql) {
        this.onesSql = onesSql;
    }
    public Map<String, String> getManySql() {
        return manySql;
    }
    public void setManySql(Map<String, String> manySql) {
        this.manySql = manySql;
    }
    public List<String> getColumns() {
        return columns;
    }
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public List<String> getIds() {
        return ids;
    }
    public void setIds(List<String> ids) {
        this.ids = ids;
    }
    public Map<String, String> getOnesMap() {
        return onesMap;
    }
    public void setOnesMap(Map<String, String> onesMap) {
        this.onesMap = onesMap;
    }
    public Map<String, Class<?>> getManys() {
        return manys;
    }
    public void setManys(Map<String, Class<?>> manys) {
        this.manys = manys;
    }
    public boolean isUseGenerate() {
        return useGenerate;
    }
    public void setUseGenerate(boolean useGenerate) {
        this.useGenerate = useGenerate;
    }
    
    public Map<String, String> getOnesJavaName() {
        return onesJavaName;
    }
    public void setOnesJavaName(Map<String, String> onesJavaName) {
        this.onesJavaName = onesJavaName;
    }
    
    public List<String> getIdJavas() {
        return idJavas;
    }
    public void setIdJavas(List<String> idJavas) {
        this.idJavas = idJavas;
    }
    public List<String> getColJavas() {
        return colJavas;
    }
    public void setColJavas(List<String> colJavas) {
        this.colJavas = colJavas;
    }
    
    
}
