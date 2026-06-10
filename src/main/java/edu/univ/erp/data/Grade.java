package edu.univ.erp.data;

public class Grade {
    private final int gradeId;
    private final int enrollmentId;
    private final String assessment;
    private final double score;

    public Grade(int gradeId, int enrollmentId, String assessment, double score) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.assessment = assessment;
        this.score = score;
    }

    public int getGradeId() { return gradeId; }
    public int getEnrollmentId() { return enrollmentId; }
    public String getAssessment() { return assessment; }
    public double getScore() { return score; }
}