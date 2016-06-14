package ro.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.discovery.InstanceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.export.MetricExportProperties;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;

//@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "ro.demo")
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})
@EnableTurbine   //http://127.0.0.1:8080/turbine.stream
@EnableHystrixDashboard  //http://127.0.0.1:8080/hystrix
@EnableHystrix // http://127.0.0.1:8081/management/hystrix.stream
@EnableCircuitBreaker
@EnableDiscoveryClient
public class DemoApplication {
    static Logger logger= LoggerFactory.getLogger(DemoApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public  Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(){
        Jackson2ObjectMapperBuilder builder=new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return builder;
    }


    @Bean
    @ExportMetricWriter
    public MetricWriter metricWriter(MetricExportProperties export, RedisConnectionFactory connectionFactory) {
        return new RedisMetricRepository(connectionFactory,
                export.getRedis().getPrefix(), export.getRedis().getKey());
    }

    @Bean
    @LoadBalanced()
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public CustomInst instanceDiscovery(){
        return new CustomInst();
    }
    public static class CustomInst implements InstanceDiscovery {
        @Autowired
        DiscoveryClient discoveryClient;
        @Value("${turbine.appConfig}")
        String appConfig;
        @Override
        public Collection<Instance> getInstanceList() throws Exception {
            Collection<Instance> instances=new ArrayList<Instance>();
            for (	ServiceInstance s : discoveryClient.getInstances(appConfig)) {
                Instance instance = new Instance(s.getHost(), appConfig, true);
                instances.add(instance);
            }
            return instances;
        }
    }


    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("chat"));
        return container;
    }
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(86400);

        return cacheManager;
    }
    @Bean
    @Autowired
    public RedisCache redisCache(CacheManager cacheManager){
        RedisCache cache = (RedisCache) cacheManager.getCache("default");
        return cache;
    }

    public static class Receiver {

        public void receiveMessage(String message) {
            logger.info("Received <" + message + ">");
        }
    }
}