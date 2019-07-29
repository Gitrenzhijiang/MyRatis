package com.ren.test2;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.POJO;

@POJO("tb_permission")
@Generatekey(true) // 使用自增主键
public class Permission {
    @Id
    private int id;
    @Column
    private String name;
    @Column
    private String desc;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    @Override
    public String toString() {
        return "Permission [id=" + id + ", name=" + name + ", desc=" + desc + "]";
    }
    
}
