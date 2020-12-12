package com.github.ritikmishra.controllers;

import com.github.ritikmishra.clients.DepApiOneClient;
import com.github.ritikmishra.clients.DepApiTwoClient;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import java.util.Map;

@Slf4j
@Controller("/api")
@PermitAll
public class PrimaryController {

    @Inject
    DepApiOneClient depApiOneClient;

    @Inject
    DepApiTwoClient depApiTwoClient;

    @Get("/apple")
    @Produces(MediaType.TEXT_HTML)
    public Single<String> getWebpage() {
        return Single.just("<body>" +
                "<p>hey there!</p> " +
                "<script>for(let i = 0; i < 10; i++) {fetch('http://localhost:8080/api/main');" +
                "fetch('http://localhost:8080/api/main2');}" +
                "</script>" +
                "</body>");
    }

    @Get("/main2")
    @Produces(MediaType.APPLICATION_JSON)
    public Single<Map<String, String>> getInformation2() {
        log.info("Get information hit, requesting data from deps 1 and 2");
        return Single.zip(depApiOneClient.getResource(), depApiOneClient.getOtherResource(), (res1, res2) -> {
            return Map.of("dep1b", res1, "dep1a", res2);
        });
//        return depApiOneClient.getResource().map(v -> Map.of("papaya", v));
    }

    @Get("/main")
    @Produces(MediaType.APPLICATION_JSON)
    public Single<Map<String, String>> getInformation() {
        log.info("Get information hit, requesting data from deps 1 and 2");
        return Single.zip(depApiOneClient.getResource(), depApiTwoClient.getResource(), (res1, res2) -> {
            return Map.of("dep1", res1, "dep2", res2);
        });
//        return depApiOneClient.getResource().map(v -> Map.of("papaya", v));
    }
}
