package com.abdo.springbatchcustomer.config.Readers;

import com.abdo.springbatchcustomer.entity.Revenue;
import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PoijiReader {
    public void readExcelWithPoiji(String filePath) {
        try {
            // Charger le fichier Excel
            File file = new File(filePath);

            // Configuration de Poiji avec des options personnalisées
            PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
                    .settings(1) // Sauter une ligne après l'entête
                    .sheetName("Revenue") // Utiliser le nom de l'onglet "Revenue"
                    .headerCount(1) // Une seule ligne d'entêtes
                    .build();

            // Lire les données du fichier Excel
            List<Revenue> revenues = Poiji.fromExcel(file, Revenue.class, options);

            // Afficher les données lues
            for (Revenue revenue : revenues) {
                System.out.println(revenue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
