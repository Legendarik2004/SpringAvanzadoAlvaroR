package org.example.springavanzadoalvaror.seguridad.impl;

import com.google.common.primitives.Bytes;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;
import org.example.springavanzadoalvaror.seguridad.Encriptacion;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

@Log4j2
@Component
public class EncriptacionAES implements Encriptacion {


    @Override
    public Either<ErrorApp, String> encriptar(String strToEncrypt, String secret) {
        Either<ErrorApp, String> result;
        try {
            byte[] iv = new byte[12];
            byte[] salt = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);
            sr.nextBytes(salt);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(Constantes.PBKDF_2_WITH_HMAC_SHA_256);
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Constantes.AES);

            Cipher cipher = Cipher.getInstance(Constantes.AES_GCM_NO_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            result = Either.right(Base64.getUrlEncoder().encodeToString(Bytes.concat(iv, salt,
                    cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)))));
        } catch (Exception e) {
            result = Either.left(new ErrorApp(Constantes.ERROR_AL_ENCRIPTAR));
        }
        return result;
    }

    @Override
    public Either<ErrorApp, String> desencriptar(String strToDecrypt, String secret) {
        Either<ErrorApp, String> result;
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(strToDecrypt);

            byte[] iv = Arrays.copyOf(decoded, 12);
            byte[] salt = Arrays.copyOfRange(decoded, 12, 28);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(Constantes.PBKDF_2_WITH_HMAC_SHA_256);
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Constantes.AES);

            Cipher cipher = Cipher.getInstance(Constantes.AES_GCM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            result = Either.right(new String(cipher.doFinal(Arrays.copyOfRange(decoded, 28, decoded.length)), StandardCharsets.UTF_8));
        } catch (Exception e) {
            result = Either.left(new ErrorApp(e.getMessage()));
        }
        return result;
    }
}

