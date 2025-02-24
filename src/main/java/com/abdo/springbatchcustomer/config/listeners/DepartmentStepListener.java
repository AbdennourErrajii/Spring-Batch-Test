package com.abdo.springbatchcustomer.config.listeners;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Random;
@Component
@AllArgsConstructor
public class DepartmentStepListener implements StepExecutionListener {
    private DataSource dataSource;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("********Before Step*************");
        System.out.println("Step Name :" + stepExecution.getStepName());
        System.out.println("Step Execution :" + stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("******** After Step ********");
        System.out.println("Step Execution :" + stepExecution);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Ajouter la colonne random_value si elle n'existe pas déjà
            statement.executeUpdate("ALTER TABLE department ADD COLUMN random_value INT");
            System.out.println("Colonne random_value ajoutée avec succès.");

            // Récupérer tous les IDs de la table department
            String selectQuery = "SELECT id FROM department";
            try (ResultSet resultSet = statement.executeQuery(selectQuery)) {
                // Préparer la requête de mise à jour
                String updateQuery = "UPDATE department SET random_value = ? WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    Random random = new Random();
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int randomValue = random.nextInt(50) + 1; // Valeur aléatoire entre 1 et 50
                        // Mettre à jour la colonne random_value pour chaque ligne
                        preparedStatement.setInt(1, randomValue);
                        preparedStatement.setInt(2, id);
                        preparedStatement.executeUpdate();
                    }
                }
            }
            System.out.println("Colonne random_value remplie avec des valeurs aléatoires.");

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution des requêtes SQL : " + e.getMessage(), e);
        }
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            return ExitStatus.COMPLETED;
        } else {
            return ExitStatus.FAILED;
        }

    }
}
