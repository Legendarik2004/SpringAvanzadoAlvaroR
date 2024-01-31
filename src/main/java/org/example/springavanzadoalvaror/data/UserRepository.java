package org.example.springavanzadoalvaror.data;

import org.example.springavanzadoalvaror.data.modelo.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends ListCrudRepository<User, Integer> {
    User findByName(String name);
}
