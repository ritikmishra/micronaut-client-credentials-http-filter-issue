package com.github.ritikmishra.clients;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Client("http://localhost:8080/dep2")
public interface DepApiTwoClient {
    @Post("/resource1")
    @Consumes(MediaType.TEXT_PLAIN)
    Single<String> getResource();
}
