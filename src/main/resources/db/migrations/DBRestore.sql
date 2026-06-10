-- Cleanup and Restore the DB on each of our machines


SET FOREIGN_KEY_CHECKS = 0;
    TRUNCATE TABLE authDB.users;
    TRUNCATE TABLE erpDB.students;
    TRUNCATE TABLE erpDB.instructors;
    TRUNCATE TABLE erpDB.grades;
    TRUNCATE TABLE erpDB.enrollments;
    TRUNCATE TABLE erpDB.sections;
    TRUNCATE TABLE erpDB.courses;
SET FOREIGN_KEY_CHECKS = 1;



-- Dummy Data For AuthDB
    -- user Table
    INSERT INTO  authDB.users (userID, userName, role, passwdHash, status) VALUES
                                                                               (1, "admin", "ADMIN", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "ACTIVE"),
                                                                               (2, "newton", "INSTRUCTOR", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "ACTIVE"),
                                                                               (3, "turing", "INSTRUCTOR", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "ACTIVE"),
                                                                               (4, "guevara", "STUDENT", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "ACTIVE"),
                                                                               (5, "curie", "STUDENT", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "ACTIVE"),
                                                                               (6, "wilde", "STUDENT", "$2b$10$cztPlQxL..FKUDApj.S.wOVuuSeD.z8JRFWM5a4FMobwq7VtvSjS.", "INACTIVE")
    ;

-- Dummy Data for erpDB
    -- instructors Table
    INSERT INTO erpDB.instructors (userID, fullName, department) VALUES
                                                                     (2, "Issac Newton", "Physics"),
                                                                     (3, "Alan Turing", "Computer Science")
    ;

    -- Students Table
    INSERT INTO erpDB.students (userID, roll, firstName, lastName, program, passOutYear) VALUES
                                                                                             (4,"2024501", "Ernesto", "Guevara", "B.Tech CSSS", 2028),
                                                                                             (5, "2024502", "Marie", "Curie", "B.Tech CSB", 2028),
                                                                                             (6, "2024503", "Oscar", "Wilde","B.Tech CSE", 2028)
    ;

    -- Courses Table
        INSERT INTO erpDB.courses( courseID, courseCode, courseName, credits) VALUES
                                                                                  (101, 'CSE101', 'Intro To Programming', 4),
                                                                                  (102, 'CSE201', 'Data Structures and Algorithms', 4),
                                                                                  (103, 'PHY101', 'Classical Mechanics', 4),
                                                                                  (104, 'MTH101', 'Calculus I', 4),
                                                                                  (105, 'HUM101', 'Humanities 101', 2)

            ;


    -- Sections Table
    INSERT INTO erpDB.sections (sectionID, courseID, instructorID, term, room, schedule, capacity, dropDeadline) VALUES
                                                                                                       (1, 101, 3, "Fall 2025", "C-101", "Mon 0900-1100 Hrs", 60, '2025-12-31'),
                                                                                                       (2, 102, 3, "Fall 2025", "L1", "Tue 1100-1300 Hrs", 1, '2025-12-31'),
                                                                                                       (3, 103, 2, "Fall 2025", "C-201", "Wed 1300-1400 Hrs", 60, '2025-11-26'),
                                                                                                       (4, 104, 2, "Fall 2025", "C-202", "Thu 1200-1400 Hrs", 30, '2025-12-01')
    ;

    -- Enrolllments Table
    INSERT INTO erpDB.enrollments (enrollmentID, studentID, sectionID, status) VALUES
                                                                                   (1,4,1, "ENROLLED"),
                                                                                   (2,4,2, "ENROLLED"),
                                                                                   (3,4,3, "ENROLLED"),
                                                                                   (4,5,1, "DROPPED"),
                                                                                   (5,5,2, "ENROLLED"),
                                                                                   (6,6,1, "ENROLLED")

    ;

    -- Grades Table
    INSERT INTO erpDB.grades (enrollmentID, componentName, score, maxScore) Values
                                                                                (1, "Quiz", 18.5, 20),
                                                                                (1, "Midterm", 18.00, 30),
                                                                                (3, "Midterm", 75.00, 30)
    ;




