package com.ren.test2;

import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.One;
import com.ren.jdbc.annotation.POJO;

/**
 * 对应 tb_role_permission 
 * 关联表
 */
@POJO("tb_role_permission")
@Generatekey(true)
public class RolePermission {
    @Id
    private int id;
    // 这个表 与另一个表是  一对一关系，当查出这一行记录时, cascade=true则 把相关联表的那一行也查出. 
    @One(value = "role_id", cascade = true)
    private Role role;
    @One(value = "permission_id", cascade = true)
    private Permission permission;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public Permission getPermission() {
        return permission;
    }
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    @Override
    public String toString() {
        return "RolePermission [id=" + id + ", role=" + role + ", permission=" + permission + "]";
    }
    
}
