package com.github.ritikmishra.controllers;

import com.github.ritikmishra.clients.DependentApiClient;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import java.util.Map;

@Controller
@PermitAll
public class PrimaryController {

    private static final Logger log = LoggerFactory.getLogger(PrimaryController.class);

    @Inject
    DependentApiClient dependentApiClient;

    @Get
    @Produces(MediaType.TEXT_HTML)
    public Single<String> getJavascriptThatCausesIssue() {
        String retHtml = "<body>" +
                "<p>hey there! open up the dev console in the browser to see the network requests failing</p> " +
                "<p>normally at least 1 of the 10 requests made errors out, but if not, reload the page</p> " +
                "<script>" +
                "   for(let i = 0; i < 10; i++) {" +
                "   fetch('http://localhost:8080/api/produces_error');" +
                "   }" +
                "</script>" +
                "</body>";
        return Single.just(retHtml);
    }

    /**
     * This endpoint gives a 500 internal server error usually 1-3 times every 10 requests;
     *
     * @return Some dummy data for the sake of demonstrating the issue
     */
    @Get("/api/produces_error")
    @Produces(MediaType.APPLICATION_JSON)
    public Single<Map<String, String>> returnCalculatedInformationBasedOffOfTwoResources() {
        log.info("Get request for information, requesting both resources from the api client");
        return Single.zip(
                dependentApiClient.getResource(),
                dependentApiClient.getOtherResource(),
                (res1, res2) -> Map.of("resource1", res1, "resource2", res2)
        );
    }
}
