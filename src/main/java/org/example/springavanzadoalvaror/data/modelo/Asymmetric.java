package org.example.springavanzadoalvaror.data.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springavanzadoalvaror.common.Constantes;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asymmetric")
public class Asymmetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "key_encrypted_asym", columnDefinition = Constantes.TEXT)
    private String encriptedKey;

    @Column(name = "user_name")
    private String username;

    @ManyToOne
    @JoinColumn(name = "symetric_id", referencedColumnName = "id")
    private Symmetric symmetric;
}