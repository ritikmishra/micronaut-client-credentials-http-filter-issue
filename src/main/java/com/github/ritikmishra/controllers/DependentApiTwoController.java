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

@Controller("/dep2")
@PermitAll
public class DependentApiTwoController {
    private static final Logger log = LoggerFactory.getLogger(DependentApiTwoController.class);
    private static final String onlyValidAccessToken = "orangegrape token";

    @Post("/auth")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Single<TokenResponse> authenticateOauthToken(HttpRequest<?> request) {
        log.info("Someone is trying to authenticate with dependent api 2");
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent()) {
            TokenResponse ret = new TokenResponse();
            ret.setAccessToken(onlyValidAccessToken);
            ret.setTokenType("bearer");
            ret.setExpiresIn(3600);
            log.info("They succeeded in authing dep 2!");
            return Single.just(ret);
        }
        log.info("They failed to auth dep2 :(");
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong 2a"));
    }

    @Post("/resource1")
    @Produces(MediaType.TEXT_PLAIN)
    public Single<String> getResource(HttpRequest<?> request) {
        Optional<String> authHeader = request.getHeaders().getAuthorization();
//        if (authHeader.isPresent() && authHeader.get().contains(onlyValidAccessToken)) {
            log.info("Successfully gave away dep 2 res 1");
            return Single.timer(500, TimeUnit.MILLISECONDS).map((delay) -> "woah there cowboy look at you go using all these apis");
//        }
//        log.info("no auth header on dep 2 resource");
//        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong 2b"));
    }
}
