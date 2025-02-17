package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.entity.EmployeDTO;
import com.abdo.springbatchcustomer.repo.EmployeDtoRepo;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class NewTableWriter implements ItemWriter<EmployeDTO> {
    @Autowired
    private EmployeDtoRepo employeDtoRepository;
        @Override
        public void write(Chunk<? extends EmployeDTO> chunk) throws Exception {
            employeDtoRepository.saveAll(chunk.getItems());
        }

}
