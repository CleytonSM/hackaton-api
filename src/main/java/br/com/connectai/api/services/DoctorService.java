package br.com.connectai.api.services;

import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository repository;

    public Doctor getDoctorByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public Doctor getAtomicDoctorByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Doutor não encontrado"));
    }

    public void save(Doctor doctor) {
        repository.save(doctor);
    }

    public Doctor getAtomicDoctorById(int doctorId) {
        return repository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doutor não encontrado"));
    }
}
