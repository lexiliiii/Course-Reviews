package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseReviewController {

    public Course getCourse(String mnemoic, int number) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        Course temp = null;
        List<Course> allCourse = driver.getAllCourses();
        for(Course c: allCourse){
            if(c.getCourseNumber() == number && c.getMnemonic().equals(mnemoic)){
                temp = c;
            }
        }
        driver.disconnect();
        return temp;
    }

    public CourseReviewController(Stage stage, String Username, String mneomic, int coursenum){
        stage.setTitle("Course Review");
        ListView<Review> list = new ListView<Review>();
        ObservableList<Review> items = null;
        try {
            items = viewableReview(mneomic,coursenum);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        list.setItems(items);
        list.setPrefWidth(800);
        list.setPrefHeight(300); // Set a preferred height to ensure scrollability
        String coursename;
        Label review=new Label("Review For "+mneomic+coursenum);
    }
    private ObservableList<Review> viewableReview(String mneomic,int coursenum) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        List<Review> allReviews = driver.getReviewsForCourse(mneomic,coursenum);
        driver.disconnect();
        return FXCollections.observableArrayList(allReviews);
    }
}
