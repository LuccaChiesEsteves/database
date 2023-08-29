package com.exercise.database.service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.exercise.database.dto.DatabaseEntryDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.Synchronized;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    @Value("${db.entry.path}")
    private String DB_ENTRY_PATH;

    @Value("${db.entry.seq.path}")
    private String DB_ENTRY_SEQ_PATH;
    @Override
    public List<String> select(Long id) {
        List<DatabaseEntryDTO> entries = loadEntries();
        if(id == -1) {
            return entries.stream().map(DatabaseEntryDTO::row).collect(Collectors.toList());
        } else {
            return Collections.singletonList(entries.stream()
                    .filter(entry -> entry.id() == id).findFirst().map(DatabaseEntryDTO::row).orElseThrow(() -> new EntityNotFoundException("Entry not found")));
        }
    }

    @Override
    @Synchronized
    public Long insert(String row) {
        DatabaseEntryDTO entryDTO = new DatabaseEntryDTO(getNextEntryId(), row);
        List<DatabaseEntryDTO> entries = loadEntries();
        entries.add(entryDTO);
        saveEntries(entries);
        return entryDTO.id();
    }

    @Override
    @Synchronized
    public boolean update(Long id, String newRow) {
        List<DatabaseEntryDTO> entries = loadEntries();
        if(entries.stream().noneMatch(entry -> entry.id() == id)) {
            return false;
        }
        DatabaseEntryDTO updatedEntry = new DatabaseEntryDTO(id, newRow);
        entries.replaceAll(dto -> dto.id() == id ? updatedEntry : dto);
        saveEntries(entries);
        return true;
    }

    @Override
    @Synchronized
    public boolean delete(Long id) {
        List<DatabaseEntryDTO> entries = loadEntries();
        if(entries.stream().noneMatch(entry -> entry.id() == id)) {
            return false;
        }
        entries.removeIf(dto -> dto.id() == id);
        saveEntries(entries);
        return true;
    }

    private List<DatabaseEntryDTO> loadEntries() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(DB_ENTRY_PATH));
            List<DatabaseEntryDTO> entries = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                DatabaseEntryDTO entryDTO = objectMapper.treeToValue(node, DatabaseEntryDTO.class);
                entries.add(entryDTO);
            }
            return entries;
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred while accessing database");
        }
    }

    private JSONArray convertDtoListToJsonArray(List<DatabaseEntryDTO> entries) {
        JSONArray jsonArray = new JSONArray();
        for (DatabaseEntryDTO dto : entries) {
            JSONObject jsonDto = new JSONObject();
            jsonDto.put("id", dto.id());
            jsonDto.put("row", dto.row());
            jsonArray.put(jsonDto);
        }
        return jsonArray;
    }

    @Synchronized
    private long getNextEntryId() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_ENTRY_SEQ_PATH))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            String jsonString = content.toString();
            JSONObject jsonObject = new JSONObject(jsonString);
            long nextId = jsonObject.getLong("nextEntryId");
            updateNextEntryId(nextId);
            return nextId;
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred while accessing entry sequence");
        }
    }

    private void saveEntries(List<DatabaseEntryDTO> entries) {
        writeToFile(convertDtoListToJsonArray(entries).toString(), DB_ENTRY_PATH);
    }

    @Synchronized
    private void updateNextEntryId(long currentId) {
        JSONObject updatedValue = new JSONObject();
        updatedValue.put("nextEntryId", currentId + 1);
        writeToFile(updatedValue.toString(), DB_ENTRY_SEQ_PATH);
    }
    private void writeToFile(String content, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred while saving to database");
        }
    }
}
