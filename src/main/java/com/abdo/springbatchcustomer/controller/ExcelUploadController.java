package com.abdo.springbatchcustomer.controller;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ExcelUploadController {
    @GetMapping("/page")
    public String employee() {
        return "page";
    }
    @GetMapping("/uploadPage")
    public String showUploadPage() {
        return "uploadExcel";  // Thymeleaf va chercher le fichier uploadExcel.html
    }

    @PostMapping("/uploadExcel")
    public String uploadExcelFile(@RequestParam("excelFile") MultipartFile file) {
        try {
            // Lire le fichier Excel
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Récupérer la première feuille
            Sheet sheet = workbook.getSheetAt(0);

            // Lire la première ligne pour afficher les titres des colonnes
            Row headerRow = sheet.getRow(0);  // Première ligne
            if (headerRow != null) {
                System.out.println("Column Titles:");
                for (Cell cell : headerRow) {
                    // Afficher les titres des colonnes
                    System.out.print(cell.getStringCellValue() + "\t");
                }
                System.out.println();  // Nouvelle ligne après les titres
            }

            workbook.close();
            return "Excel file uploaded and processed successfully!";
        } catch (IOException e) {
            return "Failed to upload Excel file: " + e.getMessage();
        }
    }

}
