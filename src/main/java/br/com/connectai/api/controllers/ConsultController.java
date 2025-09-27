package br.com.connectai.api.controllers;

import br.com.connectai.api.models.dto.ConsultDTO;
import br.com.connectai.api.models.dto.ConsultIdDTO;
import br.com.connectai.api.services.ConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consults")
public class ConsultController {
    @Autowired
    private ConsultService service;

    @GetMapping("/doctors/{specialtyId}")
    public ResponseEntity<?> getSpecialities(@PathVariable("specialtyId") int specialtyId) {
        return new ResponseEntity<>(service.getAllDoctorsBySpecialtyId(specialtyId), HttpStatus.OK);
    }

    @GetMapping("/doctors/{doctorId}/availability")
    public ResponseEntity<?> getAvailableTimes(@PathVariable("doctorId") int doctorId) {
        return new ResponseEntity<>(service.getAvailableTimes(doctorId), HttpStatus.OK);
    }

    @PostMapping("/doctors/{doctorId}")
    public ResponseEntity<?> bookConsult(@PathVariable("doctorId") int doctorId, @RequestBody ConsultDTO consult) {
        service.bookConsult(doctorId, consult);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/doctors/{doctorId}/all")
    public ResponseEntity<?> getAllConsultsByDoctor(@PathVariable("doctorId") int doctorId) {
        // historico de consultas do doutor
        return new ResponseEntity<>(service.getAllConsultsByDoctor(doctorId), HttpStatus.OK);
    }

    // update do has happened
    @PutMapping
    public ResponseEntity<?> updateConsultStatus(@RequestBody ConsultIdDTO consultUpdate,
                                                 @RequestParam(name = "hasHappened", defaultValue = "false") boolean hasHappened) {
        service.updateConsultStatus(consultUpdate, hasHappened);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // historico de consultas do paciente
    @GetMapping("/patient/{patientId}/all")
    public ResponseEntity<?> getAllConsults(@PathVariable("patientId") int patientId) {
        return new ResponseEntity<>(service.getAllConsultsByPatient(patientId), HttpStatus.NO_CONTENT);
    }
}
