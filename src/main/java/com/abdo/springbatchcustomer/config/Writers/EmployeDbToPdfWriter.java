package com.abdo.springbatchcustomer.config.Writers;
import com.abdo.springbatchcustomer.entity.Employe;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import java.io.File;
import java.io.IOException;

public class EmployeDbToPdfWriter implements ItemWriter<Employe> {
    private static final Logger log = LoggerFactory.getLogger(EmployeDbToPdfWriter.class);
    private static final String OUTPUT_DIR = "src/main/resources/outputs";
    private static final String FILE_NAME = "employe.pdf";

    @Override
    public void write(Chunk<? extends Employe> chunk) throws Exception {
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (!created) {
                throw new IOException("Impossible de créer le dossier de sortie");
            }
            log.info("Dossier de sortie créé : {}", outputDir.getAbsolutePath());
        }
        File pdfFile = new File(outputDir, FILE_NAME);
        PDDocument document;

        if (pdfFile.exists()) {
            // Ouvrir le fichier existant
            document = PDDocument.load(pdfFile);
        } else {
            // Créer un nouveau fichier
            document = new PDDocument();
        }

        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 700);

            if (document.getNumberOfPages() == 1 && document.getPages().getCount() == 1) {
                contentStream.showText("Liste des Employés");
                contentStream.newLine();
                contentStream.newLine();
            }
            for (Employe employe : chunk.getItems()) {
                contentStream.showText("- ID : " + employe.getId());
                contentStream.newLine();
                contentStream.showText("- Nom : " + employe.getName());
                contentStream.newLine();
                contentStream.showText("- Email : " + employe.getEmail());
                contentStream.newLine();
                contentStream.showText("- Téléphone : " + employe.getPhone());
                contentStream.newLine();
                contentStream.showText("- Salaire : " + employe.getSalary() + "€");
                contentStream.newLine();
                contentStream.newLine();
            }
            contentStream.endText();
        }
        document.save(pdfFile);
        document.close();
        log.info("PDF mis à jour avec succès : {}", pdfFile.getAbsolutePath());
    }
}
