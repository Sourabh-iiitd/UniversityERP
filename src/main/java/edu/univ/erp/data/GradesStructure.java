package edu.univ.erp.data;

public class GradesStructure {

    private int sectionID;
    private String courseCode;
    private String courseName;
    private String instructor;

    private String componentName;
    private double score;
    private double maxScore;

    public GradesStructure(int SectionID, String courseCode, String courseName, String instructor, String componentName, double score, double maxScore) {
        this.sectionID = SectionID;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructor = instructor;
        this.componentName = componentName;
        this.score = score;
        this.maxScore = maxScore;
    }

    public int getSectionID() {
        return sectionID;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getInstructor() {
        return instructor;
    }
    public String getComponentName() {
        return componentName;

    }
    public double getScore() {
        return score;
    }
    public double getMaxScore() {
        return maxScore;
    }
}
