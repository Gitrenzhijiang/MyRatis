package com.ren.test2;

import java.util.Date;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.One;
import com.ren.jdbc.annotation.POJO;
@POJO("tb_questionnaire")
@Generatekey(true)
public class Questionnaire {
    @Id
    private int id;
    @Column("title")
    private String title;
    @Column
    private Date ctime;
    @Column
    private Date dtime;
    @Column
    private int publish; // 1:发布   0: 未发布
    @One(value = "puser_id", cascade = true)
    private User puser;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Date getCtime() {
        return ctime;
    }
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
    public Date getDtime() {
        return dtime;
    }
    public void setDtime(Date dtime) {
        this.dtime = dtime;
    }
    public int getPublish() {
        return publish;
    }
    public void setPublish(int publish) {
        this.publish = publish;
    }
    public User getPuser() {
        return puser;
    }
    public void setPuser(User puser) {
        this.puser = puser;
    }
    @Override
    public String toString() {
        return "Questionnaire [id=" + id + ", title=" + title + ", ctime=" + ctime + ", dtime=" + dtime + ", publish="
                + publish + ", puser=" + puser + "]";
    }
}
