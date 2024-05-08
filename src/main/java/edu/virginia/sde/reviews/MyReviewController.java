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
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyReviewController {
    public MyReviewController(Stage stage, String username){
        stage.setTitle("My Review");
        Label myreview = new Label( "My Review" );




//
//
        // Testing Review:
        MyReview review1 = new MyReview("iris","CS",3140,1);
        MyReview review2 = new MyReview("lexie","CS",3140,1);
//
//
////        // TODO: Sort out courses base on the search
//        ListView<Course> list = new ListView<Course>();
//        ObservableList<Course> items = null;
//        try {
//            items = viewableMyReviews(username);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        list.setItems( items );
//        list.setPrefWidth( 800 );

//
//        // TODO: Set Action for clicking each row.
//        list.setOnMouseClicked(event -> {
//            CourseReviewController courseReview=new CourseReviewController(stage, username);
//        });

        Button logOutButton = new Button( "Log Out" );
        logOutButton.setOnAction(event -> {
            LogInController login = new LogInController( stage );
        });

        HBox functionBox = new HBox( 10 );
        functionBox.getChildren().addAll(  logOutButton );
        functionBox.setAlignment( Pos.CENTER );


        GridPane root = new GridPane();
//        root.setHgap(10);
//        root.setVgap(10);
//        root.setPadding(new Insets(0, 10, 0, 10));
//        root.setAlignment( Pos.CENTER );
//        root.add( searchBox, 0, 0 );
//        root.add( list, 0, 1 );
//        root.add( functionBox, 0, 2 );



        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }
//    private ObservableList<Course> viewableMyReviews(String username) throws SQLException {
//        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
//        driver.connect();
//        driver.createTables();
//
//        List<MyReview> allMyReviews = driver.getMyReviews(username);
//        ObservableList<MyReview> items = FXCollections.observableArrayList( allMyReviews );
//        return items;
//    }

//    private ObservableList<Course> viewableReview( String mnem, String number, Integer rating) throws SQLException {
//        if( mnem.equals( "" ) && number.equals( "" ) && rating==null ){
//            return viewableMyReviews(username);
//        }
//
//        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
//        driver.connect();
//        driver.createTables();
//
//        List<Course> allCourses = driver.getAllCourses();
//        List<Course> selectedCourses = new ArrayList<Course>();
//        mnem = mnem.toUpperCase();
//       // title = title.toLowerCase();
//
//
//        for( int i = 0; i < allCourses.size(); i++ ){
//            boolean fit = true;
//
//            Course course = allCourses.get( i );
//
//            if( !mnem.equals( "" ) ){
//                if( !course.getMnemonic().equals( mnem ) ){
//                    fit = false;
//                }
//            }
//            if( !number.equals( "" ) ){
//                if( course.getCourseNumber() != Integer.parseInt( number ) ){
//                    fit = false;
//                }
//            }
//            if( !(rating==null) ){
             //   if( !course.getCourseTitle().toLowerCase().contains( title ) ){
//                    fit = false;
//                }
//            }

//            if( fit == true ){
//                selectedCourses.add( course );
//            }
//        }
//
//        ObservableList<Course> items = FXCollections.observableArrayList( selectedCourses );
//        driver.disconnect();
//        return items;
//    }

}
