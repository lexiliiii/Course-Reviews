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
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import javax.swing.plaf.IconUIResource;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AddReviewController {
    public AddReviewController(Stage stage, String Mnemonic, int CourseNum,String Username, String coursetitle) throws SQLException {

        DatabaseReviews database=new DatabaseReviews("reviews.sqlite");

        database.connect();

        database.createTables();

        stage.setTitle("Add Review");
        Label myreview = new Label( "Add Your Review" );
        Label errorLabel=new Label();

        Label myrating =new Label("Your Rating(1-5)");
        TextField inputRate =new TextField();
        inputRate.setPrefWidth( 200 );
        inputRate.setMaxWidth( 200 );

        Set<String> validInputs = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5"));

        Label comment =new Label("Your Comment(Optional)");
        TextField inputComment=new TextField();
        inputComment.setPrefWidth( 300 );
        inputComment.setMaxWidth( 500 );

        Button addbutton=new Button();
        addbutton.setText("submit");

       addbutton.setOnAction(event-> {
           String rateString = inputRate.getText();
           String words=inputComment.getText();
           try {
               if(validInputs.contains(rateString)){
                   int rating=Integer.parseInt(rateString);
                   database.addReview(Username,Mnemonic,CourseNum,coursetitle,rating,new Timestamp(System.currentTimeMillis()),words);
                   database.addMyReview(Username,Mnemonic,CourseNum,coursetitle,rating);
                   database.commit();
                   database.disconnect();
                   inputComment.clear();
                   inputRate.clear();
                   CourseReviewController courseReview=new CourseReviewController(stage,Username,Mnemonic,CourseNum, coursetitle);
               }
               else{
                   inputRate.clear();
                   inputComment.clear();
                   errorLabel.setText("Invalid Rating");
               }

           } catch (SQLException e) {
               throw new RuntimeException(e);
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
}
