package my.edu.utar.group40_elderlypals;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

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

            Executors.newSingleThreadExecutor().execute(() -> {
                HealthVaultDatabase database = INSTANCE;

                database.healthCardDao().insertOrUpdate(new HealthCard("Ah Hong", "012-3456789",
                        "4001, Jalan Sekysen 4/3, Kampar, Perak", false));

                database.medicationDao().insert(new Medication("Vitamin C", "08:00 AM", "Yellow"));
                database.medicationDao().insert(new Medication("Panadol", "02:00 PM", "Red"));

                database.moodLogDao().insert(new MoodLog(4, "09:00"));
                database.moodLogDao().insert(new MoodLog(2, "13:00"));
            });
        }
    };
}
