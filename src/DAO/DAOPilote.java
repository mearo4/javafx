package DAO;

import Classes.Pilote;
import Classes.Pilote.Disponibilite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DAOPilote {
    
    public static boolean ajouter(Pilote pilote) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "INSERT INTO pilote (nom, prenom, licence, heures_vol, disponibilite, archived) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = cn.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pilote.getNom());
            ps.setString(2, pilote.getPrenom());
            ps.setString(3, pilote.getLicence());
            ps.setInt(4, pilote.getHeuresVol());
            ps.setString(5, pilote.getDisponibilite().name());
            ps.setBoolean(6, pilote.isArchived());
            
            int n = ps.executeUpdate();
            if (n >= 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pilote.setId(rs.getInt(1));
                    System.out.println("Ajout du pilote réussi");
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static ArrayList<Pilote> chercher(String nom) {
        ArrayList<Pilote> pilotes = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM pilote WHERE (nom LIKE ? OR prenom LIKE ?) AND archived = false";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + nom + "%");
            pst.setString(2, "%" + nom + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nomPilote = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String licence = rs.getString("licence");
                int heuresVol = rs.getInt("heures_vol");
                Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
                
                Pilote p = new Pilote(id, nomPilote, prenom, licence, heuresVol, disponibilite);
                p.setArchived(rs.getBoolean("archived"));
                pilotes.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return pilotes;
    }
    
    public static ArrayList<Pilote> chercherArchives(String nom) {
        ArrayList<Pilote> pilotes = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM pilote WHERE (nom LIKE ? OR prenom LIKE ?) AND archived = true";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + nom + "%");
            pst.setString(2, "%" + nom + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nomPilote = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String licence = rs.getString("licence");
                int heuresVol = rs.getInt("heures_vol");
                Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
                
                Pilote p = new Pilote(id, nomPilote, prenom, licence, heuresVol, disponibilite);
                p.setArchived(true);
                pilotes.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return pilotes;
    }
    
    public static boolean archiver(Pilote p) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE pilote SET archived = true WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, p.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                p.setArchived(true);
                System.out.println("Archivage du pilote réussi");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de requête d'archivage: " + ex.getMessage());
        }
        return false;
    }
    
    public static boolean restaurer(Pilote p) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE pilote SET archived = false WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, p.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                p.setArchived(false);
                System.out.println("Restauration du pilote réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de requête de restauration: " + ex.getMessage());
        }
        return false;
    }
    
    public static boolean changerDisponibilite(Pilote p, Disponibilite disponibilite) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE pilote SET disponibilite=? WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, disponibilite.name());
            pst.setInt(2, p.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Modification de disponibilité réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de requête de modification: " + ex.getMessage());
        }
        return false;
    }
    
    public static boolean mettreAJour(Pilote p) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE pilote SET nom=?, prenom=?, licence=?, heures_vol=?, disponibilite=?, archived=? WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, p.getNom());
            pst.setString(2, p.getPrenom());
            pst.setString(3, p.getLicence());
            pst.setInt(4, p.getHeuresVol());
            pst.setString(5, p.getDisponibilite().name());
            pst.setBoolean(6, p.isArchived());
            pst.setInt(7, p.getId());
            
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Mise à jour du pilote réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de requête de mise à jour: " + ex.getMessage());
        }
        return false;
    }
    
    public static ObservableList<Pilote> listerTous() {
        ObservableList<Pilote> pilotes = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM pilote WHERE archived = false");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String licence = rs.getString("licence");
                int heuresVol = rs.getInt("heures_vol");
                Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
                
                Pilote p = new Pilote(id, nom, prenom, licence, heuresVol, disponibilite);
                p.setArchived(rs.getBoolean("archived"));
                pilotes.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilotes;
    }
    
    public static ObservableList<Pilote> listerArchives() {
        ObservableList<Pilote> pilotes = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM pilote WHERE archived = true");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String licence = rs.getString("licence");
                int heuresVol = rs.getInt("heures_vol");
                Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
                
                Pilote p = new Pilote(id, nom, prenom, licence, heuresVol, disponibilite);
                p.setArchived(true);
                pilotes.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pilotes;
    }
    
    public static Pilote trouverParId(int id) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM pilote WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String licence = rs.getString("licence");
                int heuresVol = rs.getInt("heures_vol");
                Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
                
                Pilote p = new Pilote(id, nom, prenom, licence, heuresVol, disponibilite);
                p.setArchived(rs.getBoolean("archived"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par ID: " + e.getMessage());
        }
        return null;
    }
}