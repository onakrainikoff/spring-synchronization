package spring.synchronization.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * Репозиторий сущности Client
 * @see Client
 *
 * @author uchonyy@gmail.com
 *
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer>{
    Client findByClientId(String clientId);
}