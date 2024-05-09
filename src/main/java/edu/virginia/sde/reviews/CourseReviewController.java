package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseReviewController {

    private Course getCourse(String mnemoic, int number) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        Course temp = null;
        List<Course> allCourse = driver.getAllCourses();
        for (Course c : allCourse) {
            if (c.getCourseNumber() == number && c.getMnemonic().equals(mnemoic)) {
                temp = c;
            }
        }
        driver.disconnect();
        return temp;
    }

    public CourseReviewController(Stage stage, String username, String mneomic, int coursenum) throws SQLException {
        stage.setTitle("Course Review");
        DatabaseReviews database = new DatabaseReviews("reviews.sqlite");
        database.connect();
        database.createTables();

        final ObservableListWrapper wrapper = new ObservableListWrapper(FXCollections.observableArrayList());
        ListView<Review> list = new ListView<>();
        try {
            wrapper.setList(viewableReview(mneomic, coursenum)); // Populate the wrapper
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        list.setItems(wrapper.getList()); // Use the wrapped list
        list.setPrefWidth(800);
        list.setPrefHeight(600);

        final Review[] ownreviewWrapper = new Review[1];
        boolean added = false;
        for (Review review :wrapper.getList()) {
            if (review.getUsername().equals(username)) {
                added = true;
                ownreviewWrapper[0] = review;  // Use array to hold the review
            }
        }


        ScrollPane listScrollPane = new ScrollPane(list);
        listScrollPane.setFitToWidth(true);
        listScrollPane.setFitToHeight(true);

        Course course = getCourse(mneomic, coursenum);
        double average = getAverage(mneomic, coursenum);
        Label reviewLabel = new Label("Review for " + mneomic + coursenum + ": " + course.getCourseTitle());
        Label averagelable=new Label("Average Rating: "+average);

        TextField inputRate = new TextField();
        inputRate.setPrefWidth(200);
        TextArea inputComment = new TextArea();
        inputComment.setPrefWidth(500);
        inputComment.setPrefHeight(200);
        inputComment.setWrapText(true);

        Button addButton = new Button("Submit");
        Button backButton = new Button("Back");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Label errorLabel = new Label();

        addButton.setOnAction(event -> handleAddButton(database, inputRate, inputComment, username, mneomic, coursenum, wrapper.list, list, errorLabel));
        backButton.setOnAction(event -> handleBackButton(stage, username));
        editButton.setOnAction(event -> {  if (ownreviewWrapper[0] != null) {
            try {
                EditReviewScene editReviewScene = new EditReviewScene(stage, ownreviewWrapper[0]);
            } catch (Exception e) {
                errorLabel.setText("Failed to open edit review screen: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("No review available to edit.");
        }


        });
        deleteButton.setOnAction(event -> {
            try {
                database.deleteReview(ownreviewWrapper[0].getReviewID());
                database.deleteMyReview(username,mneomic,coursenum);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        VBox label=new VBox(reviewLabel,averagelable);

        VBox reviewControls = new VBox(10, new Label("Your Rating (1-5)"), inputRate, new Label("Your Comment (Optional)"), inputComment, addButton, editButton, deleteButton, errorLabel);
        reviewControls.setAlignment(Pos.CENTER);

        HBox mainContent = new HBox(20, listScrollPane, reviewControls);
        mainContent.setAlignment(Pos.CENTER);

        GridPane root = new GridPane();
        root.setHgap(30);
        root.setVgap(30);
        root.setPadding(new Insets(30));
        root.add(label, 0, 0);
        root.add(mainContent, 0, 1);
        root.add(backButton, 0, 2);

        Scene scene = new Scene(root, 1280, 780);
        stage.setScene(scene);
        stage.show();
    }

    private void handleAddButton(DatabaseReviews database, TextField inputRate, TextArea inputComment, String username, String mneomic, int coursenum, ObservableList<Review> reviews, ListView<Review> list, Label errorLabel) {
        String rateString = inputRate.getText();
        String words = inputComment.getText();
        Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));

        if (validInputs.contains(rateString)) {
            try {
                int rating = Integer.parseInt(rateString);
                database.addReview(username, mneomic, coursenum, rating, new Timestamp(System.currentTimeMillis()), words);
                database.addMyReview(username, mneomic, coursenum, rating);
                database.commit();  // Ensure commit is called to save the changes
                database.disconnect();
                // Clear the fields
                inputRate.clear();
                inputComment.clear();

                // Reload the review items to reflect new data
                ObservableList<Review> updatedItems = viewableReview(mneomic, coursenum);
                list.setItems(updatedItems); // Update the ListView with new items
                list.refresh(); // Force the list to refresh its content


            } catch (SQLException e) {
                errorLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid rating. Please enter a number between 1 and 5.");
            inputRate.clear();
            inputComment.clear();
        }
    };

    private void handleBackButton(Stage stage, String username) {
        try {
            CourseSearchController search = new CourseSearchController(stage, username);
        } catch (SQLException e) {
            throw new RuntimeException("Error navigating back to course search: " + e.getMessage(), e);
        }
    }



    private ObservableList<Review> viewableReview(String mneomic,int coursenum) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        List<Review> allReviews = driver.getReviewsForCourse(mneomic,coursenum);
        driver.disconnect();
        return FXCollections.observableArrayList(allReviews);
    }
    private double getAverage(String mneomic,int coursenum)throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        double average=driver.getAvgScore(mneomic, coursenum);
        return average;

    }
    public class ObservableListWrapper {
        private ObservableList<Review> list;

        public ObservableListWrapper(ObservableList<Review> list) {
            this.list = list;
        }

        public ObservableList<Review> getList() {
            return list;
        }

        public void setList(ObservableList<Review> list) {
            this.list = list;
        }
    }



}
