package Controller;

import Classes.Avion;
import Classes.Avion.Tetat;
import DAO.DAOAvion;
import DAO.LaConnexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AvionController {

    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddNew, btnSearch, btnFilter;
    @FXML
    private TableView<Avion> tv;
    @FXML
    private TableColumn<Avion, Integer> colId;
    @FXML
    private TableColumn<Avion, String> colModele;
    @FXML
    private TableColumn<Avion, Integer> colCapacite;
    @FXML
    private TableColumn<Avion, Tetat> colEtat;
    @FXML
    private VBox detailsPane;
    @FXML
    private Label lblNoSelection;
    @FXML
    private Label detailId, detailModele, detailCapacite, detailEtat;

    ObservableList<Avion> AvionList;
    Connection cn = LaConnexion.seConnecter();
    
    private boolean showingArchived = false;
    
    @FXML
    private Button btnChangerEtat, btnSupprimer, btnArchives, btnRestaurer;

    @FXML
    private void initialize() {
        AvionList = FXCollections.observableArrayList();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colEtat.setCellFactory(column -> new TableCell<Avion, Tetat>() {
            @Override
            protected void updateItem(Tetat item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item == Tetat.disponible) {
                        setStyle("-fx-background-color: #e6f7e9; -fx-text-fill: #2a9d8f; -fx-padding: 5; -fx-background-radius: 3;");
                    } else if (item == Tetat.maintenance) {
                        setStyle("-fx-background-color: #fff4e5; -fx-text-fill: #f9c74f; -fx-padding: 5; -fx-background-radius: 3;");
                    } else if (item == Tetat.en_vol) {
                        setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 5; -fx-background-radius: 3;");
                    }
                }
            }
        });
        
        lister();

        tv.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showDetails(newVal);
            } else {
                hideDetails();
            }
        });
        
        btnAddNew.setOnAction(e -> showAddDialog());
        btnSearch.setOnAction(e -> searchAvions());
        btnChangerEtat.setOnAction(e -> changerEtatAvion());
        btnSupprimer.setOnAction(e -> supprimerAvion());
        btnArchives.setOnAction(e -> toggleArchiveView());
        btnRestaurer.setOnAction(e -> restaurerAvion());
        
        btnRestaurer.setVisible(false);
        btnChangerEtat.setVisible(true);
        btnSupprimer.setVisible(true);
        
        hideDetails();
    }

    public void lister() {
        tv.getItems().clear();
        AvionList.clear();
        if (showingArchived) {
            AvionList.addAll(DAOAvion.listerArchives());
        } else {
            AvionList.addAll(DAOAvion.listerTous());
        }
        tv.setItems(AvionList);
    }

    private void showDetails(Avion avion) {
        detailsPane.setVisible(true);
        lblNoSelection.setVisible(false);

        detailId.setText("ID : " + String.valueOf(avion.getId()));
        detailModele.setText("Modèle : " + avion.getModele());
        detailCapacite.setText("Capacité : " + avion.getCapacite() + " passagers");
        detailEtat.setText("État : " + avion.getEtat().toString());
        
        btnChangerEtat.setVisible(!avion.isArchived());
        btnSupprimer.setVisible(!avion.isArchived());
        btnRestaurer.setVisible(avion.isArchived());
        
        detailEtat.getStyleClass().removeAll("status-active", "status-warning", "status-danger");
        switch (avion.getEtat()) {
            case disponible:
                detailEtat.getStyleClass().add("status-active");
                break;
            case maintenance:
                detailEtat.getStyleClass().add("status-warning");
                break;
            case en_vol:
                detailEtat.getStyleClass().add("status-danger");
                break;
        }
    }

    private void hideDetails() {
        detailsPane.setVisible(false);
        lblNoSelection.setVisible(true);
    }

    private void toggleArchiveView() {
        showingArchived = !showingArchived;
        if (showingArchived) {
            btnArchives.setText("Voir actifs");
            AvionList.clear();
            AvionList.addAll(DAOAvion.listerArchives());
        } else {
            btnArchives.setText("Voir archives");
            AvionList.clear();
            AvionList.addAll(DAOAvion.listerTous());
        }
    }
    
    private void restaurerAvion() {
        Avion selectedAvion = tv.getSelectionModel().getSelectedItem();
        if (selectedAvion != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de restauration");
            alert.setHeaderText("Restaurer l'avion");
            alert.setContentText("Êtes-vous sûr de vouloir restaurer cet avion ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOAvion.restaurer(selectedAvion)) {
                    AvionList.remove(selectedAvion);
                    if (!showingArchived) {
                        AvionList.add(selectedAvion);
                    }
                }
            }
        }
    }

    private void supprimerAvion() {
        Avion selectedAvion = tv.getSelectionModel().getSelectedItem();
        if (selectedAvion != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation d'archivage");
            alert.setHeaderText("Archiver l'avion");
            alert.setContentText("Êtes-vous sûr de vouloir archiver cet avion ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOAvion.archiver(selectedAvion)) {
                    AvionList.remove(selectedAvion);
                    if (showingArchived) {
                        AvionList.add(selectedAvion);
                    }
                }
            }
        }
    }

    private void showAddDialog() {
        Dialog<Avion> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un n avion");
        dialog.setHeaderText("Entrez les détails du nouvel avion");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        ComboBox<String> modeleComboBox = new ComboBox<>();
        modeleComboBox.getItems().addAll(Avion.MODELES_CAPACITES.keySet());
        modeleComboBox.setPromptText("Sélectionnez un modèle");

        TextField capaciteField = new TextField();
        capaciteField.setEditable(false);

        modeleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Integer capacite = Avion.MODELES_CAPACITES.get(newVal);
                capaciteField.setText(String.valueOf(capacite));
            } else {
                capaciteField.clear();
            }
        });

        ComboBox<Avion.Tetat> etatComboBox = new ComboBox<>();
        etatComboBox.getItems().addAll(Avion.Tetat.values());
        etatComboBox.setValue(Avion.Tetat.disponible);

        grid.add(new Label("Modèle:"), 0, 0);
        grid.add(modeleComboBox, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(capaciteField, 1, 1);
        grid.add(new Label("État:"), 0, 2);
        grid.add(etatComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String selectedModele = modeleComboBox.getValue();
                    if (selectedModele == null) {
                        throw new IllegalArgumentException("Veuillez sélectionner un modèle.");
                    }
                    int capacite = Integer.parseInt(capaciteField.getText());
                    return new Avion(0, selectedModele, capacite, etatComboBox.getValue());
                } catch (NumberFormatException e) {
                    showErrorAlert("Capacité invalide", "La capacité doit être un nombre valide.");
                    return null;
                } catch (IllegalArgumentException e) {
                    showErrorAlert("Modèle manquant", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Avion> result = dialog.showAndWait();
        result.ifPresent(avion -> {
            if (DAOAvion.ajouter(avion)) {
                AvionList.clear(); 
                lister();         
                tv.getSelectionModel().select(avion);
            } else {
                showErrorAlert("Erreur d'ajout", "Échec de l'ajout à la base.");
            }
        });
    }

   private void changerEtatAvion() {
    Avion selectedAvion = tv.getSelectionModel().getSelectedItem();
    if (selectedAvion != null) {
        Dialog<Avion.Tetat> dialog = new Dialog<>();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10)); 

        ComboBox<Avion.Tetat> etatCombo = new ComboBox<>();
        etatCombo.getItems().addAll(Avion.Tetat.values());
        etatCombo.setPrefWidth(200); 

        grid.add(new Label("Nouvel état:"), 0, 0);
        grid.add(etatCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
   
        dialog.getDialogPane().setMinWidth(400);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return etatCombo.getValue();
            }
            return null;
        });

        Optional<Avion.Tetat> result = dialog.showAndWait();
        result.ifPresent(newEtat -> {
            if (DAOAvion.changerEtat(selectedAvion, newEtat)) {
                selectedAvion.setEtat(newEtat);
                tv.refresh();
                showDetails(selectedAvion);
            }
        });
    }}

    private void searchAvions() {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            AvionList.clear();
            if (showingArchived) {
                AvionList.addAll(DAOAvion.chercherArchives(searchText));
            } else {
                AvionList.addAll(DAOAvion.chercher(searchText));
            }
        } else {
            lister();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
