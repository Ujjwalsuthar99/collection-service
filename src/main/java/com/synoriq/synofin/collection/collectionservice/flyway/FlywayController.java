package com.synoriq.synofin.collection.collectionservice.flyway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
public class FlywayController {

    private final FlywayService flywayService;

    public FlywayController(FlywayService flywayService) {
        this.flywayService = flywayService;
    }

    /**
     * API to migrate the schema for all the clients
     * @return
     */
    @GetMapping("/migrate")
    public ResponseEntity<Object> migrate() {
        try {
            flywayService.migrateAllClient();
            return ResponseEntity.ok("Flyway Service Executed Successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
