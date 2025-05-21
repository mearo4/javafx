package Controller;

import static Controller.RegisterController.HashPassword;
import DAO.LaConnexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkRemember;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegister;
    Connection cn = LaConnexion.seConnecter();

    @FXML
    private void initialize() {
        // Initialisation du contrôleur
    }

    @FXML
    private void Login(ActionEvent event) throws SQLException, IOException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                    "Veuillez remplir tous les champs", 
                    "");
            return;
        }

        String requete  = "SELECT password FROM user WHERE username = ?";
        PreparedStatement ps = cn.prepareStatement(requete);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
      if (rs.next()) {
            String Password= rs.getString("password");

            if (Password.equals(HashPassword(password))) {
                System.out.println(HashPassword(password));
                // Connexion réussie
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Connexion réussie", "Bienvenue, " + username + "!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/VIEW/FXMLdashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard");
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                        "Mot de passe incorrect", "Veuillez vérifier votre mot de passe.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                    "Utilisateur introuvable", "Veuillez vérifier votre nom d'utilisateur.");
        }
    }
    private void loadScene(String fxmlPath, Control source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToSignUp(ActionEvent event)
    {
        System.out.println("Lien inscription cliqué");
        loadScene("/VIEW/Register.fxml",linkRegister );
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}