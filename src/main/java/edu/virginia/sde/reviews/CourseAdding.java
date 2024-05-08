package edu.virginia.sde.reviews;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class CourseAdding {

    public CourseAdding(Stage stage, String username) {

        stage.setTitle( "Course Adding" );

        Label addLabel = new Label( "Add Your Course" );

        Label mnemonicLabel = new Label( "Subject Mnemonic: " );
        TextField mnemonicInput = new TextField();
        Label numberLabel = new Label( "Course Number: " );
        TextField numberInput = new TextField();
        Label titleLabel = new Label( "Course Title: " );
        TextField titleInput = new TextField();

        Label errorLabel = new Label();


        Button addButton = new Button( "Add Course" );
        addButton.setOnAction(event -> {
            String mnem = mnemonicInput.getText();
            String number = numberInput.getText();
            String title = titleInput.getText();
            if( !mnem.equals("") && !number.equals("") && !title.equals("") ){
                try {
                    if( courseFormatCheck( mnem, number, title) ){
                        if( courseAlreadyExistedCheck( mnem, number, title ) ){
                            errorLabel.setText( "Course already existed." );
                        }
                        else {
                            addCourse( mnem, number, title );
                            mnemonicInput.clear();
                            numberInput.clear();
                            titleInput.clear();
                            errorLabel.setText( "Course added successfully!" );
                        }
                    }
                    else {
                        errorLabel.setText( "Incorrect format provided!" );
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                errorLabel.setText( "Incorrect data format provided!" );
            }
//            stage.close();
        });
        Button exitButton = new Button( "Back to Course Search" );
        exitButton.setOnAction(event -> {
            try {
                CourseSearchController courseSearchController = new CourseSearchController( stage, username );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        HBox inputBox = new HBox( 10 );
        inputBox.getChildren().addAll( mnemonicLabel, mnemonicInput,
                numberLabel, numberInput,
                titleLabel, titleInput );
        inputBox.setAlignment( Pos.CENTER );

        HBox buttonBox = new HBox( 10 );
        buttonBox.getChildren().addAll( addButton, exitButton );
        buttonBox.setAlignment( Pos.CENTER );

        VBox addBox = new VBox( 10 );
        addBox.getChildren().addAll( addLabel,
                inputBox, errorLabel, buttonBox );
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

    private boolean courseFormatCheck( String mnem, String number, String title ){
        if( mnem.length() < 2 || mnem.length() > 4 ){
//            System.out.println( "0" );
            return false;
        }

        for( int i = 0; i < mnem.length(); i++ ){
            if  (!Character.isLetter( mnem.charAt(i) ) ) {
//                System.out.println( "1" );
                return false;
            }
        }

        if( number.length() != 4 ){
//            System.out.println( "2" );
            return false;
        }

        for( int j = 0; j < number.length(); j++ ){
            if ( !Character.isDigit( number.charAt( j ) ) ) {
//                System.out.println( "3" );
                return false;
            }
        }

        if( title.length() < 1 || title.length() > 50 ){
//            System.out.println( "4" );
            return false;
        }

        return true;
    }

    private boolean courseAlreadyExistedCheck( String mnem, String number, String title ) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();

        mnem = mnem.toUpperCase();
        int num = Integer.parseInt( number );

        List<Course> allCourse = driver.getAllCourses();
        for( int i = 0; i < allCourse.size(); i++ ){
            Course course = allCourse.get( i );
//            if( course.getMnemonic().equals( mnem )
//                    && course.getCourseNumber() == num
//                    && course.getCourseTitle().equals( title )){
//                return true;
//            }
            if( course.getMnemonic().equals( mnem ) && course.getCourseNumber() == num ){
                return true;
            }
        }
        driver.disconnect();
        return false;
    }

    private void addCourse( String mnem, String number, String title ) throws SQLException {
        mnem = mnem.toUpperCase();
        int num = Integer.parseInt( number );
        double avgRate = 0.00;

        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();
        driver.addCourse( mnem, num, title, avgRate );
        driver.disconnect();
    }
}
