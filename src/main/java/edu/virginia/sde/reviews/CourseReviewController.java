package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class CourseReviewController {

    private Course getCourse(String mnemoic, int number) throws SQLException {
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

    public CourseReviewController(Stage stage, String username, String mneomic, int coursenum) throws SQLException {
        stage.setTitle("Course Review");
        DatabaseReviews database = new DatabaseReviews("reviews.sqlite");
        database.connect();
        database.createTables();

//        ListView<Review> list = new ListView<Review>();
//        ObservableList<Review> items = null;
//        try {
//            items = viewableReview(mneomic, coursenum);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        list.setItems(items);
//        list.setPrefWidth(800);
//        list.setPrefHeight(300); // Set a preferred height to ensure scrollability
        // Use the wrapper for the items
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


        Course course = getCourse(mneomic, coursenum);
        double average=getAverage(mneomic, coursenum);
        Label review = new Label("Review For " + mneomic + coursenum + " " + course.getCourseTitle()+"                       "+"Average Rating"+average);

        Label myrating =new Label("Your Rating(1-5)");
        TextField inputRate =new TextField();
        inputRate.setPrefWidth( 200 );
        inputRate.setMaxWidth( 200 );

        Label comment =new Label("Your Comment(Optional)");
        TextArea inputComment = new TextArea();
        inputComment.setPrefWidth(500);
        inputComment.setMaxWidth(500);
        inputComment.setPrefHeight(200);
        inputComment.setMaxHeight(200);
        inputComment.setWrapText(true); // Enable text wrapping




        Button addbutton = new Button();
        addbutton.setText("submit");
        Label errorLabel = new Label("");
        addbutton.setOnAction(event -> {
            String rateString = inputRate.getText();
            String words = inputComment.getText();
            Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));

            if (validInputs.contains(rateString)) {
                try {
                    int rating = Integer.parseInt(rateString);
                    database.addReview(username, mneomic, coursenum, rating, new Timestamp(System.currentTimeMillis()), words);
                    database.addMyReview(username, mneomic, coursenum, rating);
                    database.commit();  // Ensure commit is called to save the changes

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
        });

        Button back = new Button("Back");
        back.setOnAction(event -> {
            try {
                CourseSearchController search = new CourseSearchController(stage, username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Button edit=new Button();
        edit.setText("Edit");
        edit.setOnAction(event -> {
            if (ownreviewWrapper[0] != null) {
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

//
        Button delete=new Button();
        delete.setText("Delete");
        delete.setOnAction(event->{
            try {
                database.deleteReview(ownreviewWrapper[0].getReviewID());
                database.deleteMyReview(username,mneomic,coursenum);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        VBox reviewControls = new VBox(10);
        reviewControls.getChildren().addAll(myrating, inputRate, comment, inputComment, addbutton, edit,errorLabel,delete);
        reviewControls.setAlignment(Pos.CENTER);

        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(list, reviewControls);
        mainContent.setAlignment(Pos.CENTER);

        HBox functionBox = new HBox( 10 );
        functionBox.getChildren().addAll( back );
        functionBox.setAlignment( Pos.CENTER_LEFT);


        GridPane root = new GridPane();
        root.setHgap(30);
        root.setVgap(30);
        root.setPadding(new Insets(0, 30, 0, 30));
        root.setAlignment(Pos.CENTER);
        root.add(review, 0, 0);
        root.add(mainContent, 0, 1);
        root.add(functionBox, 0, 2);



        Scene scene = new Scene(root, 1280, 780);
        stage.setScene(scene);
        stage.show();
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
