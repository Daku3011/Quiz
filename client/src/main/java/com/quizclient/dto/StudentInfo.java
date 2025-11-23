package com.quizclient.dto;

public class StudentInfo {
    private Long studentId;
    private String name;
    private String enrollment;

    public StudentInfo() {}
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEnrollment() { return enrollment; }
    public void setEnrollment(String enrollment) { this.enrollment = enrollment; }
}
