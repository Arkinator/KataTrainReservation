package de.zdf.service.pwValidation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PwValidatorService {
    private String pattern = "";

    @PostConstruct
    public void buildPattern() {
        int minLength = 6;
        int maxLength = 9;

        // Das Passwort soll mindestens einen Kleinbuchstaben enthalten
        pattern = "((?=.*[a-z])";

        // Das Passwort soll mindestens ein Sonderzeichen enthalten
        pattern += "(?=.*[@#$%])";

        // Das Passwort soll mindestens einen Großbuchstaben enthalten
        pattern += "(?=.*[A-Z])";

        // Das Passwort soll mindestens eine Zahl enthalten
        pattern += "(?=.*\\d)";

        // Das Passwort soll mindestens <minLength> lang sein, aber nicht länger als <maxLength>
        pattern += ".{" + minLength + "," + maxLength + "})";
    }

    public boolean validatePassword(final String password) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
