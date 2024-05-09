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
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseReviewController {
boolean added;
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

    public CourseReviewController(Stage stage, String username, String mneomic, int coursenum, String coursetitle) throws SQLException {
        stage.setTitle("Course Review");

        final ObservableListWrapper wrapper = new ObservableListWrapper(FXCollections.observableArrayList());
        ListView<Review> list = new ListView<>();
        try {
            wrapper.setList(viewableReview(coursetitle)); // Populate the wrapper
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        list.setItems(wrapper.getList()); // Use the wrapped list
        list.setPrefWidth(800);
        list.setPrefHeight(600);

        final Review[] ownreviewWrapper = new Review[1];
        added = false;
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
        double average = getAverage(coursetitle);
        Label reviewLabel = new Label("Review for " + mneomic + coursenum + ": " + course.getCourseTitle());
        Label averagelable=new Label("Average Rating: "+average);

//        TextField inputRate = new TextField();
//        inputRate.setPrefWidth(200);
//        TextArea inputComment = new TextArea();
//        inputComment.setPrefWidth(500);
//        inputComment.setPrefHeight(200);
//        inputComment.setWrapText(true);

        Button addButton = new Button("Submit");
        Button backButton = new Button("Back");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Label errorLabel = new Label();
        backButton.setOnAction(event -> handleBackButton(stage, username));
//        addButton.setOnAction(event -> handleAddButton( inputRate, inputComment, username, mneomic, coursenum, list, errorLabel,coursetitle));
//        backButton.setOnAction(event -> handleBackButton(stage, username));
//        editButton.setOnAction(event -> {  if (ownreviewWrapper[0] != null) {
//            try {
//                EditReviewScene editReviewScene = new EditReviewScene(stage, ownreviewWrapper[0]);
//            } catch (Exception e) {
//                errorLabel.setText("Failed to open edit review screen: " + e.getMessage());
//                e.printStackTrace();
//            }
//        } else {
//            errorLabel.setText("No review available to edit.");
//        }
//        });

        VBox label=new VBox(reviewLabel,averagelable);
        VBox reviewControls=new VBox();
        if(added==true){
            reviewControls=createControlsBox(username,list,coursetitle,ownreviewWrapper[0],stage);
        }
        else if(added==false){
            reviewControls=createControlsBox(username,mneomic,coursenum,list,coursetitle,ownreviewWrapper[0],stage);
        }


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

    private void handleAddButton( TextField inputRate, TextArea inputComment, String username, String mneomic, int coursenum, ListView<Review> list, Label errorLabel, String coursetitle) {
        String rateString = inputRate.getText().trim();
        String words = inputComment.getText().trim();
        Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
        if (validInputs.contains(rateString)) {
            try {
                addReview(username, mneomic, coursenum, coursetitle,rateString, new Timestamp(System.currentTimeMillis()), words);
                addMyReview(username,mneomic,coursenum,coursetitle,rateString);
                inputRate.clear();
                inputComment.clear();
                errorLabel.setText("Review added successfully.");

                // Reload the review items to reflect new data
               refreshListView(coursetitle,list);
            } catch (SQLException e) {
                errorLabel.setText("Failed to add review: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid rating. Please enter a number between 1 and 5.");
            inputRate.clear();
            inputComment.clear();
        }
    }


    private void handleBackButton(Stage stage, String username) {
        try {
            CourseSearchController search = new CourseSearchController(stage, username);
        } catch (SQLException e) {
            throw new RuntimeException("Error navigating back to course search: " + e.getMessage(), e);
        }
    }
    private void handleDeleteButton( String username,Review ownreview, ListView<Review> list,Label errorlabel,String coursetitle) throws SQLException {
        DatabaseReviews database = new DatabaseReviews("reviews.sqlite");
        database.connect();
        database.createTables();
        try {
            database.deleteReview(ownreview.getReviewID());
            database.deleteMyReview(username, ownreview.getCourseTitle());
            database.commit();
            database.disconnect();
            errorlabel.setText("Delete Review Successfully");
            refreshListView(coursetitle,list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    private double getAverage(String coursetitle)throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        double average=driver.getAvgScore(coursetitle);
        driver.disconnect();
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

    private void addReview(String username, String mneomic, int coursenum,String courseTitle, String rating,Timestamp time,String words ) throws SQLException {
        int rate = Integer.parseInt(rating);
        if (words.isEmpty()) {
            words = " ";
        }
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.addReview( username,mneomic,coursenum, courseTitle,rate, time, words);
        driver.disconnect();
    }
    private void addMyReview(String username, String mneomic, int coursenum, String rating, String coursetitle) throws SQLException {
        int rate = Integer.parseInt(rating);
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.addMyReview(username,mneomic,coursenum,coursetitle,rate);
        driver.disconnect();
    }

    private void refreshListView(String coursetitle, ListView<Review> listView) throws SQLException {
        ObservableList<Review> updatedItems = viewableReview(coursetitle);
        listView.setItems(updatedItems);
        listView.refresh();
    }

    private VBox createControlsBox(String username, String mnemonic, int courseNum, ListView<Review> listView,String coursetitle,Review review, Stage stage) {
        Label yourate=new Label("Your Rating()");
        TextField inputRate = new TextField();
        Label yousay=new Label("Your Comment (Optional)");
        inputRate.setPrefWidth(200);
        TextArea inputComment = new TextArea();
        inputComment.setPrefWidth(500);
        inputComment.setPrefHeight(200);
        inputComment.setWrapText(true);

        Button addButton = new Button("Add");
        Label errorLabel = new Label();

        addButton.setOnAction(event ->{
            handleAddButton( inputRate,inputComment,username,mnemonic,courseNum,listView, errorLabel,coursetitle);
            added=true;
        });


            return new VBox(10, yourate, inputRate, yousay, inputComment, addButton, errorLabel);
    }
    private VBox createControlsBox(String username, ListView<Review> listView,String coursetitle,Review review, Stage stage) {
        Label yourrating=new Label("Your Rating");
        Label rating=new Label(String.valueOf(review.getRating()));
        Label yourcomment=new Label("Your Comment");
        Label yourwords=new Label(review.getComment());

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Label errorLabel = new Label();
        editButton.setOnAction(event -> {
                try {
                    EditReviewScene editReviewScene = new EditReviewScene(stage, review);
                    added=true;
                    refreshListView(coursetitle,listView);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        });
        deleteButton.setOnAction(event -> {
            try {
                handleDeleteButton( username,review, listView, errorLabel,coursetitle);
                added=false;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        HBox ratelabel=new HBox(10);
        ratelabel.getChildren().addAll(yourrating,rating);
        return new VBox(10, ratelabel, yourcomment, yourwords, editButton, deleteButton, errorLabel);
    }

}
