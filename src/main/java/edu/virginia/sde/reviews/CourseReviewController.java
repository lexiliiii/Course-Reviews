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
    private boolean added;
    private Review ownReview;
    private VBox reviewControls;
    private ListView<Review> listView;
    private Label errorLabel = new Label();
    private Stage stage;
    private String username;
    private String mnemonic;
    private int courseNum;
    private String courseTitle;

    public CourseReviewController(Stage stage, String username, String mnemonic, int courseNum, String courseTitle) throws SQLException {
        this.stage = stage;
        this.username = username;
        this.mnemonic = mnemonic;
        this.courseNum = courseNum;
        this.courseTitle = courseTitle;
        stage.setTitle("Course Review");

        initializeUI();
        refreshListView(); // Initially populate and check if review added
    }

    private void initializeUI() throws SQLException {
        listView = new ListView<>();
        listView.setPrefWidth(800);
        listView.setPrefHeight(600);

        ScrollPane listScrollPane = new ScrollPane(listView);
        listScrollPane.setFitToWidth(true);
        listScrollPane.setFitToHeight(true);

        Label courseLabel = new Label("Review for " + mnemonic + courseNum+"     "+courseTitle);
        Label averageLabel=new Label("Average Rating: "+ getAverage(courseTitle));


        reviewControls = new VBox(10);
        reviewControls.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> handleBackButton());

        VBox labelBox = new VBox(10, courseLabel, averageLabel);
        HBox mainContent = new HBox(20, listScrollPane, reviewControls);
        mainContent.setAlignment(Pos.CENTER);

        GridPane root = new GridPane();
        root.setHgap(30);
        root.setVgap(30);
        root.setPadding(new Insets(30));
        root.add(labelBox, 0, 0);
        root.add(mainContent, 0, 1);
        root.add(backButton, 0, 2);

        Scene scene = new Scene(root, 1280, 780);
        stage.setScene(scene);
        stage.show();
    }

    private void refreshListView() throws SQLException {
        ObservableList<Review> reviews = viewableReview(courseTitle);
        listView.setItems(reviews);
        ownReview = reviews.stream().filter(r -> r.getUsername().equals(username)).findFirst().orElse(null);
        added = ownReview != null;
        updateReviewControls();
    }

    private void updateReviewControls() {
        reviewControls.getChildren().clear();

        if (added && ownReview != null) {
            displayUserReview();
        } else {
            addUserReviewInputControls();
        }
    }

    private void displayUserReview() {
        Label ratingLabel = new Label("Your Rating: " + ownReview.getRating());
        TextArea commentTextArea = new TextArea(ownReview.getComment());
        commentTextArea.setEditable(false);
        commentTextArea.setWrapText(true);
        commentTextArea.setMinHeight(400);  // Set a maximum height
        commentTextArea.setMinWidth(400);  // Set a maximum width to fit in the layout
        commentTextArea.setPrefRowCount(5);  // Set the preferred number of rows
//        commentLabel.setMinWidth( 10 );
//        commentLabel.setMinHeight( 10 );
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        editButton.setOnAction(event -> editReview(ownReview));
        deleteButton.setOnAction(event ->{
            deleteReview(ownReview);
            try {
                CourseReviewController courseReviewController = new CourseReviewController( stage, username, mnemonic, courseNum, courseTitle );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        reviewControls.getChildren().addAll(ratingLabel, commentTextArea, editButton, deleteButton, errorLabel);
    }

    private void addUserReviewInputControls() {
        TextField inputRate = new TextField();
        TextArea inputComment = new TextArea();
        Button addButton = new Button("Add");

        addButton.setOnAction(event ->{
            addReview(inputRate.getText(), inputComment.getText());
            try {
                CourseReviewController courseReviewController = new CourseReviewController( stage, username, mnemonic, courseNum, courseTitle );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        reviewControls.getChildren().addAll(new Label("Your Rating (1-5)"), inputRate, new Label("Your Comment (Optional)"), inputComment, addButton, errorLabel);
    }

    private void addReview(String rating, String comment) {
        try {
            int rate = Integer.parseInt(rating);
            addReview(username, mnemonic, courseNum, courseTitle, rate, new Timestamp(System.currentTimeMillis()), comment);
            addMyReview(username, mnemonic, courseNum, rate, courseTitle);
            refreshListView();
        } catch (SQLException e) {
            errorLabel.setText("Error adding review: " + e.getMessage());
        }
    }

    private void editReview(Review review) {
        try {
            EditReviewScene editReviewScene = new EditReviewScene(stage, review);
            added=true;
            refreshListView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteReview(Review review) {
        DatabaseReviews db = new DatabaseReviews("reviews.sqlite");
        try {
            db.connect();
            db.createTables();  // Consider if this is necessary at every operation
            db.deleteReview(review.getReviewID());
            db.deleteMyReview(username, courseTitle);
            updateAverageRating(courseTitle);
            db.commit();  // Ensure changes are committed
            added = false;
            refreshListView();
            System.out.println("Review deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            errorLabel.setText("Database Error: " + e.getMessage());
            try {
                db.rollback();  // Rollback in case of error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                db.disconnect();  // Ensure disconnection in finally block
            } catch (SQLException ex) {
                System.err.println("Error disconnecting from database: " + ex.getMessage());
            }
        }
    }


    private void handleBackButton() {
        try {
            CourseSearchController search = new CourseSearchController(stage, username);
        } catch (SQLException e) {
            throw new RuntimeException("Error navigating back to course search: " + e.getMessage(), e);
        }
    }

    private ObservableList<Review> viewableReview(String courseTitle) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        List<Review> allReviews = driver.getReviewsForCourse(courseTitle);
        driver.disconnect();
        return FXCollections.observableArrayList(allReviews);
    }

    private String getAverage(String courseTitle) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.updateAverageRating( courseTitle );
        String average = driver.getAvgScore(courseTitle);
        driver.disconnect();
        return average;
    }

    private void addReview(String username, String mnemonic, int courseNum, String courseTitle, int rating, Timestamp timestamp, String comment) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.addReview(username, mnemonic, courseNum, courseTitle, rating, timestamp, comment);
        updateAverageRating(courseTitle);
        driver.disconnect();
    }

    private void addMyReview(String username, String mnemonic, int courseNum, int rating, String courseTitle) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.addMyReview(username, mnemonic, courseNum, courseTitle, rating);
        updateAverageRating(courseTitle);
        driver.disconnect();
    }
    public void updateAverageRating(String courseTitle) throws SQLException {
        DatabaseReviews db = new DatabaseReviews("reviews.sqlite");
        try {
            db.connect();
            db.updateAverageRating(courseTitle);
            db.commit();
        } catch (SQLException e) {
            System.err.println("Error updating average rating: " + e.getMessage());
            db.rollback();
        } finally {
            db.disconnect();
        }
    }


}


