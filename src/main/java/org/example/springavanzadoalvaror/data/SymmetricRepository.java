package org.example.springavanzadoalvaror.data;

import org.example.springavanzadoalvaror.data.modelo.Symmetric;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SymmetricRepository extends ListCrudRepository<Symmetric, Integer> {

    Symmetric findByProgramName(String programName);
}
