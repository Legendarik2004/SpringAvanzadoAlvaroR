package org.example.springavanzadoalvaror.seguridad;

import io.vavr.control.Either;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface EncriptacionAsym {
    Either<ErrorApp, String> encriptar(String texto, PublicKey publicKey);

    Either<ErrorApp, String> desencriptar(String textoCifrado, PrivateKey privateKey);
}
