package br.com.connectai.api.services;

import br.com.connectai.api.errors.NotFoundException;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    @Autowired
    private PatientRepository repository;

    public Patient getPatientByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public Patient getAtomicPatientByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    public void save(Patient patient) {
        repository.save(patient);
    }

    public Patient getAtomicPatientById(int patientId) {
        return repository.findById(patientId).orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }
}
