package spring.synchronization.example;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

/**
 *
 * Конфигурация Spring Boot app
 * @author uchonyy@gmail.com
 *
 */
@SpringBootApplication
@EnableJpaRepositories
public class ApplicationConfiguration {

    /**
     *
     * Конфигурация
     *
     */
    @Bean
    public RedissonClient redissonClient(LettuceConnectionFactory lettuceConnectionFactory){
        Config config = new Config();
        config.setLockWatchdogTimeout(60*1000);
        SingleServerConfig singleServerConfig = config.useSingleServer();
        String schema = lettuceConnectionFactory.isUseSsl() ? "rediss://" : "redis://";
        singleServerConfig.setAddress(schema + lettuceConnectionFactory.getHostName() + ":" + lettuceConnectionFactory.getPort());
        singleServerConfig.setDatabase(lettuceConnectionFactory.getDatabase());
        if (!StringUtils.isEmpty(lettuceConnectionFactory.getPassword())) {
            singleServerConfig.setPassword(lettuceConnectionFactory.getPassword());
        }
        return Redisson.create(config);
    }
}
