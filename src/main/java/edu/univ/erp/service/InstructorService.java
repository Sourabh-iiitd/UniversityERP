package edu.univ.erp.service;

import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.EnrollmentDetail;
import edu.univ.erp.data.Grade;
import edu.univ.erp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstructorService {

    public List<CourseSectionStructure> getAssignedSections(int instructorId) {
        List<CourseSectionStructure> sections=new ArrayList<>();
        String sql="SELECT s.sectionID, c.courseCode, c.courseName, s.schedule, s.room, s.capacity, s.dropDeadline, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.sectionID=s.sectionID) as enrolledCount " +
                     "FROM sections s " +
                     "JOIN courses c ON s.courseID=c.courseID " +
                     "WHERE s.instructorID=?";
        try (Connection conn=DBConnection.getErpConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs=stmt.executeQuery();
            while (rs.next()) {
                sections.add(new CourseSectionStructure(
                        rs.getInt("sectionID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        null,//ins name not neededd
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolledCount"),
                        rs.getDate("dropDeadline")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }

    public List<EnrollmentDetail> getEnrolledStudents(int sectionId) {
        List<EnrollmentDetail> students=new ArrayList<>();
        String sql="SELECT e.enrollmentID, st.userID, st.firstName, st.lastName " +
                     "FROM enrollments e " +
                     "JOIN students st ON e.studentID=st.userID " +
                     "WHERE e.sectionID=?";
        try (Connection conn=DBConnection.getErpConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            ResultSet rs=stmt.executeQuery();
            while (rs.next()) {
                String fullName=rs.getString("firstName") + " " + rs.getString("lastName");
                students.add(new EnrollmentDetail(
                        rs.getInt("enrollmentID"),
                        rs.getInt("userID"),
                        fullName
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
    public boolean isMaintenanceEnabled() {
        try (Connection conn=DBConnection.getErpConnection()) {
            return isMaintenanceModeOn(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean isMaintenanceModeOn(Connection conn) {
        String sql="SELECT settingValue FROM settings WHERE settingKey='maintenanceOn'";
        try (PreparedStatement stmt=conn.prepareStatement(sql);
             ResultSet rs=stmt.executeQuery()) {
            if (rs.next()) {
                return "true".equalsIgnoreCase(rs.getString("settingValue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        List<Grade> grades=new ArrayList<>();
        String sql="SELECT gradeID, enrollmentID, componentName, score FROM grades WHERE enrollmentID=?";
        
        try (Connection conn=DBConnection.getErpConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs=stmt.executeQuery();
            
            while (rs.next()) {
                grades.add(new Grade(
                    rs.getInt("gradeID"),
                    rs.getInt("enrollmentID"),
                    rs.getString("componentName"), 
                    rs.getDouble("score")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grades;
    }

    public boolean saveOrUpdateGrade(int enrollmentId, String component, double score) {
        String checkSql="SELECT gradeID FROM grades WHERE enrollmentID=? AND componentName=?";
        String updateSql="UPDATE grades SET score=? WHERE gradeID=?";
        String insertSql="INSERT INTO grades (enrollmentID, componentName, score) VALUES (?, ?, ?)";

        try (Connection conn=DBConnection.getErpConnection()) {
            if (isMaintenanceModeOn(conn)) return false;

            Integer gradeId=null;
            //if grade already exist
            try (PreparedStatement checkStmt=conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, enrollmentId);
                checkStmt.setString(2, component);
                ResultSet rs=checkStmt.executeQuery();
                if (rs.next()) {
                    gradeId=rs.getInt("gradeID");
                }
            }

            //If exists update
            if (gradeId != null) {
                try (PreparedStatement updateStmt=conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, score);
                    updateStmt.setInt(2, gradeId);
                    return updateStmt.executeUpdate() > 0;
                }
            } 
            //insert if not exist
            else {
                try (PreparedStatement insertStmt=conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, enrollmentId);
                    insertStmt.setString(2, component);
                    insertStmt.setDouble(3, score);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  
        public void computeFinalGradesForSection(int sectionId) {
            String enrollSql="SELECT enrollmentID FROM enrollments WHERE sectionID=?";
            
            try (Connection conn=DBConnection.getErpConnection()) {
                if (isMaintenanceModeOn(conn)) return; //if maintenance on hai then stop
    
                try (PreparedStatement stmt=conn.prepareStatement(enrollSql)) {
                    stmt.setInt(1, sectionId);
                    ResultSet rs=stmt.executeQuery();
                    
                    while (rs.next()) {
                        int enrollmentId=rs.getInt("enrollmentID");
                        calculateStudentFinal(conn, enrollmentId);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
    private void calculateStudentFinal(Connection conn, int enrollmentId) throws SQLException {
        double quiz=0.0;
        double midterm=0.0;
        double finalExam=0.0;

        String scoreSql="SELECT componentName, score FROM grades WHERE enrollmentID=?";
        try (PreparedStatement stmt=conn.prepareStatement(scoreSql)) {
            stmt.setInt(1, enrollmentId);
            ResultSet rs=stmt.executeQuery();
            while (rs.next()) {
                String comp=rs.getString("componentName");
                double s=rs.getDouble("score");

                if (comp != null) {
                    String name=comp.trim();
                    if (name.equalsIgnoreCase("Quiz")) {
                        quiz=s;
                    } else if (name.equalsIgnoreCase("Midterm")) {
                        midterm=s;
                    } else if (name.equalsIgnoreCase("Final Exam")) {
                        finalExam=s;
                    }
                }
            }
        }

        double finalScore=quiz+midterm +finalExam;
        saveOrUpdateGrade(enrollmentId, "Overall", finalScore);
    }
    
}



