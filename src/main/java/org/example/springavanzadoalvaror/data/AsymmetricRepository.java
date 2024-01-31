package org.example.springavanzadoalvaror.data;

import org.example.springavanzadoalvaror.common.Constantes;
import org.example.springavanzadoalvaror.data.modelo.Asymmetric;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsymmetricRepository extends ListCrudRepository<Asymmetric, Integer> {

    Asymmetric findBySymmetricIdAndUsername(Integer symmetricId, String username);
    List<Asymmetric> findBySymmetricId(long symmetricId, Sort sort);
    List<Asymmetric> findByUsername(String username);

    @Query(Constantes.QUERY)
    List<String> findUsersWithoutAsymmetricBySymmetricId(@Param(Constantes.SYMMETRIC_ID) long symmetricId);
}
