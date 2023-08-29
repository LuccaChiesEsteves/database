package com.exercise.database.controller;

import com.exercise.database.service.DatabaseService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(path = "/entries")
@AllArgsConstructor
@Validated
public class EntryController {

    private final DatabaseService databaseService;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getDatabaseEntry(@PathVariable long id) {
        return databaseService.select(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long insertDatabaseEntry(@RequestBody @NotBlank String row) {
        return databaseService.insert(row);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean updateDatabaseEntry(@PathVariable long id, @RequestBody @NotBlank String row) {
        return databaseService.update(id, row);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteDatabaseEntry(@PathVariable long id) {
        return databaseService.delete(id);
    }
}
