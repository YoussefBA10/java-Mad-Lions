package Recyclage.tests;

import Recyclage.entities.EcoDepot;
import Recyclage.entities.ProduitRecyclable;
import Recyclage.entities.User;
import Recyclage.interfaces.EcodepotService;
import Recyclage.services.EcoDepotMethodes;

public class Main {
    public static void main(String[] args) {
        EcoDepot p=new EcoDepot("test","test","test",10,"test");
        EcoDepot pMOD=new EcoDepot("adam","adam","adam",2,"adam");
        User u=new User("test","test","test","test");
        EcoDepotMethodes p1=new EcoDepotMethodes();
       // ProduitRecyclable pp=new ProduitRecyclable("aa","aaa",55,"1/2/2002",'1/2/2002',p,u);
         //p.setId(5);
         //p1.ajouterEcodepot(p);
        // p1.supprimerEcodepot(p);
       // p1.modifierEcodepot(pMOD,7);
        System.out.println(p1.listeDesEcodepots());
    }
}
