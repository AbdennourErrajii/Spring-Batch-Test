package com.abdo.springbatchcustomer.config.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class EquipementStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Début du step");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Fin du step - Nombre de commits : "
                + stepExecution.getCommitCount());
        return ExitStatus.COMPLETED; // Retourner un statut correct
    }
}
