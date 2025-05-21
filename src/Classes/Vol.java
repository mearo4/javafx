package Classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Vol {

    public enum Trajet {
        court,
        moyen,
        Long
    }

    private final StringProperty numVol;
    private final IntegerProperty dateDep;
    private final IntegerProperty dateArr;
    private final StringProperty depart;
    private final StringProperty destination;
    private final ObjectProperty<Vol.Trajet> trajet;
    private final StringProperty avionId;
    private final StringProperty piloteId;
    private final BooleanProperty archived;

    public Vol(String numVol, int dateDep, int dateArr, String depart, String destination, Vol.Trajet trajet, String avionId, String piloteId) {
        this.numVol = new SimpleStringProperty(numVol);
        this.dateDep = new SimpleIntegerProperty(dateDep);
        this.dateArr = new SimpleIntegerProperty(dateArr);
        this.depart = new SimpleStringProperty(depart);
        this.destination = new SimpleStringProperty(destination);
        this.trajet = new SimpleObjectProperty<>(trajet);
        this.avionId = new SimpleStringProperty(avionId);
        this.piloteId = new SimpleStringProperty(piloteId);
        this.archived = new SimpleBooleanProperty(false);
    }

    public String getNumVol() {
        return numVol.get();
    }

    public int getDateDep() {
        return dateDep.get();
    }

    public int getDateArr() {
        return dateArr.get();
    }

    public String getDepart() {
        return depart.get();
    }

    public String getDestination() {
        return destination.get();
    }

    public Vol.Trajet getTrajet() {
        return trajet.get();
    }

    public String getAvionId() {
        return avionId.get();
    }

    public String getPiloteId() {
        return piloteId.get();
    }
    
    public boolean isArchived() {
        return archived.get();
    }

    public StringProperty numVolProperty() {
        return numVol;
    }

    public IntegerProperty dateDepProperty() {
        return dateDep;
    }

    public IntegerProperty dateArrProperty() {
        return dateArr;
    }

    public StringProperty departProperty() {
        return depart;
    }

    public StringProperty destinationProperty() {
        return destination;
    }

    public ObjectProperty<Vol.Trajet> trajetProperty() {
        return trajet;
    }

    public StringProperty avionIdProperty() {
        return avionId;
    }

    public StringProperty piloteIdProperty() {
        return piloteId;
    }
    
    public BooleanProperty archivedProperty() {
        return archived;
    }
    
    public void setArchived(boolean value) {
        archived.set(value);
    }
}

    
