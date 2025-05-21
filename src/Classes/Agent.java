/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author ghofrane
 */
public class Agent {
    private final StringProperty id;
    private final IntegerProperty nomprenom; 
    private final StringProperty motpassehache;

    public Agent(StringProperty id, IntegerProperty nomprenom, StringProperty motpaseehache) {
        this.id = id;
        this.nomprenom = nomprenom;
        this.motpassehache = motpaseehache;
    }
    
}
