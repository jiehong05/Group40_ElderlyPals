package my.edu.utar.group40_elderlypals;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Medication.class, MoodLog.class, HealthCard.class}, version = 1, exportSchema = false)
public abstract class HealthVaultDatabase extends RoomDatabase {

    public abstract MedicationDao medicationDao();
    public abstract MoodLogDao moodLogDao();
    public abstract HealthCardDao healthCardDao();

    private static volatile HealthVaultDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static HealthVaultDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (HealthVaultDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HealthVaultDatabase.class, "health_vault_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
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

            databaseWriteExecutor.execute(() -> {
                MedicationDao medDao = INSTANCE.medicationDao();
                MoodLogDao moodDao = INSTANCE.moodLogDao();
                HealthCardDao healthDao = INSTANCE.healthCardDao();

                medDao.insert(new Medication("Panadol", "09:00 AM", false));
                medDao.insert(new Medication("Vitamin C", "02:00 PM", true));

                moodDao.insert(new MoodLog("2026-04-20", "Happy", "Today I talked to my grandson"));

                healthDao.insert(new HealthCard("John Doe (Son)", "012-3456789", "O+", "Peanuts (花生)"));
            });
        }
    };
}
