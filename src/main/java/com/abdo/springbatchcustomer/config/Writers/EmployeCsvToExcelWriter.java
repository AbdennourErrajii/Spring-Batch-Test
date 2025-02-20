package com.abdo.springbatchcustomer.config.Writers;

import com.abdo.springbatchcustomer.entity.Employe;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import java.io.*;

public class EmployeCsvToExcelWriter implements ItemWriter<Employe> {
    private static final String FILE_PATH = "src/main/resources/outputs/employe.xlsx";
    @Override
    public void write(Chunk<? extends Employe> employes) throws Exception {
        File file = new File(FILE_PATH);
        boolean fileExists = file.exists();
        Workbook workbook;
        Sheet sheet;
        if (fileExists) {
            // Ouvrir le fichier existant
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
            }
        } else {
            // Créer un nouveau fichier Excel
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Employes");
            // Ajouter les en-têtes (titres des colonnes)
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nom");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Salaire");
        }
        // Trouver la première ligne vide
        int rowNum = sheet.getLastRowNum() + 1;
        // Ajouter les nouvelles lignes
        for (Employe employe : employes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(employe.getId());
            row.createCell(1).setCellValue(employe.getName());
            row.createCell(2).setCellValue(employe.getEmail());
            row.createCell(3).setCellValue(employe.getSalary());
        }
        // Sauvegarder le fichier
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("Fichier Excel mis à jour : " + FILE_PATH);
    }
}
