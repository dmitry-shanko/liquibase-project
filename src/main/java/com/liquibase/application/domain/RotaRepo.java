package com.liquibase.application.domain;

import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RotaRepo extends ReactiveCouchbaseRepository<Rota, String> {

}
