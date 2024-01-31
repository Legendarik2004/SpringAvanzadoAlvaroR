package org.example.springavanzadoalvaror.common;

public class Constantes {
    private Constantes() {
        //NOP
    }

    public static final String CREADO_CORRECTAMENTE = "Creado correctamente";
    public static final String ERROR_AL_CAMBIAR_LA_PASSWORD = "Error al cambiar la password";
    public static final String ERROR_AL_COMPARTIR = "Error al compartir";
    public static final String ERROR_AL_COMPARTIR_EL_RECURSO = "Error al compartir el recurso";
    public static final String ERROR_AL_ENCRIPTAR = "Error al encriptar";
    public static final String ERROR_AL_FIRMAR = "Error al firmar";
    public static final String FIRMA_CORRECTA = "Firma correcta";
    public static final String FIRMA_INCORRECTA = "Firma incorrecta";
    public static final String RECURSO_COMPARTIDO_CORRECTAMENTE = "Recurso compartido correctamente";
    public static final String RELLENA_TODOS_LOS_CAMPOS = "Rellena todos los campos";
    public static final String PASSWORD_CAMBIADA_CORRECTAMENTE = "Password cambiada correctamente";
    public static final String USUARIO_REGISTRADO_CORRECTAMENTE = "Usuario registrado correctamente";
    public static final String USUARIO_O_PASSWORD_INCORRECTOS = "Usuario o password incorrectos";
    public static final String USUARIO_NO_REGISTRADO = "Usuario no registrado";
    public static final String SELECCIONA_UN_RECURSO = "Selecciona un recurso";
    public static final String SELECCIONA_UN_USUARIO = "Selecciona un usuario";

    public static final String AES = "AES";
    public static final String AES_GCM_NO_PADDING = "AES/GCM/noPadding";
    public static final String ALERT = "alert";
    public static final String BTN_OK = "btn-ok";
    public static final String CN_ALVARO = "CN=Alvaro";
    public static final String CN_SERVIDOR = "CN=Servidor";
    public static final String CONFIG_PROPERTIES = "config.properties";
    public static final String CONFIRMACION = "Confirmacion";
    public static final String ERROR = "Error";
    public static final String KEY_STORE_PASSWORD = "keyStorePassword";
    public static final String KEY_STORE_ROUTE = "keyStoreRoute";
    public static final String PASSWORD = "password";
    public static final String PBKDF_2_WITH_HMAC_SHA_256 = "PBKDF2WithHmacSHA256";
    public static final String PKCS_12 = "PKCS12";
    public static final String PROGRAM_NAME = "programName";
    public static final String QUERY = "SELECT u.name FROM User u WHERE u.name NOT IN (SELECT a.username FROM Asymmetric a WHERE a.symmetric.id = :symmetricId)";
    public static final String RECURSO = "Recurso ";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_256_WITH_RSA = "SHA256withRSA";
    public static final String SHA_256_WITH_RSA_ENCRYPTION = "SHA256WithRSAEncryption";
    public static final String SYMMETRIC_ID = "symmetricId";
    public static final String TEXT = "TEXT";
    public static final String RSA = "RSA";

}