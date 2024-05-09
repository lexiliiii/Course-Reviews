package edu.virginia.sde.reviews;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CourseSearchController {

    public CourseSearchController( Stage stage, String username ) throws SQLException {

        stage.setTitle( "Course Search" );

        Label searchLabel = new Label( "Course Search" );

        Label mnemonicLabel = new Label( "Subject Mnemonic: " );
        TextField mnemonicInput = new TextField();
        Label numberLabel = new Label( "Course Number: " );
        TextField numberInput = new TextField();
        Label titleLabel = new Label( "Course Title: " );
        TextField titleInput = new TextField();
        Label errorLabel = new Label();

        ListView<Course> list = new ListView<Course>();
        ObservableList<Course> items = viewableCourse();
        list.setItems( items );
        list.setPrefWidth( 800 );

        // TODO: Set Action for clicking each row.
        list.setOnMouseClicked(event -> {
            Course selectedCourse = list.getSelectionModel().getSelectedItem();
            String selectedMnemonic = selectedCourse.getMnemonic();
            int selectedCourseNumber = selectedCourse.getCourseNumber();
            String selectedCourseTitel = selectedCourse.getCourseTitle();
            try {
                CourseReviewController review = new CourseReviewController( stage, username,selectedMnemonic,selectedCourseNumber,selectedCourseTitel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


        Button searchButton = new Button( "Search" );
        searchButton.setOnAction(event -> {
            String mnem = mnemonicInput.getText();
            String num = numberInput.getText();
            String title = titleInput.getText();


            if( mnem.length() > 4 ){
                errorLabel.setText( "Please Check Your Fields!" );
            }
            else if ( !num.equals( "" ) && num.length() != 4 ) {
                errorLabel.setText( "Please Check Your Fields!" );
            }
            else {
                errorLabel.setText( "" );
                ObservableList<Course> selectedItems = null;
                try {
                    selectedItems = viewableCourse( mnem, num, title );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if( selectedItems.isEmpty() ){
                    errorLabel.setText( "No Course Found." );
                }
                list.setItems( selectedItems );
                list.setPrefWidth( 800 );
            }
        });

        HBox inputBox = new HBox( 10 );
        inputBox.getChildren().addAll( mnemonicLabel, mnemonicInput, numberLabel, numberInput, titleLabel, titleInput, searchButton );
        inputBox.setAlignment( Pos.CENTER );

        VBox searchBox = new VBox( 10 );
        searchBox.getChildren().addAll( searchLabel, inputBox, errorLabel );
        searchBox.setAlignment( Pos.CENTER );


        Button addButton = new Button( "Add Courses" );
        addButton.setOnAction(event -> {
            CourseAdding add = new CourseAdding( stage, username );
        });

        // TODO: Connect to My Reviews Scene.
        Button myReviewButton = new Button( "My Review" );
        myReviewButton.setOnAction(event -> {
            try {
                MyReviewController review=new MyReviewController(stage,username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//            stage.close();
        });

        Button logOutButton = new Button( "Log Out" );
        logOutButton.setOnAction(event -> {
            LogInController login = new LogInController( stage );
        });

        Label currentUser = new Label( "  Current User:  " + username );

        HBox functionBox = new HBox( 10 );
        functionBox.getChildren().addAll( addButton, myReviewButton, logOutButton, currentUser );
        functionBox.setAlignment( Pos.CENTER );


        GridPane root = new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(0, 10, 0, 10));
        root.setAlignment( Pos.CENTER );
        root.add( searchBox, 0, 0 );
        root.add( list, 0, 1 );
        root.add( functionBox, 0, 2 );


        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();

    }

    private ObservableList<Course> viewableCourse() throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();

        List<Course> allCourses = driver.getAllCourses();
        ObservableList<Course> items = FXCollections.observableArrayList( allCourses );
        driver.disconnect();
        return items;
    }

    private ObservableList<Course> viewableCourse( String mnem, String number, String title ) throws SQLException {
        if( mnem.equals( "" ) && number.equals( "" ) && title.equals( "" ) ){
            return viewableCourse();
        }

        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();

        List<Course> allCourses = driver.getAllCourses();
        List<Course> selectedCourses = new ArrayList<Course>();
        mnem = mnem.toUpperCase();
        title = title.toLowerCase();


        for( int i = 0; i < allCourses.size(); i++ ){
            boolean fit = true;

            Course course = allCourses.get( i );

            if( !mnem.equals( "" ) ){
                if( !course.getMnemonic().equals( mnem ) ){
                    fit = false;
                }
            }
            if( !number.equals( "" ) ){
                if( course.getCourseNumber() != Integer.parseInt( number ) ){
                    fit = false;
                }
            }
            if( !title.equals( "" ) ){
                if( !course.getCourseTitle().toLowerCase().contains( title ) ){
                    fit = false;
                }
            }

            if( fit == true ){
                selectedCourses.add( course );
            }
        }

        ObservableList<Course> items = FXCollections.observableArrayList( selectedCourses );
        driver.disconnect();
        return items;
    }
}
