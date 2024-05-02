/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
/*import sportify.edu.entities.Reservation;
import sportify.edu.entities.Terrain;
import sportify.edu.services.TerrainService;*/

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author WALID
 */
public class Fail_pageController implements Initializable {

    @FXML
    private Label paymen_txt;
    @FXML
    private Button back_btn;

   // private Reservation reservation;
    //private Terrain terrain;

    /*public void setData(Reservation r) {
        String value;
        this.reservation = r;
        TerrainService ts = new TerrainService();
        terrain = ts.diplay(this.reservation.getTerrain_id());
        if (terrain != null) {
            value = "There's an Error in your online payment for your Reservation of the Staduim : " + terrain.getName() + ",";
            paymen_txt.setText(value);
        }
    }*/

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*this.reservation = new Reservation();
        this.terrain = new Terrain();*/
    }

    @FXML
    private void redirectToListReservation(ActionEvent event) {
           try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../gui/reservation/Reservation_view_client.fxml"));
            Parent root = loader.load();
            //UPDATE The Controller with Data :
            Scene scene = new Scene(root);
            Stage stage = (Stage) back_btn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
