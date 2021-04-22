package name.pali.scorepion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisScript<Void> script(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("classpath:add-then-cap.lua");
        return RedisScript.of(resource, Void.class);
    }
}

