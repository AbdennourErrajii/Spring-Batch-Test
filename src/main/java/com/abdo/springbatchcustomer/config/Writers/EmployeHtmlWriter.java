package com.abdo.springbatchcustomer.config.Writers;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class EmployeHtmlWriter implements ItemWriter<Employe> {

    @Override
    public void write(Chunk<? extends Employe> chunk) throws Exception {
        List<? extends Employe> employes = chunk.getItems();
        // Définir le chemin du fichier HTML
        String filePath = "src/main/resources/templates/page.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Début du fichier HTML avec Bootstrap
            writer.write("<!DOCTYPE html>");
            writer.write("<html lang='fr'><head>");
            writer.write("<meta charset='UTF-8'>");
            writer.write("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            writer.write("<title>Liste des Employés</title>");
            writer.write("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            writer.write("</head><body class='bg-light'>");

            writer.write("<div class='container mt-5'>");
            writer.write("<h1 class='text-center text-primary mb-4'>Liste des Employés</h1>");
            writer.write("<table class='table table-bordered table-striped shadow-sm'>");
            writer.write("<thead class='table-dark'><tr>");
            writer.write("<th>ID</th><th>Nom</th><th>Email</th><th>Téléphone</th><th>Salaire</th>");
            writer.write("</tr></thead><tbody>");

            // Ajouter chaque employé dans la table
            for (Employe e : employes) {
                writer.write("<tr>");
                writer.write("<td>" + e.getId() + "</td>");
                writer.write("<td>" + e.getName() + "</td>");
                writer.write("<td>" + e.getEmail() + "</td>");
                writer.write("<td>" + e.getPhone() + "</td>");
                writer.write("<td>" + e.getSalary() + " €</td>");
                writer.write("</tr>");
            }

            writer.write("</tbody></table>");
            writer.write("</div>");
            writer.write("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            writer.write("</body></html>");
        }
        System.out.println("✅ Fichier page.html stylisé avec succès !");
    }
}
