package br.com.connectai.api.services;

import br.com.connectai.api.errors.NotFoundException;
import br.com.connectai.api.helpers.PasswordEncoderHelper;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.models.dto.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PasswordEncoderHelper passwordEncoderHelper;
    @Autowired
    private EmailService emailService;

    public void login(LoginDTO login) {
        Doctor doctor = doctorService.getDoctorByEmail(login.getEmail());
        if(doctor != null) {
            String pass = passwordEncoderHelper.generateCode();
            String hashedPass = passwordEncoderHelper.encodePassword(pass);
            doctor.setPassword(hashedPass);
            doctorService.save(doctor);
            emailService.sendEmail(login.getEmail(), pass);
            return;
        }

        Patient patient = patientService.getPatientByEmail(login.getEmail());
        if(patient != null) {
            String pass = passwordEncoderHelper.generateCode();
            String hashedPass = passwordEncoderHelper.encodePassword(pass);
            patient.setPassword(hashedPass);
            patientService.save(patient);
            emailService.sendEmail(login.getEmail(), pass);

            return;
        }

        throw new NotFoundException("Usuário não encontrado");
    }
}
