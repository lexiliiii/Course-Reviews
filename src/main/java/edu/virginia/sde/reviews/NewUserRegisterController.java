package edu.virginia.sde.reviews;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.SQLException;
import java.util.List;

public class NewUserRegisterController {

    public NewUserRegisterController( Stage stage ){
        stage.setTitle( "New User Register" );

        Label logInInstruction = new Label( "Please create your username and password below" );
        logInInstruction.setFont( Font.font("Times New Roman", FontWeight.BOLD, 25) );
        logInInstruction.setStyle( "-fx-text-fill:white" );

        // Ask for Username
        Label userNameRequest = new Label( "Username:" );
        TextField input_userName = new TextField( );
        input_userName.setPrefWidth( 200 );
        input_userName.setMaxWidth( 200 );
        userNameRequest.setFont( Font.font("Times New Roman", FontWeight.BOLD, 17) );
        userNameRequest.setStyle("-fx-text-fill: white");

        // Ask for Password
        Label passwordRequest = new Label( "Password:" );
        TextField input_password = new TextField( );
        input_password.setPrefWidth( 200 );
        input_password.setMaxWidth( 200 );
        passwordRequest.setFont( Font.font("Times New Roman", FontWeight.BOLD, 17) );
        passwordRequest.setStyle("-fx-text-fill: white");


        // Buttons lead to next step
        Button registerButton = new Button( "Register" );
        registerButton.setFont(new Font("Times New Roman", 14));

        Button closeButton = new Button("Back to Log In");
        closeButton.setFont( new Font( "Times New Roman", 14 ));
        closeButton.setOnAction(event ->{
            input_userName.clear();
            input_password.clear();
            LogInController login = new LogInController( stage );
        });

        // Lay out the Username line horizontally
        HBox userNameBox = new HBox( 10 );
        userNameBox.getChildren().addAll( userNameRequest, input_userName );
        userNameBox.setAlignment( Pos.CENTER );

        // Lay out the Password line horizontally
        HBox passwordBox = new HBox( 10 );
        passwordBox.getChildren().addAll( passwordRequest, input_password );
        passwordBox.setAlignment( Pos.CENTER );

        // Lay out the Buttons line horizontally
        HBox buttonBox = new HBox(10);  // 10 pixels spacing between buttons
        buttonBox.getChildren().addAll( registerButton, closeButton );
        buttonBox.setAlignment(Pos.CENTER);

        // Lay out everything in the scene vertically
        VBox root = new VBox();
        root.setSpacing( 15 );
        root.setAlignment( Pos.CENTER );

        root.getChildren().add( logInInstruction );
        root.getChildren().add( userNameBox );
        root.getChildren().add( passwordBox );
        root.getChildren().add( buttonBox );

        Label errorLabel = new Label();
        root.getChildren().add( errorLabel );
        errorLabel.setFont( new Font( "Times New Roman", 14) );
        errorLabel.setStyle( "-fx-text-fill:red" );

        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #bdc3c7 0%, #7595af 100%);");

        registerButton.setOnAction(e -> {
            String username = input_userName.getText();
            String password = input_password.getText();
            try {
                if ( userNameAuthenticate( username, password) ) {
                    errorLabel.setText("");
                    registerUser( username, password );
                    input_userName.clear();
                    input_password.clear();
                    LogInController login = new LogInController( stage );
                } else {
                    input_userName.clear();
                    input_password.clear();
                    errorLabel.setText("Invalid username or password.");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });


        // Create Scene
        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }

    private boolean userNameAuthenticate( String username, String password ) throws SQLException {
        if( password.length() < 8 ){
            return false;
        }

        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();

        List<User> allUsers = driver.getAllUsers();
        for( int i = 0; i < allUsers.size(); i++ ){
            User user = allUsers.get(i);
            if( username.equals( user.getUsername() )){
                return false;
            }
        }
        driver.disconnect();
        return true;
    }

    private void registerUser( String username, String password ) throws SQLException {
        DatabaseReviews driver = new DatabaseReviews("reviews.sqlite" );
        driver.connect();
        driver.createTables();
        driver.registerUser( username, password );
        driver.disconnect();
    }
}