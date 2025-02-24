package com.abdo.springbatchcustomer.config.listeners;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class DepartmentChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("********Début du chunk***********");
        System.out.println(context);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("Fin du chunk");
        System.out.println(context);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("Erreur lors du traitement du chunk");
        System.out.println(context);
    }
}
