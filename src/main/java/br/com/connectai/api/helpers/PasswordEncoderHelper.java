package br.com.connectai.api.helpers;

import br.com.connectai.api.errors.WrongCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderHelper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void matchCode(String rawCode, String hashedCode) {
        if(!passwordEncoder.matches(rawCode, hashedCode)) {
            throw new WrongCredentialsException();
        }
    }

    public String encodePassword(String code) {
        return passwordEncoder.encode(code);
    }

    public String generateCode() {
        return GenerateCodeHelper.generateRandomCode();
    }
}