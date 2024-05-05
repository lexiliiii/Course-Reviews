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

public class CourseAdding {

    public CourseAdding(Stage stage) {

        stage.setTitle( "Course Adding" );

        Label addLabel = new Label( "Add Your Course" );

        Label mnemonicLabel = new Label( "Subject Mnemonic: " );
        TextField mnemonicInput = new TextField();
        Label numberLabel = new Label( "Course Number: " );
        TextField numberInput = new TextField();
        Label titleLabel = new Label( "Course Title: " );
        TextField titleInput = new TextField();

        Label errorLabel = new Label();

        // TODO: Action for Adding
        Button addButton = new Button( "Add" );
        addButton.setOnAction(event -> {
//            stage.close();
        });

        HBox inputBox = new HBox( 10 );
        inputBox.getChildren().addAll( mnemonicLabel, mnemonicInput,
                numberLabel, numberInput,
                titleLabel, titleInput );
        inputBox.setAlignment( Pos.CENTER );

        VBox addBox = new VBox( 10 );
        addBox.getChildren().addAll( addLabel,
                inputBox, errorLabel, addButton );
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
