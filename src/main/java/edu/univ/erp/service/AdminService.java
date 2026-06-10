package edu.univ.erp.service;

import edu.univ.erp.util.DBConnection;
import com.password4j.Password;
import edu.univ.erp.data.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.data.Instructor;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.UserDetail;

public class AdminService {

    //user
    public boolean addUser(String username, String rawPassword, String role) {
        String hash=Password.hash(rawPassword).withBcrypt().getResult();
        String insertUserSql="INSERT INTO users (userName, role, passwdHash, status) VALUES (?, ?, ?, 'ACTIVE')";

        Connection authConn=null;
        Connection erpConn=null;

        try {
            authConn=DBConnection.getAuthConnection();

            int userId=-1;
            String existingRole=null;
            try (PreparedStatement check=authConn.prepareStatement("SELECT userID, role FROM users WHERE userName=?")) {
                check.setString(1, username);
                try (ResultSet crs=check.executeQuery()) {
                    if (crs.next()) {
                        userId=crs.getInt("userID");
                        existingRole=crs.getString("role");
                    }
                }
            }

            if (userId==-1) {

                PreparedStatement stmt=authConn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, username);
                stmt.setString(2, role);
                stmt.setString(3, hash);
                int rows=stmt.executeUpdate();
                if (rows==0) {
                    System.out.println("addUser: auth insert affected 0 rows");
                    return false;
                }
                try (ResultSet gk=stmt.getGeneratedKeys()) {
                    if (gk != null && gk.next()) userId=gk.getInt(1);
                }
                if (userId==-1) {
                    try (PreparedStatement ps=authConn.prepareStatement("SELECT userID FROM users WHERE userName=?")) {
                        ps.setString(1, username);
                        try (ResultSet rs2=ps.executeQuery()) {
                            if (rs2.next()) userId=rs2.getInt("userID");
                        }
                    }
                }
                System.out.println("addUser: created auth user username=" + username + " id=" + userId);
            } else {
                System.out.println("addUser: username already exists -> " + username + " id=" + userId + " role=" + existingRole);

            }

            if (userId==-1) {
                System.out.println("addUser: could not determine userId for username=" + username);
                return false;
            }


            erpConn=DBConnection.getErpConnection();

            if ("INSTRUCTOR".equalsIgnoreCase(role)) {
                try (PreparedStatement chk=erpConn.prepareStatement("SELECT userID FROM instructors WHERE userID=?")) {
                    chk.setInt(1, userId);
                    try (ResultSet rs=chk.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("addUser: instructor profile already exists for userID=" + userId);
                            return true;
                        }
                    }
                }

                String instSql="INSERT INTO instructors (userID, fullName, department) VALUES (?, ?, ?)";
                try (PreparedStatement instStmt=erpConn.prepareStatement(instSql)) {
                    instStmt.setInt(1, userId);
                    instStmt.setString(2, username);
                    instStmt.setString(3, "General");
                    int r=instStmt.executeUpdate();
                    if (r==0) {
                        System.out.println("addUser: instructors insert affected 0 rows for userID=" + userId);
                        return false;
                    }
                    System.out.println("addUser: instructor profile created for userID=" + userId);
                }
            } else if ("STUDENT".equalsIgnoreCase(role)) {
                try (PreparedStatement chk=erpConn.prepareStatement("SELECT userID FROM students WHERE userID=?")) {
                    chk.setInt(1, userId);
                    try (ResultSet rs=chk.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("addUser: student profile already exists for userID=" + userId);
                            return true;
                        }
                    }
                }
                String studSql="INSERT INTO students (userID, roll, firstName, lastName, program, passOutYear) VALUES (?, ?, ?, ?, 'CS', 2025)";
                try (PreparedStatement studStmt=erpConn.prepareStatement(studSql)) {
                    studStmt.setInt(1, userId);
                    studStmt.setString(2, "S" + userId);
                    studStmt.setString(3, username);
                    studStmt.setString(4, "Student");
                    int r=studStmt.executeUpdate();
                    if (r==0) {
                        System.out.println("addUser: students insert affected 0 rows for userID=" + userId);
                        return false;
                    }
                    System.out.println("addUser: student profile created for userID=" + userId);
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("addUser: ERROR -> " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (authConn != null) authConn.close(); } catch (Exception ignored) {}
            try { if (erpConn != null) erpConn.close(); } catch (Exception ignored) {}
        }
    }

    public boolean updateCourse(int courseId, String code, String name, int credits) {
        String sql = "UPDATE courses SET courseCode = ?, courseName = ?, credits = ? WHERE courseID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, credits);
            stmt.setInt(4, courseId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }

    public List<UserDetail> getAllUserDetails() {
        List<UserDetail> users = new ArrayList<>();
        String sql = "SELECT u.userID, u.username, u.role, u.status, " +
                     "COALESCE(s.firstName, i.fullName) as name " +
                     "FROM authDB.users u " +
                     "LEFT JOIN erpDB.students s ON u.userID = s.userID " +
                     "LEFT JOIN erpDB.instructors i ON u.userID = i.userID " +
                     "ORDER BY u.role, u.username";

        try (Connection authConn = DBConnection.getAuthConnection();
             PreparedStatement stmt = authConn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {


            while (rs.next()) {
                users.add(new UserDetail(
                        rs.getInt("userID"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("name"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching user details: " + e.getMessage());
        }
        return users;
    }
    public boolean courseHasSections(int courseId) {
        String sql = "SELECT COUNT(*) FROM sections WHERE courseID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking for course sections: " + e.getMessage());
        }
        return true;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE courseID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }

    public boolean sectionHasEnrollments(int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE sectionID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking for section enrollments: " + e.getMessage());
        }
        return true;
    }

    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE sectionID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error deleting section: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSection(int sectionId, int courseId, int instructorId, String schedule, String room, int capacity) {
        String sql = "UPDATE sections SET courseID = ?, instructorID = ?, schedule = ?, room = ?, capacity = ? WHERE sectionID = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, schedule);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);
            stmt.setInt(6, sectionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error updating section: " + e.getMessage());
            return false;
        }
    }

    public boolean setUserStatus(int userId, String status) {
        String sql = "UPDATE authDB.users SET status = ? WHERE userID = ?";
        try (Connection authConn = DBConnection.getAuthConnection();
             PreparedStatement stmt = authConn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }
    //course
    public boolean addCourse(String code, String name, int credits) {
        String sql="INSERT INTO courses (courseCode, courseName, credits) VALUES (?, ?, ?)";
        try (Connection conn=DBConnection.getErpConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, credits);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT courseID, courseCode, courseName, credits FROM courses ORDER BY courseCode";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("courseID"),
                        rs.getString("courseCode"),
                        rs.getString("courseName"),
                        rs.getInt("credits")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return courses;
    }


    //section
    public boolean addSection(int courseId, int instructorId, String schedule, String room, int capacity) {
        String sql = "INSERT INTO sections (courseID, instructorID, schedule, room, capacity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, schedule);
            stmt.setString(4, room);
            stmt.setInt(5, capacity);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error adding section: " + e.getMessage());
            return false;
        }
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT userID, fullName FROM instructors ORDER BY fullName";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                instructors.add(new Instructor(
                        rs.getInt("userID"),
                        rs.getString("fullName")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error fetching instructors: " + e.getMessage());
        }
        return instructors;
    }
    public List<CourseSectionStructure> getAllSections() {
        List<CourseSectionStructure> sections = new ArrayList<>();
        String sql = "SELECT s.sectionID, c.courseCode, c.courseName, i.fullName, s.schedule, s.room, s.capacity, s.dropDeadline, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.sectionID = s.sectionID) as enrolledCount " +
                     "FROM sections s " +
                     "JOIN courses c ON s.courseID = c.courseID " +
                     "JOIN instructors i ON s.instructorID = i.userID " +
                     "ORDER BY c.courseCode, s.sectionID";

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sections.add(new CourseSectionStructure(
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
            System.err.println("Error fetching sections: " + e.getMessage());
        }
        return sections;
    }
    public boolean isMaintenanceModeOn() {
        String sql = "SELECT settingValue FROM settings WHERE settingKey = 'maintenanceOn'";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return "true".equalsIgnoreCase(rs.getString("settingValue"));
            }
        } catch (Exception e) {
            System.err.println("Error checking maintenance mode: " + e.getMessage());
        }
        return false;
    }

    public void setMaintenanceMode(boolean isEnabled) {
        String sql="UPDATE settings SET settingValue=? WHERE settingKey='maintenanceOn'";
        try (Connection conn=DBConnection.getErpConnection();
             PreparedStatement stmt=conn.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(isEnabled));
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}










