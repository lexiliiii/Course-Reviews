package edu.virginia.sde.reviews;
import javax.print.DocFlavor;
import javax.security.auth.login.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.sql.*;

public class DatabaseReviews {
    private final String sqliteFilename;
    private Connection connection;

    public DatabaseReviews(String sqliteFilename) {
        this.sqliteFilename = sqliteFilename;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        //the next line enables foreign key enforcement - do not delete/comment out
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        //the next line disables auto-commit - do not delete/comment out
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String Users =
                "CREATE TABLE IF NOT EXISTS Users (" +
                        "Username TEXT NOT NULL PRIMARY KEY, " +
                        "Password TEXT NOT NULL CHECK (LENGTH(Password) >= 8));";

        String Courses =
                "CREATE TABLE IF NOT EXISTS Courses (" +
                        "CourseMnemonic TEXT NOT NULL, " +
                        "CourseNumber INTEGER NOT NULL, " +
                        "CourseTitle TEXT NOT NULL, " +
                        "AverageReviewRating DOUBLE, " +
                        "PRIMARY KEY (CourseMnemonic, CourseNumber));";

        String Reviews =
                "CREATE TABLE IF NOT EXISTS Reviews (" +
                        "ReviewID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "CourseMnemonic TEXT NOT NULL, " +
                        "CourseNumber INTEGER NOT NULL, " +
                        "Rating INTEGER NOT NULL CHECK (Rating BETWEEN 1 AND 5), " +
                        "Timestamp TIMESTAMP NOT NULL, " +
                        "Comment TEXT, " +
                        "FOREIGN KEY (CourseMnemonic, CourseNumber) REFERENCES Courses(CourseMnemonic, CourseNumber) ON DELETE CASCADE);";

        String MyReviews =
                "CREATE TABLE IF NOT EXISTS MyReviews (" +
                        "Username TEXT NOT NULL, " +
                        "CourseMnemonic TEXT NOT NULL, " +
                        "CourseNumber INTEGER NOT NULL, " +
                        "Rating INTEGER NOT NULL CHECK (Rating BETWEEN 1 AND 5), " +
                        "PRIMARY KEY (Username, CourseMnemonic, CourseNumber), " +
                        "FOREIGN KEY (Username) REFERENCES Users(Username) ON DELETE CASCADE, " +
                        "FOREIGN KEY (CourseMnemonic, CourseNumber) REFERENCES Courses(CourseMnemonic, CourseNumber) ON DELETE CASCADE);";

        try (Statement statement = connection.createStatement()) {
            statement.execute(Users);
            statement.execute(Courses);
            statement.execute(Reviews);
            statement.execute(MyReviews);
        } catch (SQLException e) {
            throw new SQLException("Error creating tables: " + e.getMessage());
        }
    }

    /** Add users--Register
     * @param username
     * @param password
     * @throws SQLException
     */
    public void registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO Users (Username, Password) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            commit();

        } catch (SQLException e) {
            rollback();
            throw new SQLException("Error adding users: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        List<User> collection = new ArrayList<>();
        String sql = "SELECT * FROM Users;";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                collection.add(new User(username, password));
            }

        } catch (SQLException e) {
            throw new SQLException("Error reading from Users table: " + e.getMessage());
        }

        return collection;
    }

    public void addCourse(String mnemonic, int number, String title, double avgPoint) throws SQLException {
        String sql = "INSERT INTO Courses (CourseMnemonic, CourseNumber, CourseTitle, AverageReviewRating) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mnemonic);
            pstmt.setInt(2, number);
            pstmt.setString(3, title);
            pstmt.setDouble(4, avgPoint);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            rollback();
            throw new SQLException("Error adding courses: " + e.getMessage());
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        List<Course> collection = new ArrayList<>();
        String sql = "SELECT * FROM Courses;";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String mnemonic = rs.getString("CourseMnemonic");
                int number = rs.getInt("CourseNumber");
                String title = rs.getString("CourseTitle");
                double avgPoint = rs.getDouble("AverageReviewRating");
                collection.add(new Course(mnemonic, number, title, avgPoint));
            }

        } catch (SQLException e) {
            throw new SQLException("Error reading from Courses table: " + e.getMessage());
        }

        return collection;
    }

    public void addMyReview(String username, String courseMnemonic, int courseNumber, int rating) throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String sql = "INSERT INTO MyReviews (Username, CourseMnemonic, CourseNumber, Rating) VALUES (?, ?, ?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, courseMnemonic);
            pstmt.setInt(3, courseNumber);
            pstmt.setInt(4, rating);
            pstmt.executeUpdate();
//            if (affectedRows > 0) {
//                System.out.println("Review added successfully.");
//            } else {
//                throw new SQLException("Adding review failed, no rows affected.");
//            }

        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error adding myReviews: " + e.getMessage());
        }
    }

    public List<MyReview> getMyReviews(String username) throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String sql = "SELECT Username, CourseMnemonic, CourseNumber, Rating FROM MyReviews WHERE Username = ?";
        List<MyReview> reviews = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MyReview myreview = new MyReview(
                        rs.getString("Username"),
                        rs.getString("CourseMnemonic"),
                        rs.getInt("CourseNumber"),
                        rs.getInt("Rating")
                );
                reviews.add(myreview);
            }

        } catch (SQLException e) {
            throw new SQLException("Error reading from MyReviews table: " + e.getMessage());
        }
        return reviews;
    }

    public void addReview(String courseMnemonic, int courseNumber, int rating, Timestamp timestamp, String comment) throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String sql = "INSERT INTO Reviews (CourseMnemonic, CourseNumber, Rating, Timestamp, Comment) VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseMnemonic);
            pstmt.setInt(2, courseNumber);
            pstmt.setInt(3, rating);
            pstmt.setTimestamp(4, timestamp);
            pstmt.setString(5, comment);

            pstmt.executeUpdate();
//            if (affectedRows > 0) {
//                System.out.println("Review added successfully for course: " + courseMnemonic);
//            } else {
//                throw new SQLException("Adding review failed, no rows affected.");
//            }

        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error adding Reviews: " + e.getMessage());
        }
    }

    public List<Review> getReviewsForCourse(String courseMnemonic, int courseNumber) throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String sql = "SELECT * FROM Reviews WHERE CourseMnemonic = ? AND CourseNumber = ?";
        List<Review> reviews = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, courseMnemonic);
            pstmt.setInt(2, courseNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("ReviewID"),
                        rs.getString("CourseMnemonic"),
                        rs.getInt("CourseNumber"),
                        rs.getInt("Rating"),
                        rs.getTimestamp("Timestamp"),
                        rs.getString("Comment")
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving reviews for courses: " + e.getMessage());
        }
        return reviews;
    }

    public void updateReview(int reviewID, int newRating, String newComment) throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

        String sql = "UPDATE Reviews SET Rating = ?, Comment = ? WHERE ReviewID = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newRating);
            pstmt.setString(2, newComment);
            pstmt.setInt(3, reviewID);
            pstmt.executeUpdate();

//            if (affectedRows > 0) {
//                System.out.println("Review updated successfully.");
//            } else {
//                throw new SQLException("Updating review failed, no rows affected.");
//            }
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error updating reviews: " + e.getMessage());
        }
    }

    public void clearTables() throws SQLException {
        if (connection.isClosed()) {
            throw new IllegalStateException("Connection is closed right now.");
        }

//        String deleteRoutes = "DELETE FROM Routes;";
//        String deleteStops = "DELETE FROM Stops;";
//        String deleteBusLines = "DELETE FROM BusLines;";
//        String resetAutoincrement = "DELETE FROM sqlite_sequence WHERE name = 'Routes';";

        try (Statement stmt = connection.createStatement()) {
            connection.createStatement().execute("DELETE FROM MyReviews;");
            connection.createStatement().execute("DELETE FROM Reviews;");
            connection.createStatement().execute("DELETE FROM Courses;");
            connection.createStatement().execute("DELETE FROM Users;");

        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error clearing tables: " + e.getMessage());
        }
    }

//    public static void main(String[] args){
//        try {
//            DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
//
//            driver.connect();
//
//            driver.createTables();
//
////            List<User> allUser = driver.getAllUsers();
////            for( int i = 0; i < allUser.size(); i++ ){
////                System.out.println( allUser.get( i ) );
////            }
//
////            driver.registerUser("test4","12");
////            driver.registerUser("test5","1111111!!!");
//
////            driver.addCourse("SDE", 3140, "Software Development Essentials", 0.00);
////            driver.addMyReview("test1", "SDE", 3140, 1);
////            driver.addReview("SDE", 3140, 1, new Timestamp(System.currentTimeMillis()), "shit");
////
////            driver.commit();
//
////            List<User> temp = driver.getAllUsers();
////            System.out.println(temp);
//
////            List<Course> temp = driver.getAllCourses();
////            System.out.println(temp);
//
////            List<MyReview> temp = driver.getMyReviews("test1");
////            System.out.println(temp);
//
////            List<Review> temp1 = driver.getReviewsForCourse("SDE", 3140);
////            System.out.println(temp1);
//
//            driver.disconnect();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
