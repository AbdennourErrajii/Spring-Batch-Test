package com.abdo.springbatchcustomer.controller;

public interface AppInterface {

    public void CsvToDb();

    public void XMLToDb();
    public void JsonToDb();

    public void ExcelToDb();
    public void PdfToDb();

    public void DbToCsv();
    public void DbToXml();
    public void DbToJson();
    public void DbToExcel();
    public void DbToPdf();



}
