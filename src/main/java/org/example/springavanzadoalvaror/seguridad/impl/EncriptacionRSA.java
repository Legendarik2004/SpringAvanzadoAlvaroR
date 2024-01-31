package org.example.springavanzadoalvaror.seguridad.impl;

import io.vavr.control.Either;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;
import org.example.springavanzadoalvaror.seguridad.EncriptacionAsym;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Component
public class EncriptacionRSA implements EncriptacionAsym {

    @Override
    public Either<ErrorApp, String> encriptar(String texto, PublicKey clavePublica) {
        Either<ErrorApp, String> result;
        try {
            Cipher cifrador = Cipher.getInstance(Constantes.RSA);
            cifrador.init(Cipher.ENCRYPT_MODE, clavePublica);
            byte[] encoded = cifrador.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            result = Either.right(Base64.getUrlEncoder().encodeToString(encoded));
        } catch (Exception e) {
            result = Either.left(new ErrorApp(e.getMessage()));
        }
        return result;
    }

    @Override
    public Either<ErrorApp, String> desencriptar(String textoCifrado, PrivateKey clavePrivada) {
        Either<ErrorApp, String> result;
        byte[] textoCifradoBytes = Base64.getUrlDecoder().decode(textoCifrado);
        try {
            Cipher cifrador = Cipher.getInstance(Constantes.RSA);
            cifrador.init(Cipher.DECRYPT_MODE, clavePrivada);
            byte[] textoDescifradoBytes = cifrador.doFinal(textoCifradoBytes);
            result = Either.right(new String(textoDescifradoBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            result = Either.left(new ErrorApp(e.getMessage()));
        }
        return result;
    }
}