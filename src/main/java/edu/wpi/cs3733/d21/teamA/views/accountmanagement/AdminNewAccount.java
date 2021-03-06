package edu.wpi.cs3733.d21.teamA.views.accountmanagement;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.db.DatabaseUtil;
import edu.wpi.cs3733.d21.teamA.db.enums.USERTYPE;
import edu.wpi.cs3733.d21.teamA.extras.EmailService;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminNewAccount extends GenericPage { //TODO: Make this extend CreateNewAccount instead of GenericPage
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

        @FXML
        private JFXComboBox userType;

        private DatabaseController db;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = DatabaseController.getInstance();
        userType.setItems( FXCollections
                .observableArrayList(DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.EMPLOYEE),DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.ADMIN))
        );
    }
    @FXML
    public void submit_button() throws SQLException, IOException {
        // maybe we should wait to check emails until they work? Not entirely sure how this regex works the $ and ^
/*        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!email.matches(pat.pattern())){
            popUp("Failed Submission.", "\n\n\n\n\n\nInvalid email.");

        }*/
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() ||
            emailAddress.getText().isEmpty() || userName.getText().isEmpty() ||
            userType.getSelectionModel().getSelectedItem() == null) {
            popUp("Failed Submission.", "\n\n\n\n\nPlease fill out all fields listed.");
            return;
        }

        String email = emailAddress.getText();

        if (!password.getText().equals(confirmPassword.getText())) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nThe two passwords do not match, Try again.");
            return;
        }

        if (db.checkUserExistsByUsername(userName.getText())) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nUsername already exists.");
            return;
        }

        if (db.checkUserExistsByEmail(email)) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nUser with this email already exists.");
            return;
        }

        Map<String, String> user = new HashMap<String, String>();
        user.put("USERNAME", userName.getText());
        user.put("FIRSTNAME", firstName.getText());
        user.put("LASTNAME", lastName.getText());
        user.put("EMAIL", emailAddress.getText());
        user.put("USERTYPE", userType.getSelectionModel().getSelectedItem().toString());

        Security.addHashedPassword(user, password.getText());

        db.addUser(user);

        EmailService.sendAccountCreationEmail(emailAddress.getText(), userName.getText(), firstName.getText());

        popUp ( "Account Success", "\n\n\n\n\nThe account was successfully created." );
        sceneSwitch("AdminMainPage");
    }
}

