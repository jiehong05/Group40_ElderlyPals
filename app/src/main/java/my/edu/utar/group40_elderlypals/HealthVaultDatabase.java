package my.edu.utar.group40_elderlypals;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Medication.class, MoodLog.class, HealthCard.class}, version = 1, exportSchema = false)
public abstract class HealthVaultDatabase extends RoomDatabase {

    public abstract MedicationDao medicationDao();
    public abstract MoodLogDao moodLogDao();
    public abstract HealthCardDao healthCardDao();

    private static volatile HealthVaultDatabase INSTANCE;

    public static HealthVaultDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (HealthVaultDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HealthVaultDatabase.class, "health_vault_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
