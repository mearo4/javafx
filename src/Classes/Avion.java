/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Avion {

    public enum Tetat {
        disponible,
        maintenance,
        en_vol
    }
    private final IntegerProperty id;
    private final StringProperty modele;
    private final IntegerProperty capacite;
    private final ObjectProperty<Tetat> etat;
    private final BooleanProperty archived;
    private List<Vol> vols = new ArrayList<>();
    public static final Map<String, Integer> MODELES_CAPACITES = new HashMap<>();

    static {
        MODELES_CAPACITES.put("Airbus A320", 180);
        MODELES_CAPACITES.put("Airbus A321", 220);
        MODELES_CAPACITES.put("Airbus A330-300", 300);
        MODELES_CAPACITES.put("Airbus A350-900", 325);
        MODELES_CAPACITES.put("Airbus A380", 555);
        MODELES_CAPACITES.put("Boeing 737-800", 189);
        MODELES_CAPACITES.put("Boeing 737 MAX 8", 200);
        MODELES_CAPACITES.put("Boeing 747-400", 416);
        MODELES_CAPACITES.put("Boeing 767-300ER", 260);
        MODELES_CAPACITES.put("Boeing 777-300ER", 396);
        MODELES_CAPACITES.put("Boeing 787-9 Dreamliner", 280);
        MODELES_CAPACITES.put("Embraer E190", 106);
        MODELES_CAPACITES.put("Bombardier CRJ900", 88);
        MODELES_CAPACITES.put("ATR 72", 74);
        MODELES_CAPACITES.put("ATR 42", 48);
    }

    public Avion(int id, String modele, int capacite, Tetat etat) {
        this.id = new SimpleIntegerProperty(id);
        this.modele = new SimpleStringProperty(modele);
        this.capacite = new SimpleIntegerProperty(capacite);
        this.etat = new SimpleObjectProperty<>(etat);
        this.archived = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return this.id.get();
    }

    public List<Vol> getVols() {
        return vols;
    }

    public String getModele() {
        return modele.get();
    }

    public int getCapacite() {
        return capacite.get();
    }

    public Tetat getEtat() {
        return etat.get();
    }
    
    public boolean isArchived() {
        return archived.get();
    }
    
    public StringProperty modeleProperty() {
        return modele;
    }

    public IntegerProperty capaciteProperty() {
        return capacite;
    }

    public ObjectProperty<Tetat> etatProperty() {
        return etat;
    }
    
    public BooleanProperty archivedProperty() {
        return archived;
    }

    public void setId(int value) {
        id.set(value);
    }

    public void setModele(String value) {
        modele.set(value);
    }

    public void setCapacite(int value) {
        capacite.set(value);
    }

    public void setEtat(Tetat value) {
        etat.set(value);
    }
    
    public void setArchived(boolean value) {
        archived.set(value);
    }
}
