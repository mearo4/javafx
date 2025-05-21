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

public class Pilote {
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
    private final StringProperty licence;
    private final IntegerProperty heuresVol;
    private final ObjectProperty<Disponibilite> disponibilite;
    private final BooleanProperty archived;
    private List<Vol> vols = new ArrayList<>();
    
    public Pilote(int id, String nom, String prenom, String licence, int heuresVol, Disponibilite disponibilite) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.licence = new SimpleStringProperty(licence);
        this.heuresVol = new SimpleIntegerProperty(heuresVol);
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
    
    public String getLicence() {
        return licence.get();
    }
    
    public int getHeuresVol() {
        return heuresVol.get();
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
    
    public StringProperty licenceProperty() {
        return licence;
    }
    
    public IntegerProperty heuresVolProperty() {
        return heuresVol;
    }
    
    public ObjectProperty<Disponibilite> disponibiliteProperty() {
        return disponibilite;
    }
    
    public BooleanProperty archivedProperty() {
        return archived;
    }
    
    // Setters
    public void setId(int value) {
        id.set(value);
    }
    
    public void setNom(String value) {
        nom.set(value);
    }
    
    public void setPrenom(String value) {
        prenom.set(value);
    }
    
    public void setLicence(String value) {
        licence.set(value);
    }
    
    public void setHeuresVol(int value) {
        heuresVol.set(value);
    }
    
    public void setDisponibilite(Disponibilite value) {
        disponibilite.set(value);
    }
    
    public void setArchived(boolean value) {
        archived.set(value);
    }
    
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