package DAO;

import Classes.Avion;
import Classes.Avion.Tetat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author ghofrane
 */
public class DAOAvion {
  
    

    public static boolean ajouter(Avion avion) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "INSERT INTO avion (modele, capacite, etat, archived) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = cn.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, avion.getModele());
            ps.setInt(2, avion.getCapacite());
            ps.setString(3, avion.getEtat().name());
            ps.setBoolean(4, false);
            
            int n = ps.executeUpdate();
            if (n >= 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    avion.setId(rs.getInt(1));
                    System.out.println("Ajout d'avion réussi");
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Avion> chercher(String modele) {
        ArrayList<Avion> avions = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM avion WHERE modele LIKE ? AND archived = false";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + modele + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                avions.add(creerAvionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return avions;
    }

    public static ArrayList<Avion> chercherArchives(String modele) {
        ArrayList<Avion> avions = new ArrayList<>();
        Connection cn = LaConnexion.seConnecter();
        String requete = "SELECT * FROM avion WHERE modele LIKE ? AND archived = true";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, "%" + modele + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                avions.add(creerAvionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return avions;
    }

    public static boolean archiver(Avion a) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE avion SET archived = true WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, a.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                a.setArchived(true);
                System.out.println("Archivage d'avion réussi");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème lors de l'archivage : " + ex.getMessage());
        }
        return false;
    }

    public static boolean restaurer(Avion a) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE avion SET archived = false WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setInt(1, a.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                a.setArchived(false);
                System.out.println("Restauration d'avion réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème lors de la restauration : " + ex.getMessage());
        }
        return false;
    }

    public static boolean changerEtat(Avion a, Tetat etat) {
        Connection cn = LaConnexion.seConnecter();
        String requete = "UPDATE avion SET etat = ? WHERE id = ?";
        try {
            PreparedStatement pst = cn.prepareStatement(requete);
            pst.setString(1, etat.name());
            pst.setInt(2, a.getId());
            int n = pst.executeUpdate();
            if (n >= 1) {
                System.out.println("Modification d'état réussie");
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Problème lors de la modification d'état : " + ex.getMessage());
        }
        return false;
    }

    public static ObservableList<Avion> listerTous() {
        ObservableList<Avion> avions = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM avion WHERE archived = false");
            while (rs.next()) {
                avions.add(creerAvionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }

    public static ObservableList<Avion> listerArchives() {
        ObservableList<Avion> avions = FXCollections.observableArrayList();
        Connection cn = LaConnexion.seConnecter();
        try {
            ResultSet rs = cn.createStatement().executeQuery("SELECT * FROM avion WHERE archived = true");
            while (rs.next()) {
                avions.add(creerAvionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avions;
    }

    private static Avion creerAvionFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String modele = rs.getString("modele");
        int capacite = rs.getInt("capacite");
        Tetat etat = Tetat.valueOf(rs.getString("etat"));
        
        Avion a = new Avion(id, modele, capacite, etat);
        a.setArchived(rs.getBoolean("archived"));
        return a;
    }
}

