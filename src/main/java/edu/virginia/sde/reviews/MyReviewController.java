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
public class MyReviewController {
    public MyReviewController(Stage stage){
        stage.setTitle("My Review");
        Label myreview = new Label( "My Review" );


        FlowPane root=new FlowPane();
        root.getChildren().add(myreview);
//        stage.setTitle( "Course Search" );
//
//        Label searchLabel = new Label( "Course Search" );
//
//        Label mnemonicLabel = new Label( "Subject Mnemonic: " );
//        TextField mnemonicInput = new TextField();
//        Label numberLabel = new Label( "Course Number: " );
//        TextField numberInput = new TextField();
//        Label titleLabel = new Label( "Course Title: " );
//        TextField titleInput = new TextField();
//
//        // TODO: Action for Searching
//        Button searchButton = new Button( "Search" );
//        searchButton.setOnAction(event -> {
////            stage.close();
//        });
//
//        HBox inputBox = new HBox( 10 );
//        inputBox.getChildren().addAll( mnemonicLabel, mnemonicInput, numberLabel, numberInput, titleLabel, titleInput, searchButton );
//        inputBox.setAlignment( Pos.CENTER );
//
//        VBox searchBox = new VBox( 10 );
//        searchBox.getChildren().addAll( searchLabel, inputBox );
//        searchBox.setAlignment( Pos.CENTER );
//
//
//        // Testing Course SetUP:
//        Course course1 = new Course( "CS", 3140, "Software Development Essentials", 0.0);
//        course1.setAvgRating( 2.356 );
//        Course course2 = new Course( "CS", 2130, "Computer Systems and Organization", 0.0);
//        course2.setAvgRating( 2.07 );
//
//
//        // TODO: Sort out courses base on the search
//        ListView<Course> list = new ListView<Course>();
//        ObservableList<Course> items = FXCollections.observableArrayList( course1, course2 );
//        list.setItems( items );
//        list.setPrefWidth( 800 );
//
//        // TODO: Set Action for clicking each row.
//        list.setOnMouseClicked(event -> {
////            LogInController login = new LogInController( stage );
//        });
//
//        Button addButton = new Button( "Add Courses" );
//        addButton.setOnAction(event -> {
//            CourseAdding add = new CourseAdding( stage );
//        });
//
//        // TODO: Connect to My Reviews Scene.
//        Button myReviewButton = new Button( "My Review" );
//        myReviewButton.setOnAction(event -> {
//            MyReviewController review=new MyReviewController(stage);
////            stage.close();
//        });
//
//        Button logOutButton = new Button( "Log Out" );
//        logOutButton.setOnAction(event -> {
//            LogInController login = new LogInController( stage );
//        });
//
//        HBox functionBox = new HBox( 10 );
//        functionBox.getChildren().addAll( addButton, myReviewButton, logOutButton );
//        functionBox.setAlignment( Pos.CENTER );
//
//
//        GridPane root = new GridPane();
//        root.setHgap(10);
//        root.setVgap(10);
//        root.setPadding(new Insets(0, 10, 0, 10));
//        root.setAlignment( Pos.CENTER );
//        root.add( searchBox, 0, 0 );
//        root.add( list, 0, 1 );
//        root.add( functionBox, 0, 2 );
//


        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }

}
