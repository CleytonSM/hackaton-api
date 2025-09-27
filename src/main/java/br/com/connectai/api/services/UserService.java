package br.com.connectai.api.services;

import br.com.connectai.api.errors.NotFoundException;
import br.com.connectai.api.helpers.PasswordEncoderHelper;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.models.dto.LoginDTO;
import br.com.connectai.api.models.dto.LoginReturnDTO;
import br.com.connectai.api.security.jwt.JwtUtils;
import br.com.connectai.api.security.user.UserAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserAuthenticationManager userAuthenticationManager;

    public LoginReturnDTO login(LoginDTO login) {
        Doctor doctor = doctorService.getDoctorByEmail(login.getEmail());
        if(doctor != null) {
            LoginReturnDTO result = new LoginReturnDTO();
            result.setLabel(doctor.getAuth().getAuthName());
            result.setId(doctor.getId());
            //Authentication authentication = userAuthenticationManager
            //        .authenticateDoctor(new UsernamePasswordAuthenticationToken(doctor, "$2a$12$4kZsQIM8ux7u0fgBhMB31eEF9lvgbrZrJSh3M5DxUBLpwidiesdGm"));
            //result.setToken(jwtUtils.createDoctorToken(authentication));
            return result;
        }

        Patient patient = patientService.getPatientByEmail(login.getEmail());
        if(patient != null) {
            LoginReturnDTO result = new LoginReturnDTO();
            result.setLabel(patient.getAuth().getAuthName());
            result.setId(patient.getId());
            //Authentication authentication = userAuthenticationManager
                    //authenticateDoctor(new UsernamePasswordAuthenticationToken(doctor, "$2a$12$4kZsQIM8ux7u0fgBhMB31eEF9lvgbrZrJSh3M5DxUBLpwidiesdGm"));
            //result.setToken(jwtUtils.createDoctorToken(authentication));
            return result;
        }

        throw new NotFoundException("Usuário não encontrado");
    }
}
