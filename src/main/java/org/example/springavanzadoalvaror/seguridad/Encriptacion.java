package org.example.springavanzadoalvaror.seguridad;

import io.vavr.control.Either;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;

public interface Encriptacion {

    Either<ErrorApp, String> encriptar(String texto, String secret);

    Either<ErrorApp, String> desencriptar(String texto,String secret);

}
