package com.liquibase.application.domain;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

/**
 * Represents entity of Rota (rotation) for depot drivers.
 */
@Document
@Data
public class Rota {

    @Id
    private String id;

    @NotBlank(message = "rota name must not be blank")
    private String name;
}
