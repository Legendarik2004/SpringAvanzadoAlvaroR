/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.springavanzadoalvaror.asimetrico;


import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.errors.ErrorApp;
import org.example.springavanzadoalvaror.ui.main.Configuration;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Log4j2
@Component
public class Cert {



    public void generarYGuardarClaves(String alias) {
        try {
            // Generar claves publicas y privadas
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constantes.RSA);
            keyGen.initialize(2048, new SecureRandom());
            KeyPair clavesRSA = keyGen.generateKeyPair();
            PrivateKey clavePrivada = clavesRSA.getPrivate();
            PublicKey clavePublica = clavesRSA.getPublic();

            // Generar certificado
            X509Certificate cert = crearCertificado(clavePublica, clavePrivada);

            // Guardar claves en un KeyStore
            guardarClavesKeyStore(alias, cert, clavePrivada);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public X509Certificate crearCertificado(PublicKey clavePublica, PrivateKey clavePrivada) {
        try {
            X500Name owner = new X500Name(Constantes.CN_ALVARO);
            X500Name issuer = new X500Name(Constantes.CN_SERVIDOR);
            X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(
                    issuer,
                    BigInteger.valueOf(1),
                    new Date(),
                    new Date(System.currentTimeMillis() + 1000000),
                    owner, SubjectPublicKeyInfo.getInstance(
                    ASN1Sequence.getInstance(clavePublica.getEncoded()))
            );

            ContentSigner sigGen = new JcaContentSignerBuilder(Constantes.SHA_256_WITH_RSA_ENCRYPTION).build(clavePrivada);
            X509CertificateHolder holder = certGen.build(sigGen);

            return new JcaX509CertificateConverter().getCertificate(holder);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public void guardarClavesKeyStore(String alias, X509Certificate cert, PrivateKey clavePrivada) {
        try {
            char[] password = Configuration.getInstance().getProperty(Constantes.KEY_STORE_PASSWORD).toCharArray();
            KeyStore ks = KeyStore.getInstance(Constantes.PKCS_12);
            ks.load(new FileInputStream(Configuration.getInstance().getProperty(Constantes.KEY_STORE_ROUTE)), password);
            ks.setCertificateEntry(alias, cert);
            ks.setKeyEntry(alias, clavePrivada, password, new Certificate[]{cert});
            FileOutputStream fos = new FileOutputStream(Configuration.getInstance().getProperty(Constantes.KEY_STORE_ROUTE));
            ks.store(fos, password);
            fos.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Either<ErrorApp, KeyPair> cargarClavesKeyStore(String user) {
        Either<ErrorApp, KeyPair> result;
        try {
            char[] password = Configuration.getInstance().getProperty(Constantes.KEY_STORE_PASSWORD).toCharArray();
            KeyStore ksLoad = KeyStore.getInstance(Constantes.PKCS_12);
            ksLoad.load(new FileInputStream(Configuration.getInstance().getProperty(Constantes.KEY_STORE_ROUTE)), password);

            X509Certificate certLoad = (X509Certificate) ksLoad.getCertificate(user);
            KeyStore.PasswordProtection pt = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ksLoad.getEntry(user, pt);
            PrivateKey keyLoad = privateKeyEntry.getPrivateKey();

            result = Either.right(new KeyPair(certLoad.getPublicKey(), keyLoad));

        } catch (Exception e) {
            result = Either.left(new ErrorApp(e.getMessage()));
        }
        return result;
    }


    public String createSignature(String data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(Constantes.SHA_256_WITH_RSA);
            signature.initSign(privateKey);
            MessageDigest hash = MessageDigest.getInstance(Constantes.SHA_256);
            signature.update(hash.digest(data.getBytes()));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public boolean verificarFirmaPasswordRecurso(String passwordRecurso, String firmaRecurso, PublicKey publicKey) {
        try {

            // Verificar la firma de la contraseña del recurso con la clave pública del usuario
            byte[] firmaBytes = Base64.getDecoder().decode(firmaRecurso);
            Signature firma = Signature.getInstance(Constantes.SHA_256_WITH_RSA);
            firma.initVerify(publicKey);
            MessageDigest hash = MessageDigest.getInstance(Constantes.SHA_256);
            firma.update(hash.digest(passwordRecurso.getBytes()));
            return firma.verify(firmaBytes);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
