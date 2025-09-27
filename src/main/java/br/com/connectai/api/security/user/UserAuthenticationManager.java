package br.com.connectai.api.security.user;

import br.com.connectai.api.helpers.PasswordEncoderHelper;
import br.com.connectai.api.models.db.Auth;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.services.DoctorService;
import br.com.connectai.api.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class UserAuthenticationManager {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PasswordEncoderHelper passwordEncoderHelper;

    public Authentication authenticateDoctor(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String code = authentication.getCredentials().toString();

        Doctor doctor = doctorService.getAtomicDoctorByEmail(email);
        passwordEncoderHelper.matchCode(code, doctor.getPassword());
        Auth auth = doctor.getAuth();

        return new UsernamePasswordAuthenticationToken(doctor, code, grantedAuthorities(auth));
    }

    public Authentication authenticatePatient(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String code = authentication.getCredentials().toString();

        Patient patient = patientService.getAtomicPatientByEmail(email);
        passwordEncoderHelper.matchCode(code, patient.getPassword());
        Auth auth = patient.getAuth();

        return new UsernamePasswordAuthenticationToken(patient, code, grantedAuthorities(auth));
    }

    public Collection<? extends GrantedAuthority> grantedAuthorities(Auth auth) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(auth.getAuthName()));

        return grantedAuthorities;
    }
}
