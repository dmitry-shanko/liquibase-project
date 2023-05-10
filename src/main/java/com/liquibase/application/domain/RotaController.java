package com.liquibase.application.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rotas", produces = "application/json")
public class RotaController {
    private final RotaService rotaService;

    @GetMapping(value = "/{rotaId}")
    public Mono<Rota> getRota(@PathVariable("rotaId") String rotaId) {
        return rotaService.findRotaById(rotaId);
    }

    @PostMapping
    public Mono<Rota> getRota(@RequestBody Rota rota) {
        return rotaService.save(rota);
    }
}
