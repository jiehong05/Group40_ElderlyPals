package my.edu.utar.group40_elderlypals;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface MedicationDao {

    @Insert
    void insert(Medication medication);

    @Query("SELECT * FROM medication_table ORDER BY id DESC")
    List<Medication> getAllMedications();

    @Update
    void update(Medication medication);

    @Delete
    void delete(Medication medication);

    @Query("SELECT * FROM medication_table WHERE medName = :name")
    List<Medication> findMedicationByName(String name);
}
