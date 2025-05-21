package Controller;

import Classes.Pilote;
import Classes.Pilote.Disponibilite;
import DAO.DAOPilote;
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

public class PiloteController {
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddNew, btnSearch, btnFilter, btnArchives;
    @FXML
    private TableView<Pilote> tv;
    @FXML
    private TableColumn<Pilote, Integer> colId;
    @FXML
    private TableColumn<Pilote, String> colNom;
    @FXML
    private TableColumn<Pilote, String> colPrenom;
    @FXML
    private TableColumn<Pilote, String> colLicence;
    @FXML
    private TableColumn<Pilote, Integer> colHeuresVol;
    @FXML
    private TableColumn<Pilote, Disponibilite> colDisponibilite;
    @FXML
    private VBox detailsPane;
    @FXML
    private Label lblNoSelection;
    @FXML
    private Label detailId, detailNom, detailPrenom, detailLicence, detailHeuresVol, detailDisponibilite;
    
    ObservableList<Pilote> piloteList;
    Connection cn = LaConnexion.seConnecter();
    
    @FXML
    private Button btnChangerDisponibilite, btnSupprimer, btnModifier, btnRestaurer;
    
    private boolean showingArchived = false;
    
    @FXML
    private void initialize() {
        piloteList = FXCollections.observableArrayList();
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colLicence.setCellValueFactory(new PropertyValueFactory<>("licence"));
        colHeuresVol.setCellValueFactory(new PropertyValueFactory<>("heuresVol"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
        
        // Set up button event handlers
        btnArchives.setOnAction(e -> toggleArchiveView());
        btnAddNew.setOnAction(e -> showAddDialog());
        btnModifier.setOnAction(e -> modifierPilote());
        btnChangerDisponibilite.setOnAction(e -> changerDisponibilitePilote());
        btnSupprimer.setOnAction(e -> handleSupprimer());
        btnRestaurer.setVisible(false);
        btnRestaurer.setOnAction(e -> handleRestaurer());
        btnSearch.setOnAction(e -> handleSearch());
        
        tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
                btnChangerDisponibilite.setVisible(!newSelection.isArchived());
                btnModifier.setVisible(!newSelection.isArchived());
                btnSupprimer.setVisible(!newSelection.isArchived());
                btnRestaurer.setVisible(newSelection.isArchived());
            } else {
                hideDetails();
            }
        });
        
        lister();
    }
    
    private void toggleArchiveView() {
        showingArchived = !showingArchived;
        if (showingArchived) {
            btnArchives.setText("Voir actifs");
            piloteList.clear();
            piloteList.addAll(DAOPilote.listerArchives());
        } else {
            btnArchives.setText("Voir archives");
            piloteList.clear();
            piloteList.addAll(DAOPilote.listerTous());
        }
    }
    
    @FXML
    private void handleRestaurer() {
        Pilote selectedPilote = tv.getSelectionModel().getSelectedItem();
        if (selectedPilote != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de restauration");
            alert.setHeaderText("Restaurer le pilote");
            alert.setContentText("Êtes-vous sûr de vouloir restaurer ce pilote ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOPilote.restaurer(selectedPilote)) {
                    piloteList.remove(selectedPilote);
                    if (!showingArchived) {
                        piloteList.add(selectedPilote);
                    }
                }
            }
        }
    }
    
    @FXML
    private void handleSupprimer() {
        Pilote selectedPilote = tv.getSelectionModel().getSelectedItem();
        if (selectedPilote != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation d'archivage");
            alert.setHeaderText("Archiver le pilote");
            alert.setContentText("Êtes-vous sûr de vouloir archiver ce pilote ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOPilote.archiver(selectedPilote)) {
                    piloteList.remove(selectedPilote);
                    if (showingArchived) {
                        piloteList.add(selectedPilote);
                    }
                }
            }
        }
    }
    
    public void lister() {
        tv.getItems().clear();
        if (showingArchived) {
            piloteList.addAll(DAOPilote.listerArchives());
        } else {
            piloteList.addAll(DAOPilote.listerTous());
        }
        tv.setItems(piloteList);
    }
    
    private void showDetails(Pilote pilote) {
        detailsPane.setVisible(true);
        lblNoSelection.setVisible(false);
        
        detailId.setText("ID: " + String.valueOf(pilote.getId()));
        detailNom.setText("Nom: " + pilote.getNom());
        detailPrenom.setText("Prénom: " + pilote.getPrenom());
        detailLicence.setText("Licence: " + pilote.getLicence());
        detailHeuresVol.setText("Heures de vol: " + pilote.getHeuresVol() + " heures");
        detailDisponibilite.setText("Disponibilité: " + pilote.getDisponibilite().toString());
        
        detailDisponibilite.getStyleClass().removeAll("status-active", "status-warning", "status-danger");
        switch (pilote.getDisponibilite()) {
            case disponible:
                detailDisponibilite.getStyleClass().add("status-active");
                break;
            case repos:
            case formation:
                detailDisponibilite.getStyleClass().add("status-warning");
                break;
            case en_vol:
            case conge:
                detailDisponibilite.getStyleClass().add("status-danger");
                break;
        }
    }
    
    private void hideDetails() {
        detailsPane.setVisible(false);
        lblNoSelection.setVisible(true);
    }
    
    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            piloteList.clear();
            if (showingArchived) {
                piloteList.addAll(DAOPilote.chercherArchives(searchText));
            } else {
                piloteList.addAll(DAOPilote.chercher(searchText));
            }
        } else {
            lister();
        }
    }
    
    private void showAddDialog() {
        Dialog<Pilote> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un pilote");
        dialog.setHeaderText("Entrez les détails du nouveau pilote");
        
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        
        TextField licenceField = new TextField();
        licenceField.setPromptText("Licence");
        
        TextField heuresVolField = new TextField();
        heuresVolField.setPromptText("Heures de vol");
        
        ComboBox<Disponibilite> disponibiliteComboBox = new ComboBox<>();
        disponibiliteComboBox.getItems().addAll(Disponibilite.values());
        disponibiliteComboBox.setValue(Disponibilite.disponible);
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Licence:"), 0, 2);
        grid.add(licenceField, 1, 2);
        grid.add(new Label("Heures de vol:"), 0, 3);
        grid.add(heuresVolField, 1, 3);
        grid.add(new Label("Disponibilité:"), 0, 4);
        grid.add(disponibiliteComboBox, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String nom = nomField.getText();
                    String prenom = prenomField.getText();
                    String licence = licenceField.getText();
                    int heuresVol = Integer.parseInt(heuresVolField.getText());
                    Disponibilite disponibilite = disponibiliteComboBox.getValue();
                    
                    if (nom.isEmpty() || prenom.isEmpty() || licence.isEmpty()) {
                        throw new IllegalArgumentException("Veuillez remplir tous les champs obligatoires.");
                    }
                    
                    return new Pilote(0, nom, prenom, licence, heuresVol, disponibilite);
                } catch (NumberFormatException e) {
                    showErrorAlert("Heures de vol invalides", "Les heures de vol doivent être un nombre valide.");
                    return null;
                } catch (IllegalArgumentException e) {
                    showErrorAlert("Champs manquants", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Pilote> result = dialog.showAndWait();
        result.ifPresent(pilote -> {
            if (DAOPilote.ajouter(pilote)) {
                piloteList.clear();
                lister();
                tv.getSelectionModel().select(pilote);
            } else {
                showErrorAlert("Erreur d'ajout", "Échec de l'ajout à la base de données.");
            }
        });
    }
    
    private void changerDisponibilitePilote() {
        Pilote selectedPilote = tv.getSelectionModel().getSelectedItem();
        if (selectedPilote != null) {
            Dialog<Disponibilite> dialog = new Dialog<>();
            dialog.setTitle("Changer la disponibilité");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 100, 10, 10));
            
            ComboBox<Disponibilite> disponibiliteCombo = new ComboBox<>();
            disponibiliteCombo.getItems().addAll(Disponibilite.values());
            disponibiliteCombo.setValue(selectedPilote.getDisponibilite());
            disponibiliteCombo.setPrefWidth(200);
            
            grid.add(new Label("Nouvelle disponibilité:"), 0, 0);
            grid.add(disponibiliteCombo, 1, 0);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.getDialogPane().setMinWidth(400);
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return disponibiliteCombo.getValue();
                }
                return null;
            });
            
            Optional<Disponibilite> result = dialog.showAndWait();
            result.ifPresent(newDisponibilite -> {
                if (DAOPilote.changerDisponibilite(selectedPilote, newDisponibilite)) {
                    selectedPilote.setDisponibilite(newDisponibilite);
                    tv.refresh();
                    showDetails(selectedPilote);
                }
            });
        }
    }
    
    private void modifierPilote() {
        Pilote selectedPilote = tv.getSelectionModel().getSelectedItem();
        if (selectedPilote != null) {
            Dialog<Pilote> dialog = new Dialog<>();
            dialog.setTitle("Modifier un pilote");
            dialog.setHeaderText("Modifier les détails du pilote");
            
            ButtonType modifyButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            TextField nomField = new TextField(selectedPilote.getNom());
            TextField prenomField = new TextField(selectedPilote.getPrenom());
            TextField licenceField = new TextField(selectedPilote.getLicence());
            TextField heuresVolField = new TextField(String.valueOf(selectedPilote.getHeuresVol()));
            
            ComboBox<Disponibilite> disponibiliteComboBox = new ComboBox<>();
            disponibiliteComboBox.getItems().addAll(Disponibilite.values());
            disponibiliteComboBox.setValue(selectedPilote.getDisponibilite());
            
            grid.add(new Label("Nom:"), 0, 0);
            grid.add(nomField, 1, 0);
            grid.add(new Label("Prénom:"), 0, 1);
            grid.add(prenomField, 1, 1);
            grid.add(new Label("Licence:"), 0, 2);
            grid.add(licenceField, 1, 2);
            grid.add(new Label("Heures de vol:"), 0, 3);
            grid.add(heuresVolField, 1, 3);
            grid.add(new Label("Disponibilité:"), 0, 4);
            grid.add(disponibiliteComboBox, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == modifyButtonType) {
                    try {
                        String nom = nomField.getText();
                        String prenom = prenomField.getText();
                        String licence = licenceField.getText();
                        int heuresVol = Integer.parseInt(heuresVolField.getText());
                        Disponibilite disponibilite = disponibiliteComboBox.getValue();
                        
                        if (nom.isEmpty() || prenom.isEmpty() || licence.isEmpty()) {
                            throw new IllegalArgumentException("Veuillez remplir tous les champs obligatoires.");
                        }
                        
                        selectedPilote.setNom(nom);
                        selectedPilote.setPrenom(prenom);
                        selectedPilote.setLicence(licence);
                        selectedPilote.setHeuresVol(heuresVol);
                        selectedPilote.setDisponibilite(disponibilite);
                        
                        return selectedPilote;
                    } catch (NumberFormatException e) {
                        showErrorAlert("Heures de vol invalides", "Les heures de vol doivent être un nombre valide.");
                        return null;
                    } catch (IllegalArgumentException e) {
                        showErrorAlert("Champs manquants", e.getMessage());
                        return null;
                    }
                }
                return null;
            });
            
            Optional<Pilote> result = dialog.showAndWait();
            result.ifPresent(pilote -> {
                if (DAOPilote.mettreAJour(pilote)) {
                    tv.refresh();
                    showDetails(pilote);
                } else {
                    showErrorAlert("Erreur de modification", "Échec de la mise à jour dans la base de données.");
                }
            });
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