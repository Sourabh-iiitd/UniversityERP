package edu.univ.erp.data;

public class Course {
    private final int courseId;
    private final String courseCode;
    private final String courseName;
    private final int credits;

    public Course(int courseId, String courseCode, String courseName, int credits) {
        this.courseId=courseId;
        this.courseCode=courseCode;
        this.courseName=courseName;
        this.credits=credits;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
        
    }
    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}