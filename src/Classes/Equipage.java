package Classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.ArrayList;
import java.util.List;

public class Equipage {
    public enum Fonction {
        chef_cabine,
        hotesse,
        steward,
        personnel_securite
    }
    
    public enum Disponibilite {
        disponible,
        en_vol,
        repos,
        conge,
        formation
    }
    
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final ObjectProperty<Fonction> fonction;
    private final IntegerProperty experience;
    private final ObjectProperty<Disponibilite> disponibilite;
    private final BooleanProperty archived;
    private List<Vol> vols = new ArrayList<>();
    
    public Equipage(int id, String nom, String prenom, Fonction fonction, int experience, Disponibilite disponibilite) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.fonction = new SimpleObjectProperty<>(fonction);
        this.experience = new SimpleIntegerProperty(experience);
        this.disponibilite = new SimpleObjectProperty<>(disponibilite);
        this.archived = new SimpleBooleanProperty(false);
    }
    
    // Getters
    public int getId() {
        return id.get();
    }
    
    public String getNom() {
        return nom.get();
    }
    
    public String getPrenom() {
        return prenom.get();
    }
    
    public Fonction getFonction() {
        return fonction.get();
    }
    
    public int getExperience() {
        return experience.get();
    }
    
    public Disponibilite getDisponibilite() {
        return disponibilite.get();
    }
    
    public boolean isArchived() {
        return archived.get();
    }
    
    public List<Vol> getVols() {
        return vols;
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public StringProperty nomProperty() {
        return nom;
    }
    
    public StringProperty prenomProperty() {
        return prenom;
    }
    
    public ObjectProperty<Fonction> fonctionProperty() {
        return fonction;
    }
    
    public IntegerProperty experienceProperty() {
        return experience;
    }
    
    public ObjectProperty<Disponibilite> disponibiliteProperty() {
        return disponibilite;
    }
    
    public BooleanProperty archivedProperty() {
        return archived;
    }
    
    public void setId(int value) {
        id.set(value);
    }
    
    public void setNom(String value) {
        nom.set(value);
    }
    
    public void setPrenom(String value) {
        prenom.set(value);
    }
    
    public void setFonction(Fonction value) {
        fonction.set(value);
    }
    
    public void setExperience(int value) {
        experience.set(value);
    }
    
    public void setDisponibilite(Disponibilite value) {
        disponibilite.set(value);
    }
    
    public void setArchived(boolean value) {
        archived.set(value);
    }
    
    // Méthodes métier
    public String getNomComplet() {
        return getPrenom() + " " + getNom();
    }
    
    public boolean estDisponible() {
        return getDisponibilite() == Disponibilite.disponible;
    }
    
    public void ajouterVol(Vol vol) {
        if (!vols.contains(vol)) {
            vols.add(vol);
        }
    }
    
    public void retirerVol(Vol vol) {
        vols.remove(vol);
    }
}