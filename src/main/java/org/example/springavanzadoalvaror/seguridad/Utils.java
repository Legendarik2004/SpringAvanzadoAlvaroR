package org.example.springavanzadoalvaror.seguridad;

import java.security.SecureRandom;
import java.util.Base64;

public class Utils {

    private Utils() {
        //NOP
    }

    public static String randomBytes()
    {
        SecureRandom sr = new SecureRandom();
        byte[] bits = new byte[32];
        sr.nextBytes(bits);
        return Base64.getUrlEncoder().encodeToString(bits);
    }
}