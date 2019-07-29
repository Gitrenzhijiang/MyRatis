package com.ren.test2;

import java.util.List;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.Many;
import com.ren.jdbc.annotation.One;
import com.ren.jdbc.annotation.POJO;
/**
 * 对应 数据库 tb_user 表
 * @author REN
 */
@POJO("tb_user")
@Generatekey(true) // 使用自增主键
public class User {
    @Id
    private int id;
    @Column("name")
    private String name;
    @Column("password")
    private String password;
    @Column("email")
    private String email;
    @Column("real_name")
    private String realName;
    // 当前用户所拥有的角色   一个用户一个角色
    @One(value = "role_id", cascade = true)
    private Role myRole;
    
    @Many(value = Questionnaire.class, cascade = true)
    private List<Questionnaire> questionnaires;
    
    
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public Role getMyRole() {
        return myRole;
    }
    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }
    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }
    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email + ", realName="
                + realName + ", myRole=" + myRole + ", questionnaires=" + questionnaires + "]";
    }
    
}
