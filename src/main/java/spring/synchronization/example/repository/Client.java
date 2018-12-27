package spring.synchronization.example.repository;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * Сущность - клиент.
 * поле clientId является уникальным
 *
 * @author uchonyy@gmail.com
 *
 */
@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "clientId", unique = true)
    private String clientId;

    @Basic(optional = false)
    @Column(name = "date")
    private LocalDateTime date = LocalDateTime.now();

    public Client(String clientId) {
        this.clientId = clientId;
    }
}
