package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;

public class DashboardController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button btnDashboard, btnAvions, btnVols, btnEquipage, btnPilotes;
    @FXML
    private Label lblDate;
    @FXML
    private HBox statsContainer;
    private Button currentActiveButton;

    @FXML
    private void initialize() {
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));

        btnAvions.setOnAction(e -> loadView("/VIEW/avion.fxml"));
        btnPilotes.setOnAction(e -> {
            System.out.println("Chargement de l'interface Pilotes");
            loadView("/VIEW/pilote.fxml");
        });
        btnEquipage.setOnAction(e -> {
            System.out.println("Chargement de l'interface Équipage");
            loadView("/VIEW/equipage.fxml");
        });
        btnVols.setOnAction(e -> loadView("/VIEW/vol.fxml"));
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Tentative de chargement du fichier: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            mainBorderPane.setCenter(view);
            System.out.println("Chargement réussi");
        } catch (IOException e) {
            System.err.println("Erreur de chargement: " + fxmlPath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Exception inattendue lors du chargement de " + fxmlPath);
            e.printStackTrace();
        }
    }
}
