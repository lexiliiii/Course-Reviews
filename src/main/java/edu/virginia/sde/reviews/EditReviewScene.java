package edu.virginia.sde.reviews;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.Time;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import javax.swing.plaf.IconUIResource;
import java.sql.SQLException;
import java.sql.Timestamp;
public class EditReviewScene {
    public EditReviewScene(Stage stage,Review review) throws SQLException {
        stage.setTitle("Edit Review");
        Label myreview = new Label( "Edit Your Review" );
        Label errorLabel=new Label();

        Label myrating =new Label("Your Rating(1-5)");
        TextField inputRate =new TextField();

        Label comment =new Label("Your Comment(Optional)");
        TextField inputComment=new TextField();

        Button addbutton=new Button();
        addbutton.setText("submit");

        addbutton.setOnAction(event-> {
            String rateString = inputRate.getText();
            String newcomment = inputComment.getText();
            Timestamp time = new Timestamp( System.currentTimeMillis() );
            Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
            if(validInputs.contains(rateString)) {
                try {
                    if (newcomment.isEmpty()) {
                        updateReview(review.getReviewID(), rateString, " ", time);
                    } else {
                        updateReview(review.getReviewID(), rateString, newcomment, time );
                    }
                    updateMyReview(review, rateString, newcomment);
                    inputComment.clear();
                    inputRate.clear();
                    errorLabel.setText("Successfully Edited Review");
                    CourseReviewController reviewscene = new CourseReviewController(stage, review.getUsername(), review.getCourseMnemonic(), review.getCourseNumber(),review.getCourseTitle());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                inputRate.clear();
                inputComment.clear();
                errorLabel.setText("Invalid Rating");
            }
        });


        Label errorLabel2 = new Label();

        HBox inputBox = new HBox( 10 );
        inputBox.getChildren().addAll(myrating, inputRate
        );
        inputBox.setAlignment( Pos.CENTER );

        VBox addBox = new VBox( 10 );
        addBox.getChildren().addAll( myreview,errorLabel2,inputBox,errorLabel,comment,inputComment,addbutton
        );
        addBox.setAlignment( Pos.CENTER );


        GridPane root = new GridPane();
        root.setHgap( 10 );
        root.setVgap( 10 );
        root.setPadding(new Insets(0, 10, 0, 10));
        root.setAlignment( Pos.CENTER );
        root.add( addBox , 0, 0 );


        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }
    private void updateReview(int reviewID,String newrating,String comments, Timestamp time ) throws SQLException {
        int score=Integer.parseInt(newrating);
        if (comments.isEmpty()) {
            comments = " ";
        }
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        driver.updateReview(reviewID,score, time, comments );
        driver.disconnect();
    }
    private void updateMyReview(Review review,String newrating,String comments ) throws SQLException {
        int score=Integer.parseInt(newrating);
        if (comments.isEmpty()) {
            comments = " ";
        }
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite");
        driver.connect();
        driver.createTables();
        String username=review.getUsername();
        String mneomic=review.getCourseMnemonic();
        int coursenum=review.getCourseNumber();
        driver.updateMyReview(username,review.getCourseTitle(),score );
        driver.disconnect();
    }

}

