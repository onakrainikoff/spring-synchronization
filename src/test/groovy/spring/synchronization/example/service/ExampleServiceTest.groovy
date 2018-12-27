package spring.synchronization.example.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification
import spring.synchronization.example.ApplicationConfiguration
import spring.synchronization.example.repository.ClientRepository

import java.util.concurrent.CyclicBarrier
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * Тест, воспроизводящий сутуацию гонки запросов
 *
 * @author uchonyy@gmail.com
 *
 */
@SpringBootTest()
@ContextConfiguration(classes = ApplicationConfiguration.class)
class ExampleServiceTest extends Specification {
    @Autowired
    ExampleService exampleService
    @Autowired
    ClientRepository clientRepository

    ExecutorService executor = Executors.newCachedThreadPool()
    @Shared RedisServer redisServer = new RedisServer(6378)

    def setupSpec(){
        redisServer.start()
    }

    def cleanupSpec(){
        redisServer.stop()
    }

    def cleanup(){
        clientRepository.deleteAll()
    }


    def "Test example1"() {
        given:
        def barrier = new CyclicBarrier(5);
        when:
        def r1 = executor.submit({barrier.await(); exampleService.example1("clientId1", "requestId1")})
        def r2 = executor.submit({barrier.await(); exampleService.example1("clientId1", "requestId2")})
        def r3 = executor.submit({barrier.await(); exampleService.example1("clientId2", "requestId1")})
        def r4 = executor.submit({barrier.await(); exampleService.example1("clientId1", "requestId3")})
        def r5 = executor.submit({barrier.await(); exampleService.example1("clientId2", "requestId2")})
        r1.get()
        r2.get()
        r3.get()
        r4.get()
        r5.get()
        then:
        thrown(ExecutionException)

    }

    def "Test example2"() {
        given:
        def barrier = new CyclicBarrier(5);
        when:
        def r1 = executor.submit({barrier.await(); exampleService.example2("clientId1", "requestId1")})
        def r2 = executor.submit({barrier.await(); exampleService.example2("clientId1", "requestId2")})
        def r3 = executor.submit({barrier.await(); exampleService.example2("clientId2", "requestId1")})
        def r4 = executor.submit({barrier.await(); exampleService.example2("clientId1", "requestId3")})
        def r5 = executor.submit({barrier.await(); exampleService.example2("clientId2", "requestId2")})
        r1.get()
        r2.get()
        r3.get()
        r4.get()
        r5.get()
        then:
        noExceptionThrown()
    }

    def "Test example3"() {
        given:
        def barrier = new CyclicBarrier(5);
        when:
        def r1 = executor.submit({barrier.await(); exampleService.example3("clientId1", "requestId1")})
        def r2 = executor.submit({barrier.await(); exampleService.example3("clientId1", "requestId2")})
        def r3 = executor.submit({barrier.await(); exampleService.example3("clientId2", "requestId1")})
        def r4 = executor.submit({barrier.await(); exampleService.example3("clientId1", "requestId3")})
        def r5 = executor.submit({barrier.await(); exampleService.example3("clientId2", "requestId2")})
        r1.get()
        r2.get()
        r3.get()
        r4.get()
        r5.get()
        then:
        noExceptionThrown()
    }

    def "Test example4"() {
        given:
        def barrier = new CyclicBarrier(5);
        when:
        def r1 = executor.submit({barrier.await(); exampleService.example4("clientId1", "requestId1")})
        def r2 = executor.submit({barrier.await(); exampleService.example4("clientId1", "requestId2")})
        def r3 = executor.submit({barrier.await(); exampleService.example4("clientId2", "requestId1")})
        def r4 = executor.submit({barrier.await(); exampleService.example4("clientId1", "requestId3")})
        def r5 = executor.submit({barrier.await(); exampleService.example4("clientId2", "requestId2")})
        r1.get()
        r2.get()
        r3.get()
        r4.get()
        r5.get()
        then:
        noExceptionThrown()
    }

    def "Test example5"() {
        given:
        def barrier = new CyclicBarrier(5);
        when:
        def r1 = executor.submit({barrier.await(); exampleService.example5("clientId1", "requestId1")})
        def r2 = executor.submit({barrier.await(); exampleService.example5("clientId1", "requestId2")})
        def r3 = executor.submit({barrier.await(); exampleService.example5("clientId2", "requestId1")})
        def r4 = executor.submit({barrier.await(); exampleService.example5("clientId1", "requestId3")})
        def r5 = executor.submit({barrier.await(); exampleService.example5("clientId2", "requestId2")})
        r1.get()
        r2.get()
        r3.get()
        r4.get()
        r5.get()
        then:
        noExceptionThrown()
    }

}
