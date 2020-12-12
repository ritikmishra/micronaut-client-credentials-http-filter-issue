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
public class DependentApiController {
    private static final Logger log = LoggerFactory.getLogger(DependentApiController.class);
    private static final String onlyValidAccessToken = "applebanana token";

    /**
     * Dummy api endpoint to authenticate with OAuth2 Client Credentials
     * @param request http request with client id, secret encoded using Basic authentication in header
     * @return the access token
     */
    @Post("/auth")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Single<TokenResponse> authenticateOauthToken(HttpRequest<?> request) {
        log.info("Processing authentication request with dependent API");
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent()) {
            TokenResponse ret = new TokenResponse();
            ret.setAccessToken(onlyValidAccessToken);
            ret.setTokenType("bearer");
            ret.setExpiresIn(3600);

            log.info("dependent API successfully authenticated us");
            return Single.just(ret);
        }
        log.info("Authentication with API failed");
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong (1a)"));
    }

    /**
     * A dummy resource that requires the access token to access
     * @param request request with Bearer token
     * @return the resource that needed authentication
     */
    @Post("/resource1")
    @Produces(MediaType.TEXT_PLAIN)
    public Single<String> getResource(HttpRequest<?> request) {
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent() && authHeader.get().contains(onlyValidAccessToken)) {
            return Single.just("tada! here is resource 1 tyvm");
        }
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong (1b)"));
    }

    @Post("/resource2")
    @Produces(MediaType.TEXT_PLAIN)
    public Single<String> getOtherResource(HttpRequest<?> request) {
        Optional<String> authHeader = request.getHeaders().getAuthorization();
        if (authHeader.isPresent() && authHeader.get().contains(onlyValidAccessToken)) {
            // The presence of a non-zero delay seems important. If its removed, the errors appear less frequently.
            // Sadly, in my real application, the delay is a result of network latency/other unavoidable factors.
            return Single.timer(200, TimeUnit.MILLISECONDS).map((a) -> "choo choo! resource 2 ready for you");
        }
        return Single.error(new HttpStatusException(HttpStatus.UNAUTHORIZED, "auth missing or wrong (1c)"));
    }
}
