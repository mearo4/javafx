package DAO;

import Classes.Equipage;
import Classes.Equipage.Disponibilite;
import Classes.Equipage.Fonction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DAOEquipage {
    
    public static boolean ajouter(Equipage equipage) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "INSERT INTO equipage (nom, prenom, fonction, experience, disponibilite, archived) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement ps = cn.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, equipage.getNom());
            ps.setString(2, equipage.getPrenom());
            ps.setString(3, equipage.getFonction().name());
            ps.setInt(4, equipage.getExperience());
            ps.setString(5, equipage.getDisponibilite().name());
            ps.setBoolean(6, false);
            
            int n = ps.executeUpdate();
            if (n >= 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    equipage.setId(rs.getInt(1));
                    System.out.println("Ajout du membre d'équipage réussi");
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static ArrayList<Equipage> chercher(String nom) {
        ArrayList<Equipage> equipages = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM equipage WHERE (nom LIKE ? OR prenom LIKE ?) AND archived = false";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + nom + "%");
            pst.setString(2, "%" + nom + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                equipages.add(creerEquipageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return equipages;
    }

    public static ArrayList<Equipage> chercherArchives(String nom) {
        ArrayList<Equipage> equipages = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM equipage WHERE (nom LIKE ? OR prenom LIKE ?) AND archived = true";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + nom + "%");
            pst.setString(2, "%" + nom + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                equipages.add(creerEquipageFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return equipages;
    }
    
    public static boolean archiver(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE equipage SET archived = true WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, e.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                e.setArchived(true);
                System.out.println("Archivage du membre d'équipage réussi");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème lors de l'archivage: " + ex.getMessage());
        }
        return false;
    }

    public static boolean restaurer(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE equipage SET archived = false WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, e.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                e.setArchived(false);
                System.out.println("Restauration du membre d'équipage réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème lors de la restauration: " + ex.getMessage());
        }
        return false;
    }
    
    public static boolean changerDisponibilite(Equipage e, Disponibilite disponibilite) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE equipage SET disponibilite=? WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, disponibilite.name());
            pst.setInt(2, e.getId());
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
    
    public static boolean mettreAJour(Equipage e) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE equipage SET nom=?, prenom=?, fonction=?, experience=?, disponibilite=? WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, e.getNom());
            pst.setString(2, e.getPrenom());
            pst.setString(3, e.getFonction().name());
            pst.setInt(4, e.getExperience());
            pst.setString(5, e.getDisponibilite().name());
            pst.setInt(6, e.getId());
            
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Mise à jour du membre d'équipage réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème de requête de mise à jour: " + ex.getMessage());
        }
        return false;
    }
    
    public static ObservableList<Equipage> listerTous() {
        ObservableList<Equipage> equipages = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM equipage WHERE archived = false");
            while (rs.next()) {
                equipages.add(creerEquipageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipages;
    }

    public static ObservableList<Equipage> listerArchives() {
        ObservableList<Equipage> equipages = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM equipage WHERE archived = true");
            while (rs.next()) {
                equipages.add(creerEquipageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipages;
    }
    
    public static Equipage trouverParId(int id) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM equipage WHERE id=?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return creerEquipageFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par ID: " + e.getMessage());
        }
        return null;
    }

    private static Equipage creerEquipageFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        Fonction fonction = Fonction.valueOf(rs.getString("fonction"));
        int experience = rs.getInt("experience");
        Disponibilite disponibilite = Disponibilite.valueOf(rs.getString("disponibilite"));
        
        Equipage e = new Equipage(id, nom, prenom, fonction, experience, disponibilite);
        e.setArchived(rs.getBoolean("archived"));
        return e;
    }
}