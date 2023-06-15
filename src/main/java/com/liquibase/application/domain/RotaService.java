package com.liquibase.application.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class RotaService {
    private static final String ROTA_ENTITY_TYPE = "rota";
    private static final String NAME_FIELD = "name";

    private final RotaRepo rotaRepo;
    private final CreateBucketService createBucketService;


    public Mono<Rota> findRotaById(String rotaId) {
        return rotaRepo.findById(rotaId);
    }

    public Mono<Rota> save(Rota rota) {
        createBucketService.createBucket();
        return rotaRepo.save(rota);
    }

}
