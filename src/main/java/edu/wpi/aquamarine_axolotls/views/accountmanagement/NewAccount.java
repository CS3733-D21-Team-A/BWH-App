package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.extras.Security;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class NewAccount extends GenericPage {
    @FXML
    public javafx.scene.image.ImageView tfaView;
    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXTextField userName;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField confirmPassword;

    @FXML private JFXTextField verification;
    @FXML
    private ImageView view;

    @FXML private Label tfaSource;

    @FXML
    private Label passwordMatchLabel;

    @FXML
    private Label verfIncorrect;
    @FXML
    private Pane tfaPane;
    @FXML
    private JFXButton submitButton;
    @FXML
    private JFXCheckBox tfa;

    private DatabaseController db;

    private Security security;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = DatabaseController.getInstance();
        Subject subject = new Subject();
        PasswordObserver passwordObserver = new PasswordObserver(subject, passwordMatchLabel);
        subject.attach ( passwordObserver );
    //    password.textProperty().addListener(observable -> {
      //      subject.setNewPassword(password.getText());
       // });

        //confirmPassword.textProperty().addListener(observable -> {
          //  subject.setConfirmPassword(confirmPassword.getText());
       //  });
    }

    @FXML
    public void submit_button(ActionEvent actionEvent) throws SQLException {
        // maybe we should wait to check emails until they work? Not entirely sure how this regex works the $ and ^
/*        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!email.matches(pat.pattern())){
            popUp("Failed Submission.", "\n\n\n\n\n\nInvalid email.");

        }*/
        if ( firstName.getText ( ).isEmpty ( ) || lastName.getText ( ).isEmpty ( ) ||
             emailAddress.getText ( ).isEmpty ( ) || userName.getText ( ).isEmpty ( ) ) {
            popUp ( "Failed Submission." ,"\n\n\n\n\nPlease fill out all fields listed." );
            return;
        }

        if ( !password.getText ( ).equals ( confirmPassword.getText ( ) ) ) {
            popUp ( "Account Creation Failed" ,"\n\n\n\n\n\nThe two passwords do not match, Try again." );
            return;
        }

        if ( db.checkUserExists ( userName.getText ( ) ) ) {
            popUp ( "Account Creation Failed" ,"\n\n\n\n\n\nUsername already exists." );
            return;
        }

        for (Map<String, String> usr : db.getUsers ( )) {
            String email = emailAddress.getText ( );
            if ( usr.get ( "EMAIL" ).equals ( email ) ) {
                popUp ( "Account Creation Failed" ,"\n\n\n\n\n\nUser with this email already exists." );
                return;
            }
        }
        Map<String, String> user = new HashMap<String, String> ( );
        user.put ( "USERNAME" ,userName.getText ( ) );
        user.put ( "FIRSTNAME" ,firstName.getText ( ) );
        user.put ( "LASTNAME" ,lastName.getText ( ) );
        user.put ( "EMAIL" ,emailAddress.getText ( ) );
        user.put ( "USERTYPE" ,"Patient" );
        user.put ( "PASSWORD" ,password.getText ( ) );
        db.addUser ( user );

        if ( tfa.isSelected ( ) ) {

            tfaSelected();
        }

        else {
            popUp ( "Account Success" ,"\n\n\n\n\n\nThe account you submitted was successfully created." );
            sceneSwitch ( "LogIn" );
        }
    }

    public void tfaSelected(){
        tfaPane.setVisible ( true );
        verfIncorrect.setVisible ( false );
        submitButton.setVisible (false  );
        try {
            Pair<String,byte[]> TOTPinformation = Security.enableTOTP (userName.getText());
            readByteArray(TOTPinformation.getValue ());
            tfaSource.setText ( "Secret: \n"+ TOTPinformation.getKey ());

        } catch (QrGenerationException | SQLException | IOException e) {
            e.printStackTrace ( );
        }

    }


    public void readByteArray(byte[]  pair) throws IOException {
        Image img = new Image(new ByteArrayInputStream(pair));

        tfaView.setImage  ( img );
    }


    public void tfasubmit_button() throws SQLException {
        boolean verificationSuccess = Security.verifyTOTP(userName.getText (),verification.getText ( ) );
        if(verificationSuccess){
            popUp ( "Account Success" ,"\n\n\n\n\n\nThe account you submitted was successfully created." );
            sceneSwitch ( "LogIn" );
        }
        else{
            verfIncorrect.setVisible ( true );

        }

    }

    public void cancel2fa(){
        tfaPane.setVisible ( false );
        try {
            db.deleteUser ( userName.getText () );
        } catch (SQLException throwables) {
            throwables.printStackTrace ( );
        }
        submitButton.setVisible ( true );
    }
}

