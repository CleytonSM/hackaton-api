package br.com.connectai.api.services;

import br.com.connectai.api.models.db.Available;
import br.com.connectai.api.models.db.Consult;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.models.dto.AvailableDTO;
import br.com.connectai.api.models.dto.ConsultDTO;
import br.com.connectai.api.models.dto.DoctorDTO;
import br.com.connectai.api.models.dto.DoctorResponse;
import br.com.connectai.api.models.enums.SpecialtiesEnum;
import br.com.connectai.api.repository.AvailableRepository;
import br.com.connectai.api.repository.ConsultRepository;
import br.com.connectai.api.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultService {
    @Autowired
    private AvailableRepository availableRepository;
    @Autowired
    private AvailableService availableService;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private ConsultRepository repository;


    public List<Doctor> getAllDoctorsBySpecialtyId(int specialtyId) {
        SpecialtiesEnum specialties = SpecialtiesEnum.fromCode(specialtyId);
        // dar um jeito de ranquear com o ML dps
        return doctorRepository.findBySpecialty(specialties.toString());
    }

    public DoctorResponse getAvailableTimes(int doctorId) {
        Doctor doctor = doctorService.getAtomicDoctorById(doctorId);
        List<Available> availabilities = availableRepository.findAllByDoctor(doctor);
        List<AvailableDTO> availableDTOS = new ArrayList<>();
        DoctorResponse response = new DoctorResponse();
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setName(doctor.getName());
        doctorDTO.setEmail(doctor.getEmail());
        doctorDTO.setCrm(doctor.getCrm());
        doctorDTO.setSpecialtyId(SpecialtiesEnum.valueOf(doctor.getSpecialty()).getCode());
        doctorDTO.setCreatedAt(doctor.getCreatedAt());
        doctorDTO.setUpdatedAt(doctor.getUpdatedAt());

        response.setDoctor(doctorDTO);
        availabilities.forEach(a -> {
            AvailableDTO dto = new AvailableDTO();
            dto.setId(a.getId());
            LocalDateTime localDateTime = a.getDatetimeAvailable();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String stringDate = localDateTime.format(formatter);

            dto.setDatetimeAvailable(stringDate);
            dto.setTime(a.getTime());
            dto.setSpecialtyId(SpecialtiesEnum.valueOf(doctor.getSpecialty()).getCode());
            availableDTOS.add(dto);
        });

        response.setAvailabilities(availableDTOS);
        return response;
    }

    @Transactional
    public void bookConsult(int doctorId, ConsultDTO consult) {
        Doctor doctor = doctorService.getAtomicDoctorById(doctorId);
        Available available = availableService.getAtomicAvailableById(consult.getAvailableId());
        Patient patient = patientService.getAtomicPatientById(consult.getPatientId());

        Consult newConsult = new Consult();
        newConsult.setConsultDate(available.getDatetimeAvailable());
        newConsult.setHour(available.getTime());
        newConsult.setHasHappened(Boolean.FALSE);
        newConsult.setDoctor(doctor);
        newConsult.setPatient(patient);

        repository.save(newConsult);
        availableRepository.delete(available);
    }

    public List<Consult> getAllConsultsByDoctor(int doctorId) {
        Doctor doctor = doctorService.getAtomicDoctorById(doctorId);
        return repository.findAllByDoctor(doctor);
    }

    public List<Consult> getAllConsultsByPatient(int patientId) {
        Patient patient = patientService.getAtomicPatientById(patientId);
        return repository.findAllByPatient(patient);
    }

    @Transactional
    public void updateConsultStatus(int id, boolean hasHappened) {
        Consult consult = getAtomicConsultById(id);
        consult.setHasHappened(hasHappened);
        repository.save(consult);
    }

    public Consult getAtomicConsultById(int id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }
}
