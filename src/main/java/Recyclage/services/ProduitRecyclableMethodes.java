package Recyclage.services;

import Recyclage.entities.ProduitRecyclable;
import Recyclage.interfaces.ProduitRecyclableService;
import Recyclage.tools.MyConnection;
import javafx.scene.control.Alert;
import Recyclage.entities.EcoDepot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitRecyclableMethodes implements ProduitRecyclableService<ProduitRecyclable> {
    @Override
    public void ajouterProduit(ProduitRecyclable produitRecyclable) {
        if (produitRecyclable.getEcoDepot() == null) {
            // Afficher un message d'erreur si l'éco-dépôt n'est pas défini
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("L'éco-dépôt n'est pas défini pour le produit recyclable.");
            alert.showAndWait();
            return;
        }

        String requete = "INSERT INTO produit_recyclable (nom, description, quantite, date_depot, eco_depot_id, type)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, produitRecyclable.getNom());
            pst.setString(2, produitRecyclable.getDescription());
            pst.setInt(3, produitRecyclable.getQuantite());
            pst.setDate(4, new java.sql.Date(produitRecyclable.getDateDepot().getTime())); // Convertir java.util.Date en java.sql.Date
            pst.setLong(5, produitRecyclable.getEcodepot_id());
            pst.setString(6, produitRecyclable.getType().toString());

            pst.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Success");
            alert.setContentText("Produit recyclable ajouté");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


    @Override
    public boolean supprimerProduit(ProduitRecyclable produitRecyclable) {
        String requete = "DELETE FROM PRODUIT_RECYCLABLE WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setInt(1, produitRecyclable.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Produit recyclable supprimé");
                return true; // Retourne true si au moins une ligne a été supprimée
            } else {
                System.out.println("Aucun produit recyclable trouvé avec cet ID");
                return false; // Retourne false si aucune ligne n'a été supprimée
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du produit recyclable: " + e.getMessage());
            return false; // En cas d'erreur, retourne false
        }
    }


    @Override
    public boolean modifierProduit(ProduitRecyclable produitRecyclable, int id) {
        String requete = "UPDATE PRODUIT_RECYCLABLE SET nom = ?, description = ?, quantite = ?, date_depot = ?, type = ?, eco_depot_id = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, produitRecyclable.getNom());
            pst.setString(2, produitRecyclable.getDescription());
            pst.setInt(3, produitRecyclable.getQuantite());
            pst.setDate(4, new java.sql.Date(produitRecyclable.getDateDepot().getTime())); // Convertir java.util.Date en java.sql.Date
            pst.setString(5, produitRecyclable.getType().toString());
            pst.setLong(6, produitRecyclable.getEcodepot_id());
            pst.setInt(7, id);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Produit recyclable modifié avec succès");
                return true;
            } else {
                System.out.println("Aucun produit recyclable trouvé avec cet ID");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du produit recyclable: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ProduitRecyclable> listeDesProduits() {
        List<ProduitRecyclable> produits = new ArrayList<>();
        String requete = "SELECT * FROM PRODUIT_RECYCLABLE";
        try {
            Statement stmt = MyConnection.getInsatance().getCnx().createStatement();
            ResultSet rs = stmt.executeQuery(requete);
            while (rs.next()) {
                ProduitRecyclable produit = new ProduitRecyclable();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setQuantite(rs.getInt("quantite"));
                produit.setType(rs.getString("type"));
                produit.setDateDepot(rs.getDate("date_depot"));

                // Récupérer l'EcoDepot associé à partir de l'ID stocké dans le produit recyclable
                long ecodepotId = rs.getLong("eco_depot_id");
                EcoDepot ecoDepot = obtenirEcoDepotParId(ecodepotId); // Méthode à implémenter
                produit.setEcoDepot(ecoDepot);

                produits.add(produit);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des produits recyclables: " + e.getMessage());
        }
        return produits;
    }
    public EcoDepot obtenirEcoDepotParId(long ecodepotId) {
        EcoDepot ecoDepot = null;
        String requete = "SELECT * FROM ECO_DEPOT WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setLong(1, ecodepotId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ecoDepot = new EcoDepot();
                ecoDepot.setId(rs.getInt("id"));
                ecoDepot.setNom(rs.getString("nom"));
                // Définir les autres attributs de l'EcoDepot si nécessaire
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'EcoDepot par ID: " + e.getMessage());
        }
        return ecoDepot;
    }
    public ProduitRecyclable getProduitRecyclableByAttributes(ProduitRecyclable produitRecyclable) {
        String requete = "SELECT * FROM produit_recyclable WHERE nom = ? AND description = ? AND type = ? AND quantite = ?  ";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, produitRecyclable.getNom());
            pst.setString(2, produitRecyclable.getDescription());
            pst.setString(3, produitRecyclable.getType().toString());
            pst.setLong(4, produitRecyclable.getQuantite());



            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ProduitRecyclable existingProduct = new ProduitRecyclable();
                existingProduct.setId(rs.getInt("id"));
                // Autres attributs à récupérer si nécessaire
                return existingProduct;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
        }
        return null; // Retourner null si aucun produit correspondant n'est trouvé
    }
    public ProduitRecyclable getProduitRecyclableByAttributesButDifferentQuantity(ProduitRecyclable produitRecyclable) {
        String requete = "SELECT * FROM produit_recyclable WHERE nom = ? AND description = ? AND type = ? AND quantite != ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setString(1, produitRecyclable.getNom());
            pst.setString(2, produitRecyclable.getDescription());
            pst.setString(3, produitRecyclable.getType().toString());
            pst.setInt(4, produitRecyclable.getQuantite());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ProduitRecyclable existingProduct = new ProduitRecyclable();
                existingProduct.setId(rs.getInt("id"));
                // Autres attributs à récupérer si nécessaire
                return existingProduct;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
        }
        return null; // Retourner null si aucun produit correspondant n'est trouvé
    }
    public void updateProduit(ProduitRecyclable produitRecyclable) {
        String requete = "UPDATE produit_recyclable SET quantite = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInsatance().getCnx().prepareStatement(requete);
            pst.setInt(1, produitRecyclable.getQuantite());
            pst.setInt(2, produitRecyclable.getId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Quantité du produit recyclable mise à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour de la quantité du produit recyclable.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
        }
    }



}


