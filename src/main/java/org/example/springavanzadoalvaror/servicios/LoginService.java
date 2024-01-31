package org.example.springavanzadoalvaror.servicios;

import io.vavr.control.Either;

import org.example.springavanzadoalvaror.asimetrico.Cert;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.UserRepository;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;
import org.example.springavanzadoalvaror.data.modelo.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cert cert;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, Cert cert) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cert = cert;
    }

    public Either<ErrorApp, Boolean> doLogin(String usuario, String password) {
        Either<ErrorApp, Boolean> result;
        User u = userRepository.findByName(usuario);
        if (u != null) {
            if (passwordEncoder.matches(password, u.getPassword())) {
                result = Either.right(true);
            } else {
                result = Either.left(new ErrorApp(Constantes.USUARIO_O_PASSWORD_INCORRECTOS));
            }
        } else {
            result = Either.left(new ErrorApp(Constantes.USUARIO_O_PASSWORD_INCORRECTOS));
        }
        return result;
    }

    public Either<ErrorApp, User> doRegister(String usuario, String password) {
        Either<ErrorApp, User> result;
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(usuario, encodedPassword);
        userRepository.save(user);
        User u = userRepository.findByName(user.getName());
        if (u != null) {
            try{
                cert.generarYGuardarClaves(u.getName());
                result = Either.right(u);
            } catch (Exception e) {
                result = Either.left(new ErrorApp(e.getMessage()));
            }
        } else {
            result = Either.left(new ErrorApp(Constantes.USUARIO_NO_REGISTRADO));
        }
        return result;
    }

}