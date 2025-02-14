package com.abdo.springbatchcustomer.config.Readers;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;


public class ApachePoiReader {

    public void readExcelWithPoi(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Lire le premier onglet
            for (Row row : sheet) {
                // Supposons que la première ligne est l'entête
                if (row.getRowNum() == 0) {
                    continue;
                }

                int year = (int) row.getCell(0).getNumericCellValue();
                String quarter = row.getCell(1).getStringCellValue();
                double amount = row.getCell(2).getNumericCellValue();

                // Afficher les données lues
                System.out.println("Year: " + year + ", Quarter: " + quarter + ", Amount: " + amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
