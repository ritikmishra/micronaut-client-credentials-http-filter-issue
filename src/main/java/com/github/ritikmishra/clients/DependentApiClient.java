package com.github.ritikmishra.clients;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;

/**
 * An HTTP client for interfacing with another API that this micronaut application depends on
 *
 * For the sake of making this demonstration self-contained, the "other api" is inside this micronaut application.
 */
@Client("http://localhost:8080/dep1")
public interface DependentApiClient {
    @Post("/resource1")
    @Consumes(MediaType.TEXT_PLAIN)
    Single<String> getResource();

    @Post("/resource2")
    @Consumes(MediaType.TEXT_PLAIN)
    Single<String> getOtherResource();
}
