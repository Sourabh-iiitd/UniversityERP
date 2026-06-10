package edu.univ.erp.service;

import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.TranscriptStructure;
import edu.univ.erp.util.DBConnection;
import edu.univ.erp.data.GradesStructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class StudentService {

    // Some Helper Functions
    private boolean isMaintenanceModeOn(Connection conn) throws Exception{
        String sqlStmt = "SELECT settingValue FROM settings WHERE settingKey = 'maintenanceOn'";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                if("true".equalsIgnoreCase(rs.getString("settingValue"))){
                    return true;
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private boolean isAlreadyEnrolled(Connection conn, int studentID, int sectionID) throws Exception{
        String sql = "SELECT enrollmentID FROM enrollments WHERE studentID = ? AND sectionID = ?";
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setInt(1, studentID);
            preparedStmt.setInt(2, sectionID);

            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next()) {
                return true;
            }


        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private boolean isSectionFull(Connection conn, int sectionID) throws Exception{
        String sqlStmt = "SELECT COUNT(*) FROM enrollments WHERE sectionID = ?";
        int current = 0;
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, sectionID);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                current = rs.getInt(1);
            }

        }
        catch (Exception e) {
            System.out.println(e);
        }

        String sqlStmt2 = "SELECT capacity FROM sections WHERE sectionID = ?";
        int max = 0;
        try{
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt2);
            preparedStmt.setInt(1, sectionID);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                max = rs.getInt(1);
            }

        }
        catch (Exception e) {
            System.out.println(e);
        }

        if(current >= max){
            return true;
        }
        return false;

    }

    // Function to get all the available sections with courses
    public List<CourseSectionStructure> getAvailableSections(){
        List<CourseSectionStructure> list = new ArrayList<>();

        String sqlStmt = "SELECT s.sectionID, c.courseID,c.courseCode, c.courseName, i.fullName, s.schedule, s.room, s.capacity, s.dropDeadline ,(SELECT COUNT(*) FROM enrollments e WHERE e.sectionID = s.sectionID AND status='ENROLLED') as enrolledCount FROM sections s JOIN courses c ON s.courseID = c.courseID JOIN instructors i on s.instructorID = i.userID ORDER BY c.courseCode";
        try{
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);

            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()){
                list.add(new CourseSectionStructure(
                        rs.getInt("sectionID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("fullName"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("enrolledCount"),
                        rs.getInt("capacity"),
                        rs.getDate("dropDeadline")

                        ));
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        return list;
    }

    public void registerStudent(int studentID, int sectionID) throws Exception{

        
        Connection conn = DBConnection.getErpConnection();
        if(isMaintenanceModeOn(conn)){
            throw new Exception("Maintenance mode is enabled. Registrations are disabled.");
        }
        if(isAlreadyEnrolled(conn, studentID, sectionID)){
            throw new Exception("Student is already enrolled in this section.");
        }
        if(isSectionFull(conn, sectionID)){
            throw new Exception("Section is full.");
        }

        String sqlStmt = "INSERT INTO enrollments (studentID, sectionID, status) VALUES (?, ?, 'ENROLLED')";
        try (PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt)) {
            preparedStmt.setInt(1, studentID);
            preparedStmt.setInt(2, sectionID);
            preparedStmt.executeUpdate();
        }
       

    }

    // Code for 2nd Tab
    public List<CourseSectionStructure> getStudentRegistrations(int studentID){
        List<CourseSectionStructure> list = new ArrayList<>();

        String sqlStmt = """
            SELECT s.sectionID, c.courseCode, c.courseName, i.fullName, s.schedule, s.room,(SELECT COUNT(*) FROM enrollments e WHERE e.sectionID = s.sectionID AND status='ENROLLED') AS enrolledCount, s.capacity, s.dropDeadline
            FROM enrollments en
            JOIN sections s ON en.sectionID = s.sectionID
            JOIN courses c ON s.courseID = c.courseID
            JOIN instructors i ON  s.instructorID = i.userID
            WHERE en.studentID = ? AND en.status='ENROLLED'
            ORDER BY c.courseCode
            """;

        try{
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, studentID);
            ResultSet rs = preparedStmt.executeQuery();

            while(rs.next()){
                list.add(new CourseSectionStructure(
                        rs.getInt("sectionID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("fullName"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("enrolledCount"),
                        rs.getInt("capacity"),
                        rs.getDate("dropDeadline")
                ));
            }
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e);
        }


    return list;
    }

    public void dropSection(int studentID, int sectionID) throws Exception{
        try{
            Connection conn = DBConnection.getErpConnection();
            if(isMaintenanceModeOn(conn)){
                throw new Exception("Maintenance mode is enabled. You cannot drop sections right now.");
            }
            if(isDropDeadlineExpired(sectionID)){
                throw new Exception("The drop deadline for this section has passed.");
            }

            String sqlStmt = "UPDATE enrollments SET status='DROPPED' WHERE studentID = ? AND sectionID = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, studentID);
            preparedStmt.setInt(2, sectionID);

            if(preparedStmt.executeUpdate() == 0){
                throw new Exception("Drop failed. You might not be enrolled in this section.");
            }

        } catch (Exception e) {
            System.out.println("Error in dropSection: " + e.getMessage());
            throw e;
        }

    }

    // Code for 3rd Tab
    public List<GradesStructure> getStudentGrades(int studentID){
        List<GradesStructure> list = new ArrayList<>();

        String sqlStmt = """
                SELECT s.sectionID, c.courseCode, c.courseName, i.fullname AS instructor, g.componentName, g.score, g.maxScore
                FROM enrollments en
                JOIN grades g ON en.enrollmentID = g.enrollmentID
                JOIN sections s ON  en.sectionID = s.sectionID
                JOIN courses c ON s.courseID = c.courseID
                JOIN instructors i ON  s.instructorID = i.userID
                WHERE en.studentID = ? AND en.status='ENROLLED'
                ORDER BY c.courseCode, g.componentName
                """;
        try{
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, studentID);

            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()){
                list.add(new GradesStructure(
                        rs.getInt("sectionID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("instructor"),
                        rs.getString("componentName"),
                        rs.getDouble("score"),
                        rs.getDouble("maxScore")
                ));
            }
        }
        catch (Exception ex) {
            System.out.println("ERROR getStudentGrades: " + ex);
        }
        return list;
    }


    // For MAINTANANCE BANNER
    public boolean isMaintenanceMode(){
        try{
            Connection conn  = DBConnection.getErpConnection();
            System.out.println("Hiiiii" + isMaintenanceModeOn(conn));
            return isMaintenanceModeOn(conn);
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e);
            return false;
        }
    }


    // Code for CSV Export
    public List<TranscriptStructure> getTranscript(int studentID){
        List<TranscriptStructure> list = new ArrayList<>();

        String sqlStmt = """
                SELECT  c.courseCode, c.courseName, i.fullname AS instructor,s.sectionID, g.componentName, g.score, g.maxScore
                FROM enrollments en
                JOIN grades g ON en.enrollmentID = g.enrollmentID
                JOIN sections s ON  en.sectionID = s.sectionID
                JOIN courses c ON s.courseID = c.courseID
                JOIN instructors i ON  s.instructorID = i.userID
                WHERE en.studentID = ?
                ORDER BY c.courseCode, g.componentName
                """;
        try{
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, studentID);

            ResultSet rs = preparedStmt.executeQuery();
            while(rs.next()){
                list.add(new TranscriptStructure(
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("instructor"),
                        rs.getInt("sectionID"),
                        rs.getString("componentName"),
                        rs.getDouble("score"),
                        rs.getDouble("maxScore")
                ));
            }
        }
        catch (Exception ex) {
            System.out.println("ERROR getTranscript: " + ex);
        }
        return list;
    }

    // Code for TimeTable
    public List<CourseSectionStructure> getStudentTimeTable(int studentID){
        List<CourseSectionStructure> list = new ArrayList<>();

        String sqlStmt = """
                  SELECT s.sectionID, c.courseCode, c.courseName, i.fullName, s.schedule, s.room,(SELECT COUNT(*) FROM enrollments e WHERE e.sectionID = s.sectionID AND status='ENROLLED') AS enrolledCount, s.capacity, s.dropDeadline
            FROM enrollments en
            JOIN sections s ON en.sectionID = s.sectionID
            JOIN courses c ON s.courseID = c.courseID
            JOIN instructors i ON  s.instructorID = i.userID
            WHERE en.studentID = ? AND en.status='ENROLLED'
            ORDER BY s.schedule
                """;

        try{
            Connection conn = DBConnection.getErpConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, studentID);
            ResultSet rs = preparedStmt.executeQuery();

            while(rs.next()){
                list.add(new CourseSectionStructure(
                        rs.getInt("sectionID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getString("fullName"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("enrolledCount"),
                        rs.getInt("capacity"),
                        rs.getDate("dropDeadline")
                ));
            }
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e);
        }





        return list;

    }

    private boolean isDropDeadlineExpired(int sectionID) throws Exception{
        try{
            Connection conn = DBConnection.getErpConnection();
            String sqlStmt = "SELECT dropDeadline FROM sections WHERE sectionID = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);
            preparedStmt.setInt(1, sectionID);

            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                java.sql.Date deadline =  rs.getDate("dropDeadline");

                if( (new java.sql.Date(System.currentTimeMillis())).after(deadline) ){
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error in Drop Deadline Expired Getter " + e);
        }


        return false;
    }
}


