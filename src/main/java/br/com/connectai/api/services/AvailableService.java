package br.com.connectai.api.services;

import br.com.connectai.api.errors.NotFoundException;
import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.repository.AvailableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvailableService {
    @Autowired
    private AvailableRepository repository;

    public Available getAtomicAvailableById(int availableId) {
        return repository.findById(availableId).orElseThrow(() -> new NotFoundException("Disponibilidade não encontrada"));
    }
}
