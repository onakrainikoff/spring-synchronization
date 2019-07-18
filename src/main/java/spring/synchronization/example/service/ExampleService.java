package spring.synchronization.example.service;


import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.synchronization.example.repository.Client;
import spring.synchronization.example.repository.ClientRepository;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Сервис, демонстрирующий различные варианты синхронизаций запросов
 *
 * @author uchonyy@gmail.com
 *
 */
@Service
@Slf4j
public class ExampleService {
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RedissonClient redissonClient;
    private final ConcurrentMap<String, ReentrantLock> locks = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterWrite(Duration.ofSeconds(2)).<String, ReentrantLock>build().asMap();;

    /**
     *
     * Без синхронизации
     *
     * в данном примере демонстрирующий ситуация, когда несколько запросов (потоков)
     * одного клиента начнут создавать сущность Client и получат sql ошибку дубликации
     * т.к. поле clientId является unique
     *
     */
    public void example1(String clientId, String requestId){
        log.info("Начинаем обработку запроса requestId={} clientId={}", requestId, clientId);
        Client client = clientRepository.findByClientId(clientId);
        if(client == null){
            log.info("Создаем клиента: requestId={} clientId={}", requestId, requestId);
            client = clientRepository.save(new Client(clientId));
            log.info("Клиент успешно создан: requestId={} client={}", requestId, client);
        }
        log.info("Закончена обработка запроса requestId={} client={}", requestId, client);
    }

    /**
     *
     * Полная синхронизация
     *
     * в данном примере создание сущности Client происходит после синхронизации;
     * но блокируются все запросы (потоки), которым нужно выполнить создание,
     * даже если они будут создавать Client с разными clientId и никак друг с другом бы не конкурировали
     *
     */
    public void example2(String clientId, String requestId){
        log.info("Начинаем обработку запроса requestId={} clientId={}", requestId, clientId);
        Client client = clientRepository.findByClientId(clientId);
        if(client == null){
            synchronized (this){
                client = clientRepository.findByClientId(clientId);
                if(client == null){
                    log.info("Создаем клиента: requestId={} clientId={}", requestId, requestId);
                    client = clientRepository.save(new Client(clientId));
                    log.info("Клиент успешно создан: requestId={} client={}", requestId, client);
                }
            }
        }
        log.info("Закончена обработка запроса requestId={} client={}", requestId, client);
    }

    /**
     *
     * Синхронизация по clientId. Вариант 1.
     *
     * в данном примере синхронизация запросов делается для конкретного клиента, не блокируя запросы остальных;
     * для синхронизации используем конструкцию synchronized, передавая в нее в качестве объекта id клиента,
     * который получаем из стандартного пула строк;
     *
     */
    public void example3(String clientId, String requestId){
        log.info("Начинаем обработку запроса requestId={} clientId={}", requestId, clientId);
        Client client = clientRepository.findByClientId(clientId);
        if(client == null){
            synchronized (clientId.intern()){
                client = clientRepository.findByClientId(clientId);
                if(client == null){
                    log.info("Создаем клиента: requestId={} clientId={}", requestId, requestId);
                    client = clientRepository.save(new Client(clientId));
                    log.info("Клиент успешно создан: requestId={} client={}", requestId, client);
                }
            }
        }
        log.info("Закончена обработка запроса requestId={} client={}", requestId, client);
    }

    /**
     *
     * Синхронизация по clientId. Вариант 2.
     *
     * в данном примере синхронизация запросов делается для конкретного клиента, не блокируя запросы остальных;
     * для синхронизации используем ReentrantLock, получаемые из пула, которым вычтупает ConcurrentHashMap;
     *
     */
    public void example4(String clientId, String requestId) throws ExecutionException {
        log.info("Начинаем обработку запроса requestId={} clientId={}", requestId, clientId);
        Client client = clientRepository.findByClientId(clientId);
        if(client == null){
            ReentrantLock lock = locks.computeIfAbsent(clientId, (k) -> new ReentrantLock());
            lock.lock();
            try{
                client = clientRepository.findByClientId(clientId);
                if(client == null){
                    log.info("Создаем клиента: requestId={} clientId={}", requestId, requestId);
                    client = clientRepository.save(new Client(clientId));
                    log.info("Клиент успешно создан: requestId={} client={}", requestId, client);
                }
            } finally {
                lock.unlock();
            }
        }
        log.info("Закончена обработка запроса requestId={} client={}", requestId, client);
    }

    /**
     *
     * Синхронизация по clientId. Вариант 3.
     *
     * в данном примере синхронизация запросов делается для конкретного клиента, не блокируя запросы остальных;
     * для синхронизации используем Redisson, в кчачестве пула он использует redis;
     *
     */
    public void example5(String clientId, String requestId){
        log.info("Начинаем обработку запроса requestId={} clientId={}", requestId, clientId);
        Client client = clientRepository.findByClientId(clientId);
        if(client == null){
            RLock lock = redissonClient.getFairLock(clientId);
            lock.lock();
            try{
                client = clientRepository.findByClientId(clientId);
                if(client == null){
                    log.info("Создаем клиента: requestId={} clientId={}", requestId, requestId);
                    client = clientRepository.save(new Client(clientId));
                    log.info("Клиент успешно создан: requestId={} client={}", requestId, client);
                }
            } finally {
                lock.unlock();
            }
        }
        log.info("Закончена обработка запроса requestId={} client={}", requestId, client);
    }
}
