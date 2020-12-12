package com.github.ritikmishra.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller("/dep1")
@PermitAll
public class DependentApiOneController {
    private static final Logger log = LoggerFactory.getLogger(DependentApiOneController.class);
    private static final String onlyValidAccessToken = "applebanana token";

    @Post("/auth")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Single<TokenResponse> authenticateOauthToken(HttpRequest<?> request) {
        log.info("Someone is trying to authenticate with dependent api 1");
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent()) {
            TokenResponse ret = new TokenResponse();
            ret.setAccessToken(onlyValidAccessToken);
            ret.setTokenType("bearer");
            ret.setExpiresIn(3600);
            log.info("They succeeded in authing dep 1!");
            return Single.just(ret);
        }
        log.info("They failed to auth dep1 :(");
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong 1a"));
    }

    @Post("/resource1")
    @Produces(MediaType.TEXT_PLAIN)
    public Single<String> getResource(HttpRequest<?> request) {
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent() && authHeader.get().contains(onlyValidAccessToken)) {
            log.info("Successfully gave away dep 1 res 1");
            return Single.just("tada! here is resource 1 tyvm");
        }
        log.info("no auth header on dep 1 resource 1");
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong 1b"));
    }

    @Post("/resource2")
    @Produces(MediaType.TEXT_PLAIN)
    public Single<String> getOtherResource(HttpRequest<?> request) {
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent() && authHeader.get().contains(onlyValidAccessToken)) {
            log.info("Successfully gave away dep 1 res 2");
            return Single.timer(200, TimeUnit.MILLISECONDS).map((a) -> "choo choo! dep 1 resource 2 ready for you");
        }
        log.info("no auth header on dep 1 resource 2");
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong 1c"));
    }
}
