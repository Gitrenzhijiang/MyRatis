package com.ren.test2;

import java.util.List;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.Many;
import com.ren.jdbc.annotation.POJO;

/**
 * 对应物理表 tb_role
 * @author REN
 *
 */
@POJO("tb_role")
@Generatekey(true) // 使用自增主键
public class Role {
    @Id("id")
    private int id;
    @Column("name")
    private String name;
    @Column("desc")
    private String desc;
    
    
    @Many(value = User.class, cascade = true)
    private List<User> userList;
    
    @Many(value = RolePermission.class, cascade = true)
    private List<User> rolePermissions;
    
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
    public List<User> getUserList() {
        return userList;
    }
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
    public List<User> getRolePermissions() {
        return rolePermissions;
    }
    public void setRolePermissions(List<User> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
    @Override
    public String toString() {
        return "Role [id=" + id + ", name=" + name + ", desc=" + desc + ", userList=" + userList + ", rolePermissions="
                + rolePermissions + "]";
    }
    
}
