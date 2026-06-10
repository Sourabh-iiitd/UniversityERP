package edu.univ.erp.data;

public class EnrollmentDetail {
    private final int enrollmentId;
    private final int studentId;
    private final String studentName;

    public EnrollmentDetail(int enrollmentId, int studentId, String studentName) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    @Override
    public String toString() {
        return studentName;
    }
}