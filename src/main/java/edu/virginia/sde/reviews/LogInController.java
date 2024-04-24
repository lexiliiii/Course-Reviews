package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

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
        Button registerButton = new Button( "New User Register" );

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
        buttonBox.getChildren().addAll( logInButton, registerButton );
        buttonBox.setAlignment(Pos.CENTER);

        // Lay out everything in the scene vertically
        VBox root = new VBox();
        root.setSpacing( 15 );
        root.setAlignment( Pos.CENTER );

        root.getChildren().add( logInInstruction );
        root.getChildren().add( userNameBox );
        root.getChildren().add( passwordBox );
        root.getChildren().add( buttonBox );

        // Create Scene
        Scene scene = new Scene( root,1280, 780 );
        stage.setScene(scene);
        stage.show();
    }

}
