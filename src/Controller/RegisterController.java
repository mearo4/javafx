package Controller;

import DAO.LaConnexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private Button btnRegister;

    @FXML
    private CheckBox chkTerms;

    @FXML
    private Hyperlink linkLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;
    Connection cn = LaConnexion.seConnecter();

    @FXML
    void handleRegister(ActionEvent event) throws IOException {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty() || !chkTerms.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'inscription",
                    "Veuillez remplir tous les champs",
                    "");
            return;
        }

        String query = "INSERT INTO user (username, password) VALUES (?, ?)";

        try {
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setString(1, username);
            String hashedPassword = HashPassword(password);
            ps.setString(2, hashedPassword);

            int i = ps.executeUpdate();
            if (i > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                        "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                        "");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/VIEW/FXMLdashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) btnRegister.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Dashboard");
                stage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'enregistrer l'utilisateur.",
                    e.getMessage());
        }
    }

    public static String HashPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(pass.getBytes());
            byte[] rbt = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : rbt) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {

        }
        return null;
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
    public void goToLogIn(ActionEvent event)
    {
        System.out.println("Lien login cliqué");
        loadScene("/VIEW/FXML.Login.fxml",linkLogin );
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
