package com.abdo.springbatchcustomer.config.partitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Component
public class CsvPartitioner implements Partitioner {
    private Resource resource; // Fichier CSV à partitionner
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Ignorer la première ligne
            int startLine = 1;
            int partitionIndex = 0;
            while (reader.readLine() != null) {
                if ((startLine - 1) % gridSize == 0) {
                    ExecutionContext context = new ExecutionContext();
                    context.putInt("startLine", startLine);
                    context.putInt("endLine", startLine + gridSize - 1);
                    partitionMap.put("partition" + partitionIndex, context);
                    partitionIndex++;
                }
                startLine++;
            }
            System.out.println("************************"+partitionMap);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV", e);
        }
        return partitionMap;
    }

}
