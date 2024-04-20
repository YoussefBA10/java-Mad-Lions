package Recyclage.services;

import Recyclage.entities.EcoDepot;
import Recyclage.interfaces.EcodepotService;
import Recyclage.tools.MyConnection;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EcoDepotMethodes implements EcodepotService<EcoDepot> {
    private boolean estEcoDepotValide(EcoDepot ecoDepot) {
        if (ecoDepot.getNom() == null || ecoDepot.getNom().isEmpty()) {
            System.out.println("Le champ nom est obligatoire.");
            return false;
        }

        if (!ecoDepot.getNom().matches("[a-zA-Z]+")) {
            System.out.println("Le champ nom doit contenir uniquement des lettres.");
            return false;
        }

        if (ecoDepot.getAdresse() == null || ecoDepot.getAdresse().isEmpty()) {
            System.out.println("Le champ adresse est obligatoire.");
            return false;
        }
        if(ecoDepot.getType().isEmpty()) {
            System.out.println("Le champ Type est obligatoire.");
            return false;
        }

        if(ecoDepot.getStatut_point_collecte().isEmpty()) {
            System.out.println("Le champ Statut_point_collecte est obligatoire.");
            return false;
        }

        if (!ecoDepot.getAdresse().matches("[a-zA-Z0-9 ]+")) {
            System.out.println("Le champ adresse doit contenir uniquement des lettres, des chiffres et des espaces.");
            return false;
        }

        if (ecoDepot.getCapacite_stockage() <= 0) {
            System.out.println("Le champ capacite_stockage doit être un nombre positif.");
            return false;
        }

        return true;
    }

    @Override
    public void ajouterEcodepot(EcoDepot ecoDepot) {
        if (estEcoDepotValide(ecoDepot)) {
            String requete = "INSERT INTO ECO_DEPOT (nom , adresse ,type ,capacite_stockage, statut_point_collecte)" +
                    "VALUES (? , ? ,? ,? ,?)";
            try {
                PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
                pst.setString(1, ecoDepot.getNom());
                pst.setString(2, ecoDepot.getAdresse());
                pst.setString(3, ecoDepot.getType());
                pst.setInt(4, ecoDepot.getCapacite_stockage());
                pst.setString(5, ecoDepot.getStatut_point_collecte());

                pst.executeUpdate();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Success");
                alert.setContentText("EcodepotAjouter");
                alert.showAndWait();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("erreur");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
        else {
            System.out.println("La saisie de l'éco-dépôt est invalide. Veuillez vérifier les champs et réessayer.");
        }
    }

    @Override
    public void supprimerEcodepot(EcoDepot ecoDepot) {
        Connection connection = null; // Déclarer la variable connection à l'extérieur du bloc try
        try {
            connection = MyConnection.getInsatance().getCnx();
            connection.setAutoCommit(false); // Désactiver l'autocommit

            // Supprimer d'abord les produits recyclables associés à l'éco-dépôt
            String deleteProduitsQuery = "DELETE FROM PRODUIT_RECYCLABLE WHERE ECO_DEPOT_ID = ?";
            PreparedStatement pstProduits = connection.prepareStatement(deleteProduitsQuery);
            pstProduits.setInt(1, ecoDepot.getId());
            int rowsDeletedProduits = pstProduits.executeUpdate();

            // Ensuite, supprimer l'éco-dépôt lui-même
            String deleteEcoDepotQuery = "DELETE FROM ECO_DEPOT WHERE id = ?";
            PreparedStatement pstEcoDepot = connection.prepareStatement(deleteEcoDepotQuery);
            pstEcoDepot.setInt(1, ecoDepot.getId());
            int rowsDeletedEcoDepot = pstEcoDepot.executeUpdate();

            // Valider la transaction si les deux suppressions réussissent
            connection.commit();

            if (rowsDeletedEcoDepot > 0) {
                System.out.println("Ecodepot supprimé avec succès");
            } else {
                System.out.println("Aucun ecodepot trouvé avec cet ID");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'Ecodepot: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback(); // Annuler la transaction en cas d'erreur
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors de l'annulation de la transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Rétablir l'autocommit par défaut
                }
            } catch (SQLException ex) {
                System.out.println("Erreur lors du rétablissement de l'autocommit: " + ex.getMessage());
            }
        }
    }

    @Override
    public void modifierEcodepot(EcoDepot ecoDepot, int id) {
        String requete = "UPDATE ECO_DEPOT SET nom=?, adresse=?, type=?, capacite_stockage=?, statut_point_collecte=? WHERE id=?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, ecoDepot.getNom());
            pst.setString(2, ecoDepot.getAdresse());
            pst.setString(3, ecoDepot.getType());
            pst.setInt(4, ecoDepot.getCapacite_stockage());
            pst.setString(5, ecoDepot.getStatut_point_collecte());
            pst.setInt(6, id); // Utiliser l'ID fourni pour identifier l'éco-dépôt à modifier

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("EcoDepot modifié avec succès");
            } else {
                System.out.println("Aucun éco-dépôt trouvé avec cet ID, aucune modification n'a été effectuée");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'éco-dépôt : " + e.getMessage());
        }
    }


    @Override
    public List<EcoDepot> listeDesEcodepots() {
        List<EcoDepot> listeEcoDepots = new ArrayList<>();
        String requete = "SELECT * FROM ECO_DEPOT";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String adresse = rs.getString("adresse");
                String type = rs.getString("type");
                int capacite_stockage = rs.getInt("capacite_stockage");
                String statut_point_collecte = rs.getString("statut_point_collecte");


                EcoDepot ecoDepot = new EcoDepot(nom, adresse, type, capacite_stockage, statut_point_collecte);
                ecoDepot.setId(id);

                listeEcoDepots.add(ecoDepot);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la liste des éco-dépôts : " + e.getMessage());
        }

        return listeEcoDepots;
    }
    public void chargerNomsEcoDepots(ComboBox<String> comboBox) {
        String requete = "SELECT nom FROM eco_depot";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                comboBox.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du chargement des noms des éco-dépôts : " + e.getMessage());
            alert.showAndWait();
        }
    }
    public long getIdEcoDepotByNom(String nom) {
        String requete = "SELECT id FROM eco_depot WHERE nom = ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, nom);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'ID de l'éco-dépôt par nom : " + e.getMessage());
        }
        return -1; // Retourne -1 si aucun éco-dépôt correspondant n'a été trouvé
    }

}
