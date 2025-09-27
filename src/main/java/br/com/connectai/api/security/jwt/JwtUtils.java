package br.com.connectai.api.security.jwt;

import br.com.connectai.api.helpers.SecretKeyHelper;
import br.com.connectai.api.models.db.Auth;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.security.user.UserAuthenticationManager;
import br.com.connectai.api.services.AuthService;
import br.com.connectai.api.services.DoctorService;
import br.com.connectai.api.services.PatientService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    @Autowired
    private SecretKeyHelper secretKeyHelper;
    @Autowired
    private UserAuthenticationManager userAuthenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;

    public String createDoctorToken(Authentication authentication) {
        Date date = new Date();
        SecretKey key = secretKeyHelper.secretKeyBuilder();

        return Jwts.builder()
                .issuer("ConnectaAi")
                .claims(doctorClaimsSetup(authentication))
                .subject("JWT Token")
                .issuedAt(date)
                .expiration(new Date(date.getTime() + 36000000L))
                .signWith(key)
                .compact();
    }

    public String createPatientToken(Authentication authentication) {
        Date date = new Date();
        SecretKey key = secretKeyHelper.secretKeyBuilder();

        return Jwts.builder()
                .issuer("ConnectaAi")
                .claims(patientClaimsSetup(authentication))
                .subject("JWT Token")
                .issuedAt(date)
                .expiration(new Date(date.getTime() + 36000000L))
                .signWith(key)
                .compact();
    }

    private Map<String, String> doctorClaimsSetup(Authentication authentication) {
        Doctor doctor = (Doctor) authentication.getPrincipal();
        Auth auth = doctor.getAuth();

        Map<String, String> claims = new HashMap<>();

        claims.put("email", doctor.getEmail());
        claims.put("role", auth.getAuthName());

        return claims;
    }

    private Map<String, String> patientClaimsSetup(Authentication authentication) {
        Patient patient = (Patient) authentication.getPrincipal();
        Auth auth = patient.getAuth();

        Map<String, String> claims = new HashMap<>();

        claims.put("email", patient.getEmail());
        claims.put("role", auth.getAuthName());

        return claims;
    }

    public Authentication getAuthentication(String token) {
        String email = extractEmail(token);
        Patient patient = patientService.getPatientByEmail(email);

        Doctor doctor = null;
        if(patient == null) {
            doctor = doctorService.getDoctorByEmail(email);
        }

        if(doctor == null && patient == null) {
            throw new RuntimeException("User not found");
        }

        if(patient != null) {
            Auth auth = patient.getAuth();

            return new UsernamePasswordAuthenticationToken(patient, "",
                    userAuthenticationManager.grantedAuthorities(auth));
        }

        return new UsernamePasswordAuthenticationToken(doctor, "",
                userAuthenticationManager.grantedAuthorities(doctor.getAuth()));
    }

    private String getUserEmailFromToken(String token) {
        SecretKey secretKey = secretKeyHelper.secretKeyBuilder();

        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("email", String.class);
    }

    public String extractEmail(String token) {
        SecretKey key = secretKeyHelper.secretKeyBuilder();
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
                .getPayload().get("email", String.class);
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public boolean validateToken(String token) {
        SecretKey secretKey = secretKeyHelper.secretKeyBuilder();

        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }

    public String extractRole(String token) {
        SecretKey key = secretKeyHelper.secretKeyBuilder();
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
                .getPayload().get("role", String.class);
    }
}
