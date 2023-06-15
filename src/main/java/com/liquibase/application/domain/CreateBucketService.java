package com.liquibase.application.domain;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CreateBucketService {

    public void createBucket() {
        String name = UUID.randomUUID().toString();
    }
}
