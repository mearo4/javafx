package Controller;

import Classes.Equipage;
import Classes.Equipage.Disponibilite;
import Classes.Equipage.Fonction;
import DAO.DAOEquipage;
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

public class EquipageController {
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddNew, btnSearch, btnFilter;
    @FXML
    private TableView<Equipage> tv;
    @FXML
    private TableColumn<Equipage, Integer> colId;
    @FXML
    private TableColumn<Equipage, String> colNom;
    @FXML
    private TableColumn<Equipage, String> colPrenom;
    @FXML
    private TableColumn<Equipage, Fonction> colFonction;
    @FXML
    private TableColumn<Equipage, Integer> colExperience;
    @FXML
    private TableColumn<Equipage, Disponibilite> colDisponibilite;
    @FXML
    private VBox detailsPane;
    @FXML
    private Label lblNoSelection;
    @FXML
    private Label detailId, detailNom, detailPrenom, detailFonction, detailExperience, detailDisponibilite;
    
    ObservableList<Equipage> equipageList;
    Connection cn = LaConnexion.seConnecter();
    
    private boolean showingArchived = false;
    
    @FXML
    private Button btnChangerDisponibilite, btnSupprimer, btnModifier, btnArchives, btnRestaurer;
    
    @FXML
    private void initialize() {
        equipageList = FXCollections.observableArrayList();
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colFonction.setCellValueFactory(new PropertyValueFactory<>("fonction"));
        colExperience.setCellValueFactory(new PropertyValueFactory<>("experience"));
        colDisponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
        
        colFonction.setCellFactory(column -> new TableCell<Equipage, Fonction>() {
            @Override
            protected void updateItem(Fonction item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString().replace('_', ' '));
                }
            }
        });
        
        colDisponibilite.setCellFactory(column -> new TableCell<Equipage, Disponibilite>() {
            @Override
            protected void updateItem(Disponibilite item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    switch (item) {
                        case disponible:
                            setStyle("-fx-background-color: #e6f7e9; -fx-text-fill: #2a9d8f; -fx-padding: 5; -fx-background-radius: 3;");
                            break;
                        case en_vol:
                            setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 5; -fx-background-radius: 3;");
                            break;
                        case repos:
                            setStyle("-fx-background-color: #fff4e5; -fx-text-fill: #f9c74f; -fx-padding: 5; -fx-background-radius: 3;");
                            break;
                        case conge:
                            setStyle("-fx-background-color: #fce4ec; -fx-text-fill: #e91e63; -fx-padding: 5; -fx-background-radius: 3;");
                            break;
                        case formation:
                            setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #9c27b0; -fx-padding: 5; -fx-background-radius: 3;");
                            break;
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
        btnSearch.setOnAction(e -> searchEquipages());
        btnChangerDisponibilite.setOnAction(e -> changerDisponibiliteEquipage());
        btnSupprimer.setOnAction(e -> supprimerEquipage());
        btnModifier.setOnAction(e -> modifierEquipage());
        btnArchives.setOnAction(e -> toggleArchiveView());
        btnRestaurer.setOnAction(e -> restaurerEquipage());
        
        btnRestaurer.setVisible(false);
        btnChangerDisponibilite.setVisible(true);
        btnModifier.setVisible(true);
        btnSupprimer.setVisible(true);
        
        hideDetails();
    }
    
    public void lister() {
        tv.getItems().clear();
        equipageList.clear();
        if (showingArchived) {
            equipageList.addAll(DAOEquipage.listerArchives());
        } else {
            equipageList.addAll(DAOEquipage.listerTous());
        }
        tv.setItems(equipageList);
    }
    
    private void showDetails(Equipage equipage) {
        detailsPane.setVisible(true);
        lblNoSelection.setVisible(false);
        
        detailId.setText("ID: " + String.valueOf(equipage.getId()));
        detailNom.setText("Nom: " + equipage.getNom());
        detailPrenom.setText("Prénom: " + equipage.getPrenom());
        detailFonction.setText("Fonction: " + equipage.getFonction().toString().replace('_', ' '));
        detailExperience.setText("Expérience: " + equipage.getExperience() + " années");
        detailDisponibilite.setText("Disponibilité: " + equipage.getDisponibilite().toString());
        
        btnChangerDisponibilite.setVisible(!equipage.isArchived());
        btnModifier.setVisible(!equipage.isArchived());
        btnSupprimer.setVisible(!equipage.isArchived());
        btnRestaurer.setVisible(equipage.isArchived());
        
        detailDisponibilite.getStyleClass().removeAll("status-active", "status-warning", "status-danger");
        switch (equipage.getDisponibilite()) {
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
    
    private void showAddDialog() {
        Dialog<Equipage> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un membre d'équipage");
        dialog.setHeaderText("Entrez les détails du nouveau membre d'équipage");
        
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
        
        ComboBox<Fonction> fonctionComboBox = new ComboBox<>();
        fonctionComboBox.getItems().addAll(Fonction.values());
        fonctionComboBox.setPromptText("Sélectionnez une fonction");
        
        TextField experienceField = new TextField();
        experienceField.setPromptText("Années d'expérience");
        
        ComboBox<Disponibilite> disponibiliteComboBox = new ComboBox<>();
        disponibiliteComboBox.getItems().addAll(Disponibilite.values());
        disponibiliteComboBox.setValue(Disponibilite.disponible);
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Fonction:"), 0, 2);
        grid.add(fonctionComboBox, 1, 2);
        grid.add(new Label("Expérience:"), 0, 3);
        grid.add(experienceField, 1, 3);
        grid.add(new Label("Disponibilité:"), 0, 4);
        grid.add(disponibiliteComboBox, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String nom = nomField.getText();
                    String prenom = prenomField.getText();
                    Fonction fonction = fonctionComboBox.getValue();
                    int experience = Integer.parseInt(experienceField.getText());
                    Disponibilite disponibilite = disponibiliteComboBox.getValue();
                    
                    if (nom.isEmpty() || prenom.isEmpty() || fonction == null) {
                        throw new IllegalArgumentException("Veuillez remplir tous les champs obligatoires.");
                    }
                    
                    return new Equipage(0, nom, prenom, fonction, experience, disponibilite);
                } catch (NumberFormatException e) {
                    showErrorAlert("Expérience invalide", "L'expérience doit être un nombre valide.");
                    return null;
                } catch (IllegalArgumentException e) {
                    showErrorAlert("Champs manquants", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Equipage> result = dialog.showAndWait();
        result.ifPresent(equipage -> {
            if (DAOEquipage.ajouter(equipage)) {
                equipageList.clear();
                lister();
                tv.getSelectionModel().select(equipage);
            } else {
                showErrorAlert("Erreur d'ajout", "Échec de l'ajout à la base de données.");
            }
        });
    }
    
    private void changerDisponibiliteEquipage() {
        Equipage selectedEquipage = tv.getSelectionModel().getSelectedItem();
        if (selectedEquipage != null) {
            Dialog<Disponibilite> dialog = new Dialog<>();
            dialog.setTitle("Changer la disponibilité");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 100, 10, 10));
            
            ComboBox<Disponibilite> disponibiliteCombo = new ComboBox<>();
            disponibiliteCombo.getItems().addAll(Disponibilite.values());
            disponibiliteCombo.setValue(selectedEquipage.getDisponibilite());
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
                if (DAOEquipage.changerDisponibilite(selectedEquipage, newDisponibilite)) {
                    selectedEquipage.setDisponibilite(newDisponibilite);
                    tv.refresh();
                    showDetails(selectedEquipage);
                }
            });
        }
    }
    
    private void supprimerEquipage() {
        Equipage selectedEquipage = tv.getSelectionModel().getSelectedItem();
        if (selectedEquipage != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation d'archivage");
            alert.setHeaderText("Archiver le membre d'équipage");
            alert.setContentText("Êtes-vous sûr de vouloir archiver ce membre d'équipage ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOEquipage.archiver(selectedEquipage)) {
                    equipageList.remove(selectedEquipage);
                    if (showingArchived) {
                        equipageList.add(selectedEquipage);
                    }
                }
            }
        }
    }
    
    private void modifierEquipage() {
        Equipage selectedEquipage = tv.getSelectionModel().getSelectedItem();
        if (selectedEquipage != null) {
            Dialog<Equipage> dialog = new Dialog<>();
            dialog.setTitle("Modifier un membre d'équipage");
            dialog.setHeaderText("Modifier les détails du membre d'équipage");
            
            ButtonType modifyButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            TextField nomField = new TextField(selectedEquipage.getNom());
            TextField prenomField = new TextField(selectedEquipage.getPrenom());
            
            ComboBox<Fonction> fonctionComboBox = new ComboBox<>();
            fonctionComboBox.getItems().addAll(Fonction.values());
            fonctionComboBox.setValue(selectedEquipage.getFonction());
            
            TextField experienceField = new TextField(String.valueOf(selectedEquipage.getExperience()));
            
            ComboBox<Disponibilite> disponibiliteComboBox = new ComboBox<>();
            disponibiliteComboBox.getItems().addAll(Disponibilite.values());
            disponibiliteComboBox.setValue(selectedEquipage.getDisponibilite());
            
            grid.add(new Label("Nom:"), 0, 0);
            grid.add(nomField, 1, 0);
            grid.add(new Label("Prénom:"), 0, 1);
            grid.add(prenomField, 1, 1);
            grid.add(new Label("Fonction:"), 0, 2);
            grid.add(fonctionComboBox, 1, 2);
            grid.add(new Label("Expérience:"), 0, 3);
            grid.add(experienceField, 1, 3);
            grid.add(new Label("Disponibilité:"), 0, 4);
            grid.add(disponibiliteComboBox, 1, 4);
            
            dialog.getDialogPane().setContent(grid);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == modifyButtonType) {
                    try {
                        String nom = nomField.getText();
                        String prenom = prenomField.getText();
                        Fonction fonction = fonctionComboBox.getValue();
                        int experience = Integer.parseInt(experienceField.getText());
                        Disponibilite disponibilite = disponibiliteComboBox.getValue();
                        
                        if (nom.isEmpty() || prenom.isEmpty() || fonction == null) {
                            throw new IllegalArgumentException("Veuillez remplir tous les champs obligatoires.");
                        }
                        
                        selectedEquipage.setNom(nom);
                        selectedEquipage.setPrenom(prenom);
                        selectedEquipage.setFonction(fonction);
                        selectedEquipage.setExperience(experience);
                        selectedEquipage.setDisponibilite(disponibilite);
                        
                        return selectedEquipage;
                    } catch (NumberFormatException e) {
                        showErrorAlert("Expérience invalide", "L'expérience doit être un nombre valide.");
                        return null;
                    } catch (IllegalArgumentException e) {
                        showErrorAlert("Champs manquants", e.getMessage());
                        return null;
                    }
                }
                return null;
            });
            
            Optional<Equipage> result = dialog.showAndWait();
            result.ifPresent(equipage -> {
                if (DAOEquipage.mettreAJour(equipage)) {
                    tv.refresh();
                    showDetails(equipage);
                } else {
                    showErrorAlert("Erreur de modification", "Échec de la mise à jour dans la base de données.");
                }
            });
        }
    }
    
    private void toggleArchiveView() {
        showingArchived = !showingArchived;
        if (showingArchived) {
            btnArchives.setText("Voir actifs");
            equipageList.clear();
            equipageList.addAll(DAOEquipage.listerArchives());
        } else {
            btnArchives.setText("Voir archives");
            equipageList.clear();
            equipageList.addAll(DAOEquipage.listerTous());
        }
    }
    
    private void restaurerEquipage() {
        Equipage selectedEquipage = tv.getSelectionModel().getSelectedItem();
        if (selectedEquipage != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de restauration");
            alert.setHeaderText("Restaurer le membre d'équipage");
            alert.setContentText("Êtes-vous sûr de vouloir restaurer ce membre d'équipage ?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (DAOEquipage.restaurer(selectedEquipage)) {
                    equipageList.remove(selectedEquipage);
                    if (!showingArchived) {
                        equipageList.add(selectedEquipage);
                    }
                }
            }
        }
    }
    
    private void searchEquipages() {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            equipageList.clear();
            if (showingArchived) {
                equipageList.addAll(DAOEquipage.chercherArchives(searchText));
            } else {
                equipageList.addAll(DAOEquipage.chercher(searchText));
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