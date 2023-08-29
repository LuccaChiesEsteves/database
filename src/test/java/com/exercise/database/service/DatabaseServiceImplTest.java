package com.exercise.database.service;


import jakarta.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileWriter;
import java.io.IOException;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class DatabaseServiceImplTest {

    private static final String DB_ENTRY_PATH = "src/test/resources/database/databaseEntry.json";
    private static final String DB_ENTRY_SEQ_PATH = "src/test/resources/database/databaseEntrySeq.json";

    @Autowired
    private DatabaseServiceImpl databaseService;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(databaseService, "DB_ENTRY_PATH", "src/test/resources/database/databaseEntry.json");
        ReflectionTestUtils.setField(databaseService, "DB_ENTRY_SEQ_PATH", "src/test/resources/database/databaseEntrySeq.json");
        resetNextEntryId();
        resetDatabaseEntries();
    }

    @Test
    void insertValidBodyIdReturned() {
        long result = databaseService.insert("valid body");
        assertEquals(1, result);
    }

    @Test
    void getNextEntryIdEntryInsertedIdUpdated() {
        long firstId = databaseService.insert("test1");
        long secondId = databaseService.insert("test2");
        assertEquals(1L, firstId);
        assertEquals(2L, secondId);
    }

    @Test
    void updateIdNotFoundFalseReturned() {
        boolean result = databaseService.update(1L, "false returned");
        assertFalse(result);
    }

    @Test
    void updateIdFoundTrueReturned() {
        databaseService.insert("test");
        boolean result = databaseService.update(1L, "updated value");
        assertTrue(result);
        List<String> updatedValue = databaseService.select(1L);
        assertThat(updatedValue.get(0)).isEqualTo("updated value");
    }

    @Test
    void deleteIdNotFoundFalseReturned() {
        boolean result = databaseService.delete(1L);
        assertFalse(result);
    }

    @Test
    void deleteIdFoundTrueReturned() {
        databaseService.insert("test");
        boolean result = databaseService.delete(1L);
        assertTrue(result);
        assertThrows(EntityNotFoundException.class, () -> databaseService.select(1L));
    }



    private void resetNextEntryId() {
        JSONObject updatedValue = new JSONObject();
        updatedValue.put("nextEntryId", 1);
        writeToFile(updatedValue.toString(), DB_ENTRY_SEQ_PATH);
    }

    private void resetDatabaseEntries() {
        writeToFile("", DB_ENTRY_PATH);
    }

    private void writeToFile(String content, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred while saving to database");
        }
    }

}