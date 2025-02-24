package com.abdo.springbatchcustomer.config.Readers;

import com.abdo.springbatchcustomer.entity.Employe;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;

public class PartitionedCsvReader extends FlatFileItemReader<Employe> implements ItemStreamReader<Employe> {
    private int startLine = 1;
    private int endLine = Integer.MAX_VALUE;
    private int currentLine = 0;
    private boolean initialized = false;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
        if (!initialized && executionContext != null) {
            this.startLine = executionContext.containsKey("startLine") ?
                    executionContext.getInt("startLine") : 1;
            this.endLine = executionContext.containsKey("endLine") ?
                    executionContext.getInt("endLine") : Integer.MAX_VALUE;
            this.initialized = true;
        }
    }

    @Override
    public Employe read() throws Exception {
        if (currentLine >= endLine) {
            return null;
        }

        while (currentLine < startLine - 1) {
            super.read();
            currentLine++;
        }

        Employe employe = super.read();
        if (employe != null) {
            currentLine++;
        }

        return employe;
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();
        this.initialized = false;
        this.currentLine = 0;
    }
}
