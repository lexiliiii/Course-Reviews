package edu.virginia.sde.reviews;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.List;

public class LogInController {

    public LogInController(Stage stage){
        stage.setTitle( "Log In" );

        Label logInInstruction = new Label( "Welcome! Please log in into your account" );

        // Ask for Username
        Label userNameRequest = new Label( "Username:" );
        TextField input_userName = new TextField( );
        input_userName.setPrefWidth( 200 );
        input_userName.setMaxWidth( 200 );

        // Ask for Password
        Label passwordRequest = new Label( "Password:" );
        TextField input_password = new TextField( );
        input_password.setPrefWidth( 200 );
        input_password.setMaxWidth( 200 );

        // Buttons lead to next step
        Button logInButton = new Button( "Log In" );
        logInButton.setPrefSize( 75, 20 );
        Button registerButton = new Button( "New User Register" );
//        registerButton.setPrefSize( 100, 20 );

        // Close buttons
        Button closeButton = new Button("Exit");
//        closeButton.setPrefSize(100, 20);
        closeButton.setOnAction(event -> stage.close());

//        // Lay out the Close button
//        HBox close = new HBox(10 );
//        close.getChildren().add( closeButton );
//        close.setAlignment( Pos.BOTTOM_RIGHT );

        // Lay out the Username line horizontally
        HBox userNameBox = new HBox( 10 );
        userNameBox.getChildren().addAll( userNameRequest, input_userName );
        userNameBox.setAlignment( Pos.CENTER );

        // Lay out the Password line horizontally
        HBox passwordBox = new HBox( 10 );
        passwordBox.getChildren().addAll( passwordRequest, input_password );
        passwordBox.setAlignment( Pos.CENTER );

        // Lay out the Buttons line horizontally
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll( logInButton, registerButton, closeButton );
        buttonBox.setAlignment(Pos.CENTER);

        // Lay out everything in the scene vertically
        VBox root = new VBox();
        root.setSpacing( 15 );
        root.setAlignment( Pos.CENTER );

        root.getChildren().add( logInInstruction );
        root.getChildren().add( userNameBox );
        root.getChildren().add( passwordBox );
        root.getChildren().add( buttonBox );
//        root.getChildren().add( close );

        Label errorLabel = new Label();
        root.getChildren().add( errorLabel );

        logInButton.setOnAction(e -> {
            String username = input_userName.getText();
            String password = input_password.getText();
            try {
                if ( authenticate(username, password) ) {
                    errorLabel.setText("");
                    input_userName.clear();
                    input_password.clear();
                    CourseSearchController courseSearch = new CourseSearchController( stage );
                } else {
                    input_userName.clear();
                    input_password.clear();
                    errorLabel.setText("Invalid username or password.");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        registerButton.setOnAction(e -> {
            NewUserRegisterController register = new NewUserRegisterController( stage );
        });

        // Create Scene
        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }


    private boolean authenticate( String username, String password ) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();

        boolean result = false;

        List<User> allUsers = driver.getAllUsers();
        for( int i = 0; i < allUsers.size(); i++ ){
            User user = allUsers.get(i);
            if( username.equals( user.getUsername() )){
                if( password.equals( user.getPassword() )){
                    result = true;
                }
            }
        }
        driver.disconnect();

        return result;

    }
}
