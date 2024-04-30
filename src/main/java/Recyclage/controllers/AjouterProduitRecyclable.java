package Recyclage.controllers;

import Recyclage.entities.EcoDepot;
import Recyclage.entities.ProduitRecyclable;
import Recyclage.services.EcoDepotMethodes;
import Recyclage.services.ProduitRecyclableMethodes;
import Recyclage.tests.HelloApplication;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class AjouterProduitRecyclable implements Initializable {

    @FXML
    private ComboBox<String> ComboBoxTF1P;

    @FXML
    private ComboBox<String> ComboBoxTFP;

    @FXML
    private TextField DiscriptionTFP;

    @FXML
    private DatePicker dateP;

    @FXML
    private TextField nomTFP;

    @FXML
    private TextField quantiteTFP;
    private EcoDepotMethodes ecoDepotMethodes;

    @FXML
    void AfficherLesProduitRecyclable(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ProduitRecyclable/AfficherProduitRecyclable.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();

            // Créer une transition de fondu pour la nouvelle scène
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), root);
            fadeTransition.setFromValue(0.0); // Définir la transparence initiale à 0
            fadeTransition.setToValue(1.0); // Définir la transparence finale à 1

            // Démarrer la transition de fondu
            fadeTransition.play();

            // Afficher la nouvelle scène dans une nouvelle fenêtre
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle après la transition
            ((Stage) nomTFP.getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjouterProduitRecyclable(ActionEvent event) {
        if (nomTFP.getText().isEmpty() || DiscriptionTFP.getText().isEmpty() || ComboBoxTFP.getValue() == null ||
                dateP.getValue() == null || ComboBoxTF1P.getValue() == null || quantiteTFP.getText().isEmpty()) {
            afficherAlerteErreur("Tous les champs doivent être remplis");
            return;
        }
        if (!nomTFP.getText().matches("[a-zA-ZÀ-ÿ\\s]+")) {
            afficherAlerteErreur("Le nom ne doit contenir que des lettres et des espaces");
            return;
        }
        // Vérifier si la capacité de stockage est un entier valide
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteTFP.getText());
            if (quantite <= 0) {
                afficherAlerteErreur("La quantité doit être un nombre entier positif");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerteErreur("La quantité doit être un nombre entier valide");
            return;
        }
        // Récupérer la capacité de stockage de l'éco-dépôt sélectionné
        int capaciteStockage = ecoDepotMethodes.getCapaciteStockageByNom(ComboBoxTF1P.getValue());

        // Calculer la capacité restante
        int capaciteRestante = capaciteStockage - quantite;

        // Vérifier la capacité restante
        if (capaciteRestante >= 0) {
        ProduitRecyclableMethodes produitRecyclableMethodes = new ProduitRecyclableMethodes();
        ProduitRecyclable produitRecyclable = new ProduitRecyclable();
        produitRecyclable.setNom(nomTFP.getText());
        produitRecyclable.setDescription(DiscriptionTFP.getText());
        produitRecyclable.setType(ComboBoxTFP.getValue());
        produitRecyclable.setDateDepot(java.sql.Date.valueOf(dateP.getValue()));
        produitRecyclable.setQuantite(Integer.parseInt(quantiteTFP.getText()));
        String selectedEcoDepotNom = ComboBoxTF1P.getValue();
        // Obtenir l'ID de l'éco-dépôt correspondant au nom sélectionné
            EcoDepot selectedEcoDepot = ecoDepotMethodes.getEcoDepotByNom(selectedEcoDepotNom);

            if (selectedEcoDepot != null) {
                // Associer l'éco-dépôt récupéré à l'objet ProduitRecyclable
                produitRecyclable.setEcoDepot(selectedEcoDepot);
                System.out.println(produitRecyclable.getEcoDepot());
            } else {
                // Gérer le cas où aucun éco-dépôt correspondant n'a été trouvé
                System.out.println("Aucun éco-dépôt correspondant trouvé pour le nom : " + selectedEcoDepotNom);
            }

        ProduitRecyclable existingProduct = produitRecyclableMethodes.getProduitRecyclableByAttributes(produitRecyclable);
        if (existingProduct != null) {
                afficherAlerteErreur("Ce produit existe déjà !");
                return;

        }

        // Vérifier si un produit avec les mêmes données mais une quantité différente existe déjà
        ProduitRecyclable existingProductWithDifferentQuantity = produitRecyclableMethodes.getProduitRecyclableByAttributesButDifferentQuantity(produitRecyclable);
        if (existingProductWithDifferentQuantity != null) {
            if (confirmerAjoutProduit()) {
                // Mettre à jour la quantité du produit existant
                afficherAlerteErreur("Ce produit existe déjà et la quantite a eter modifier !");
                existingProductWithDifferentQuantity.setQuantite(existingProductWithDifferentQuantity.getQuantite() + quantite);
                produitRecyclableMethodes.updateProduit(existingProductWithDifferentQuantity);
                afficherPageProduitsRecyclables();
                return;

            }
        }
            if (confirmerAjoutProduit()) {
                produitRecyclableMethodes.ajouterProduit(produitRecyclable);
                ecoDepotMethodes.updateCapaciteStockage(ComboBoxTF1P.getValue(), capaciteRestante);
                afficherPageProduitsRecyclables();
            }
        }
        else {
            if (capaciteStockage > 0 && capaciteStockage < quantite) {
                afficherAlerteErreur("La capacité de stockage est presque atteinte pour cet éco-dépôt. Il ne reste que " + capaciteStockage + " places disponibles.");
                return;
            } else if (capaciteStockage == 0) {
                afficherAlerteErreur("La capacité de stockage de cet éco-dépôt est saturée. Veuillez choisir un autre éco-dépôt.");
                return;
            }
        }
        }




    // Méthode pour afficher une alerte d'erreur
    private void afficherAlerteErreur2(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] items = {"verre", "Metal", "Plastique", "Matiere Organique"};
        ComboBoxTFP.getItems().addAll(items);
        ecoDepotMethodes = new EcoDepotMethodes();
        ecoDepotMethodes.chargerNomsEcoDepots(ComboBoxTF1P);
        dateP.setValue(LocalDate.now());
    }

    private void afficherAlerteErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);

        // Charger l'image à partir du chemin spécifié
        Image image = new Image("/css/Images/alerte2.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Ajuster la largeur de l'image si nécessaire
        imageView.setPreserveRatio(true);
        alert.setGraphic(imageView);

        // Appliquer un style CSS personnalisé à l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/CSS/alerte.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    @FXML
    void réinitialiser(ActionEvent event) {
// Réinitialisation des champs texte
        DiscriptionTFP.setText("");
        nomTFP.setText("");
        quantiteTFP.setText("");
        // Réinitialisation du datepicker
        dateP.setValue(null);

    }

    private void afficherPageProduitsRecyclables() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ProduitRecyclable/AfficherProduitRecyclable.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();

            // Créer une transition de fondu pour la nouvelle scène
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), root);
            fadeTransition.setFromValue(0.0); // Définir la transparence initiale à 0
            fadeTransition.setToValue(1.0); // Définir la transparence finale à 1

            // Démarrer la transition de fondu
            fadeTransition.play();

            // Afficher la nouvelle scène dans une nouvelle fenêtre
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle après la transition
            ((Stage) nomTFP.getScene().getWindow()).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean confirmerAjoutProduit() {
        // Créer une nouvelle alerte de type CONFIRMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'ajout");
        alert.setHeaderText("Confirmer l'ajout du produit recyclable");
        alert.setContentText("Êtes-vous sûr de vouloir ajouter ce produit recyclable ?");

        // Obtenir le bouton de type OK
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        // Appliquer un style CSS personnalisé au bouton OK
        okButton.getStyleClass().add("custom-ok-button");

        // Obtenir le bouton de type Annuler
        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        // Appliquer un style CSS personnalisé au bouton Annuler
        cancelButton.getStyleClass().add("custom-cancel-button");

        // Charger la feuille de style CSS pour l'alerte
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/CSS/confirmation.css").toExternalForm());

        // Afficher l'alerte et attendre la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}


