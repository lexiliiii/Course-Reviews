package edu.virginia.sde.reviews;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyReviewController {
    public MyReviewController(Stage stage, String username) throws SQLException {
        stage.setTitle("My Review");
        Label myreview = new Label(username+"'s review");
        myreview.setFont( Font.font("Times New Roman", FontWeight.BOLD, 20) );
        myreview.setStyle( "-fx-text-fill:white" );
        myreview.setAlignment(Pos.TOP_CENTER);

        ListView<MyReview> list = new ListView<MyReview>();
        ObservableList<MyReview> items = viewableMyReview(username);
        list.setItems(items);
        list.setPrefWidth(800);
        list.setPrefHeight(300); // Set a preferred height to ensure scrollability

        list.setOnMouseClicked(event -> {
            if (!list.getSelectionModel().isEmpty()) {
                MyReview selectedReview = list.getSelectionModel().getSelectedItem();
                String mnemonic=selectedReview.getCourseMnemonic();
                int coursenum=selectedReview.getCourseNumber();
                try {
                    CourseReviewController courseReview = new CourseReviewController(stage, username, mnemonic, coursenum,selectedReview.getCourseTitle());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button back = new Button("Back");
        back.setFont(new Font("Times New Roman", 13));
        back.setOnAction(event -> {
            try {
                CourseSearchController search = new CourseSearchController(stage, username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(0, 10, 0, 10));
        root.setAlignment(Pos.CENTER);
        root.add(myreview, 0, 0);
        root.add(list, 0, 1);
        root.add(back, 0, 2);

        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #bdc3c7 0%, #7595af 100%);");

        Scene scene = new Scene(root, 1280, 780);
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<MyReview> viewableMyReview(String username) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        List<MyReview> allMyReviews = driver.getMyReviews(username);
        driver.disconnect();
        return FXCollections.observableArrayList(allMyReviews);
    }


}

