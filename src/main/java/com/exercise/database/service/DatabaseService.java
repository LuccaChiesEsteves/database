package com.exercise.database.service;

import java.util.List;

public interface DatabaseService {
    /**
     * Lookup a specific row in the database, based on its unique id
     *
     * @ param id The unique id of the entry to lookup, or -1 to get the
     * whole database contents ( i.e.: select * from...)
     * @ return The entire row found ( e.g.: if one uses a CSV file as an
     * underlying storage format, the entire comma-separated line is
     * returned) or a list of rows for id = -1
     */
    List<String> select(Long id);
    /**
     * Insert a new row in the database. The unique id of this new entry is
     * generated and returned
     * @ param row The row' s data to insert ( e. g.: comma- separated values -
     * for CSV)
     * @ return The generated unique id
     */
    Long insert(String row);
    /**
     * " Update" a specific row. The " update" means replacing the current row
     * with the one passed as a parameter
     * @ param id The unique id of the row to " update"
     * @ param newRow The new row' s data
     * @ return True if the update succeeded, false otherwise ( e.g.: the id
     * hasn't been found)
     */
    boolean update(Long id, String newRow);
    /**
     * Delete a specific row in the database
     * @ param id The unique id of the row to delete
     * @ return True if the update succeeded, false otherwise ( e. g.: the id
     * hasn't' t been found)
     */
    boolean delete(Long id);
}
