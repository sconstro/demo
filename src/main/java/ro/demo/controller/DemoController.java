package ro.demo.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import ro.demo.PersonResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sconstantinescu on 09.06.2016.
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DemoController {
@Autowired
    CounterService counter;
    @Autowired
    StringRedisTemplate template;



    @Autowired
    RestTemplate restTemplate;
    @RequestMapping(value="/hello/{name}", method = RequestMethod.GET)

    public String hello(@PathVariable String name) {
//        template.boundValueOps("aaa").set("bbb");
        counter.increment("histogram.hello1");
        HystrixCommand<String> hystrix = new HystrixCommand<String>(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("databaseGroup")).andCommandKey(HystrixCommandKey.Factory.asKey("demo"))) {
            @Override
            protected String run() throws Exception {
                Thread.sleep(1000);
                return "test";
            }
        };
        String a= hystrix.execute();
        return "Hello " + name+" Result is "+a;
    }

    @Value("${application.url}")
    String urlStr;
    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public Map ip() throws IOException {
        URL url = new URL(urlStr);
        Map m = new HashMap<>();
        InputStream is = url.openStream();
        String content = new BufferedReader(new InputStreamReader(url.openStream())).lines().collect(Collectors.joining("\n"));
        m.put("content", content);
        m.put("date", new Date());
        m.put("server", "api.ipify.org");
        return m;
    }


    /**
     * Consul : consul agent -dev -bind 127.0.0.1
     * Consul UI: http://127.0.0.1:8500/ui/#/dc1/services
     */
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @RequestMapping(value = "/test-discovery", method = RequestMethod.GET)
    public DeferredResult<String> testDiscovery()  {
        DeferredResult ret = new DeferredResult();
//        ResponseEntity<PersonResource> result = restTemplate.getForEntity("http://127.0.0.1:8080/person/178", PersonResource.class);
        ResponseEntity<PersonResource> result = restTemplate.getForEntity("http://demo/person/178", PersonResource.class);
        if (result.getStatusCode().is2xxSuccessful()) {
            ret.setResult(result);
        }
        return ret;
    }
    @RequestMapping(value="/person/{cnp}", method = RequestMethod.GET)
    public PersonResource getPerson(@PathVariable String cnp) {

        return new PersonResource(1,"178");
    }
}
