package ro.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by sconstantinescu on 09.06.2016.
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DemoController {

    @RequestMapping(value="/hello/{name}", method = RequestMethod.GET)
    public String hello(@PathVariable String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "/ip", method = RequestMethod.GET)
    public Map ip() throws IOException {
        URL url = new URL("https://api.ipify.org/");
        Map m = new HashMap<>();
        InputStream is = url.openStream();
        String content = new BufferedReader(new InputStreamReader(url.openStream())).lines().collect(Collectors.joining("\n"));
        m.put("content", content);
        m.put("date", new Date());
        m.put("server", "api.ipify.org");
        return m;
    }
}
