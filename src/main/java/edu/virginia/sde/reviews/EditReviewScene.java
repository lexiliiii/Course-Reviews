package edu.virginia.sde.reviews;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
        myreview.setFont( Font.font("Times New Roman", FontWeight.BOLD, 20) );
        myreview.setStyle( "-fx-text-fill:white" );

        Label myrating =new Label("Your Rating(1-5)");
        TextField inputRate =new TextField();
        myrating.setFont( Font.font("Times New Roman", FontWeight.NORMAL, 15) );
        myrating.setStyle( "-fx-text-fill:white" );

        Label comment =new Label("Your Comment(Optional)");
        TextField inputComment=new TextField();
        comment.setFont( Font.font("Times New Roman", FontWeight.NORMAL, 15) );
        comment.setStyle( "-fx-text-fill:white" );

        Button addbutton=new Button();
        addbutton.setText("Edit");
        addbutton.setFont(new Font("Times New Roman", 13));

        Button backbutton=new Button();
        backbutton.setText("Back");
        backbutton.setFont(new Font("Times New Roman", 13));

        backbutton.setOnAction( event -> {
            try {
                CourseReviewController reviewscene = new CourseReviewController(stage, review.getUsername(), review.getCourseMnemonic(), review.getCourseNumber(),review.getCourseTitle());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        addbutton.setOnAction(event-> {
            String rateString = inputRate.getText();
            String newcomment = inputComment.getText();
            Timestamp time = new Timestamp( System.currentTimeMillis() );
            Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));
            if(validInputs.contains(rateString)) {
                try {
                    if (newcomment.isEmpty()) {
                        updateReview(review.getReviewID(), rateString, " ", time );
                    } else {
                        updateReview(review.getReviewID(), rateString, newcomment, time );
                    }
                    updateMyReview(review, rateString, newcomment);
                    inputComment.clear();
                    inputRate.clear();
                    errorLabel.setText("Successfully Edited Review");
                    errorLabel.setFont( new Font( "Times New Roman", 14) );
                    errorLabel.setStyle( "-fx-text-fill:red" );
                    CourseReviewController reviewscene = new CourseReviewController(stage, review.getUsername(), review.getCourseMnemonic(), review.getCourseNumber(),review.getCourseTitle());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else{
                inputRate.clear();
                inputComment.clear();
                errorLabel.setText("Invalid Rating");
                errorLabel.setFont( new Font( "Times New Roman", 14) );
                errorLabel.setStyle( "-fx-text-fill:red" );
            }
        });


        Label errorLabel2 = new Label();

        HBox inputBox = new HBox( 10 );
        inputBox.getChildren().addAll(myrating, inputRate);
        inputBox.setAlignment( Pos.CENTER );

        HBox buttonBox = new HBox( 10 );
        buttonBox.getChildren().addAll( addbutton, backbutton );
        buttonBox.setAlignment( Pos.CENTER );

        VBox addBox = new VBox( 10 );
        addBox.getChildren().addAll( myreview,errorLabel2,inputBox,errorLabel,comment,inputComment,buttonBox);
        addBox.setAlignment( Pos.CENTER );


        GridPane root = new GridPane();
        root.setHgap( 10 );
        root.setVgap( 10 );
        root.setPadding(new Insets(0, 10, 0, 10));
        root.setAlignment( Pos.CENTER );
        root.add( addBox , 0, 0 );

        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #bdc3c7 0%, #7595af 100%);");

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
        driver.updateReview( reviewID,score, time, comments );
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
        String title = review.getCourseTitle();
        driver.updateMyReview(username,review.getCourseTitle(),score );
        driver.disconnect();
    }

}

