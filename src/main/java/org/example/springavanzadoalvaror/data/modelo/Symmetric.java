package org.example.springavanzadoalvaror.data.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springavanzadoalvaror.common.Constantes;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "symmetric")
public class Symmetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "password_encrypted_sym")
    private String password;
    @Column(name = "program_name")
    private String programName;
    @Column(name = "sign", columnDefinition = Constantes.TEXT)
    private String sign;
    @Column(name = "signer_name")
    private String signerName;

    @OneToMany(mappedBy = "symmetric")
    private List<Asymmetric> asymmetrics;

    public Symmetric(String password, String programName, String sign, String signerName) {
        this.password = password;
        this.programName = programName;
        this.sign = sign;
        this.signerName = signerName;
    }
}