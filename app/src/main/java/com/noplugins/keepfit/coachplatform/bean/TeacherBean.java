package com.noplugins.keepfit.coachplatform.bean;

import java.util.List;

public class TeacherBean {

    /**
     * teacherNum : CUS19092550811153
     * teacherName : 张晗
     * phone : 13623421234
     * year : 2
     * sex : 1
     * card :
     * label : 5,6,7
     * logoUrl : http://qnimg.ahcomg.com/jiaolian3
     * teacherType : 3
     * tips : 每天把牢骚拿出来晒晒太阳，心情就不会缺钙
     * grade : 950
     * skill : 1,2,3
     * serviceDur : 830
     * createDate : 2019-09-12 11:11:49
     * updateDate : 2019-09-12 11:11:49
     * deleted : 0
     * status : 2
     * skillList : ["瘦身塑形","增重增肌","体能训练"]
     * finalGrade : 9.5
     */

    private String teacherNum;
    private String teacherName;
    private String phone;
    private int year;
    private int sex;
    private String card;
    private String label;
    private String logoUrl;
    private int teacherType;
    private String tips;
    private int grade;
    private String skill;
    private int serviceDur;
    private String createDate;
    private String updateDate;
    private int deleted;
    private int status;
    private double finalGrade;
    private List<String> skillList;

    public String getTeacherNum() {
        return teacherNum;
    }

    public void setTeacherNum(String teacherNum) {
        this.teacherNum = teacherNum;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public int getTeacherType() {
        return teacherType;
    }

    public void setTeacherType(int teacherType) {
        this.teacherType = teacherType;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getServiceDur() {
        return serviceDur;
    }

    public void setServiceDur(int serviceDur) {
        this.serviceDur = serviceDur;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public List<String> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<String> skillList) {
        this.skillList = skillList;
    }
}
