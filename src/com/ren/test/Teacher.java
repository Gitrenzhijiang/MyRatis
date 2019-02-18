package com.ren.test;

import java.util.List;

import com.ren.jdbc.annotation.Column;
import com.ren.jdbc.annotation.Generatekey;
import com.ren.jdbc.annotation.Id;
import com.ren.jdbc.annotation.Many;
import com.ren.jdbc.annotation.POJO;
@POJO
@Generatekey(true)
public class Teacher {
    @Id
    private Integer id;
    @Column(value="tname")
    private String name;
    @Many(value=Person.class, cascade=false)
    private List<Person> myStudents;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Person> getMyStudents() {
        return myStudents;
    }
    public void setMyStudents(List<Person> myStudents) {
        this.myStudents = myStudents;
    }
    @Override
    public String toString() {
        return "Teacher [id=" + id + ", name=" + name + ", myStudents=" + myStudents + "]";
    }
}
