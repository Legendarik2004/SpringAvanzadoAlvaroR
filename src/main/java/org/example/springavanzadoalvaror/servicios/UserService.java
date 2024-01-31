package org.example.springavanzadoalvaror.servicios;

import io.vavr.control.Either;
import org.example.springavanzadoalvaror.asimetrico.Cert;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.AsymmetricRepository;
import org.example.springavanzadoalvaror.data.SymmetricRepository;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;
import org.example.springavanzadoalvaror.data.modelo.Asymmetric;
import org.example.springavanzadoalvaror.data.modelo.Symmetric;
import org.example.springavanzadoalvaror.seguridad.Encriptacion;
import org.example.springavanzadoalvaror.seguridad.EncriptacionAsym;
import org.example.springavanzadoalvaror.seguridad.Utils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

@Service
public class UserService {

    private final SymmetricRepository symmetricRepository;
    private final AsymmetricRepository asymmetricRepository;
    private final Encriptacion encriptacion;
    private final EncriptacionAsym encriptacionAsym;
    private final Cert cert;

    public UserService(SymmetricRepository symmetricRepository, AsymmetricRepository asymmetricRepository, Encriptacion encriptacion, EncriptacionAsym encriptacionAsym, Cert cert) {
        this.symmetricRepository = symmetricRepository;
        this.asymmetricRepository = asymmetricRepository;
        this.encriptacion = encriptacion;
        this.encriptacionAsym = encriptacionAsym;
        this.cert = cert;
    }

    public List<Symmetric> getRecursos(String username) {
        return asymmetricRepository.findByUsername(username).stream().map(Asymmetric::getSymmetric).toList();
    }

    public List<Asymmetric> getCompartidos(long id) {
        Sort sort = Sort.by(Sort.Direction.ASC, Constantes.USERNAME);
        return asymmetricRepository.findBySymmetricId(id, sort);
    }

    public List<String> getPossibleCompartidos(Symmetric recurso) {
        return asymmetricRepository.findUsersWithoutAsymmetricBySymmetricId(recurso.getId());
    }

    public Either<ErrorApp, Symmetric> crearRecurso(String username, String nombreRecurso, String passwordRecurso) {
        Either<ErrorApp, Symmetric> result;
        try {

            Either<ErrorApp, KeyPair> keyPairs = cert.cargarClavesKeyStore(username);

            if (keyPairs.isRight()) {
                PrivateKey clavePrivada = keyPairs.get().getPrivate();
                PublicKey clavePublica = keyPairs.get().getPublic();

                // Firmar el recurso
                String firma = cert.createSignature(passwordRecurso, clavePrivada);
                if (firma != null) {

                    // Encriptar la contraseña del recurso
                    String random = Utils.randomBytes();
                    String passwordEncriptada = encriptacion.encriptar(passwordRecurso, random).get();

                    // Encriptar el random con la clave publica del usuario
                    String randomEncrypt = encriptacionAsym.encriptar(random, clavePublica).get();

                    //Guardo y consigo el recurso con su id generado
                    symmetricRepository.save(new Symmetric(passwordEncriptada, nombreRecurso, firma, username));
                    Symmetric recurso = symmetricRepository.findByProgramName(nombreRecurso);

                    Either<ErrorApp, Boolean> compartido = compartirRecursoPropio(username, recurso, randomEncrypt);

                    if (compartido.isRight()) {
                        if (Boolean.TRUE.equals(compartido.get())) {
                            result = Either.right(recurso);
                        } else {
                            result = Either.left(new ErrorApp(Constantes.ERROR_AL_COMPARTIR));
                        }
                    } else {
                        result = Either.left(compartido.getLeft());
                    }
                } else {
                    result = Either.left(new ErrorApp(Constantes.ERROR_AL_FIRMAR));
                }
            } else {
                result = Either.left(keyPairs.getLeft());
            }
        } catch (Exception e) {
            result = Either.left(new ErrorApp(e.getMessage()));
        }
        return result;
    }

    public Either<ErrorApp, Boolean> compartirRecursoPropio(String username, Symmetric recurso, String encryptedRandom) {
        Asymmetric asymmetric = new Asymmetric();
        asymmetric.setUsername(username);
        asymmetric.setSymmetric(recurso);
        asymmetric.setEncriptedKey(encryptedRandom);
        if (asymmetricRepository.save(asymmetric) == asymmetric) {
            return Either.right(true);
        } else {
            return Either.left(new ErrorApp(Constantes.ERROR_AL_COMPARTIR));
        }
    }

    public Either<ErrorApp, Boolean> compartirRecurso(String username, String nombreCompartido, Symmetric recurso) {

        Either<ErrorApp, KeyPair> clavesUser = cert.cargarClavesKeyStore(username);

        Either<ErrorApp, KeyPair> clavesCompartido = cert.cargarClavesKeyStore(nombreCompartido);

        Asymmetric as = asymmetricRepository.findBySymmetricIdAndUsername((int) recurso.getId(), username);

        Either<ErrorApp, Boolean> result = Either.left(new ErrorApp(Constantes.ERROR_AL_COMPARTIR_EL_RECURSO));

        if (clavesUser.isRight() || clavesCompartido.isRight()) {
            Either<ErrorApp, String> random = encriptacionAsym.desencriptar(as.getEncriptedKey(), clavesUser.get().getPrivate());
            if (random.isRight()) {
                Either<ErrorApp, String> encryptedPassword = encriptacionAsym.encriptar(random.get(), clavesCompartido.get().getPublic());


                if (encryptedPassword.isRight()) {
                    Asymmetric asymmetric = new Asymmetric();
                    asymmetric.setUsername(nombreCompartido);
                    asymmetric.setSymmetric(recurso);
                    asymmetric.setEncriptedKey(encryptedPassword.get());
                    if (asymmetricRepository.save(asymmetric) == asymmetric) {
                        result = Either.right(true);
                    }
                }
            }
        }
        return result;
    }


    public boolean changePassword(String username, String password, Symmetric recurso) {
        Either<ErrorApp, KeyPair> claves = cert.cargarClavesKeyStore(username);
        Asymmetric as = asymmetricRepository.findBySymmetricIdAndUsername((int) recurso.getId(), username);

        Either<ErrorApp, String> random = encriptacionAsym.desencriptar(as.getEncriptedKey(), claves.get().getPrivate());
        String firma = cert.createSignature(password, claves.get().getPrivate());

        if (random.isRight()) {
            String passwordEncriptada = encriptacion.encriptar(password, random.get()).get();
            recurso.setPassword(passwordEncriptada);
            recurso.setSign(firma);
            recurso.setSignerName(username);
            symmetricRepository.save(recurso);
            return true;
        } else {
            return false;
        }
    }

    public Either<ErrorApp, Boolean> checkFirma(String username, Symmetric recurso) {
        Either<ErrorApp, Boolean> result;

        Either<ErrorApp, KeyPair> claves = cert.cargarClavesKeyStore(username);
        PrivateKey clavePrivada = claves.get().getPrivate();
        PublicKey clavePublica = claves.get().getPublic();

        Asymmetric as = asymmetricRepository.findBySymmetricIdAndUsername((int) recurso.getId(), username);

        if (claves.isRight()) {
            Either<ErrorApp, String> random = encriptacionAsym.desencriptar(as.getEncriptedKey(), clavePrivada);
            if (random.isRight()) {
                String password = encriptacion.desencriptar(recurso.getPassword(), random.get()).get();
                result = Either.right(cert.verificarFirmaPasswordRecurso(password, recurso.getSign(), clavePublica));
            } else {
                result = Either.left(random.getLeft());
            }
        } else {
            result = Either.left(claves.getLeft());
        }
        return result;
    }
}
