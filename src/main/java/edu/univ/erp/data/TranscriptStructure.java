package edu.univ.erp.data;

public class TranscriptStructure {
    private String courseCode;
    private String courseName;
    private String instructor;
    private int sectionID;

    private String component;
    private double score;
    private double maxScore;

    public TranscriptStructure(
            String courseCode, String courseName, String instructor, int sectionID, String component, double score, double maxScore

    ) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructor = instructor;
        this.sectionID = sectionID;
        this.component = component;
        this.score = score;
        this.maxScore = maxScore;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public int getSectionID() {
        return sectionID;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getInstructor() {
        return instructor;
    }
    public String getComponentName() {
        return component;

    }
    public double getScore() {
        return score;
    }
    public double getMaxScore() {
        return maxScore;
    }
}
