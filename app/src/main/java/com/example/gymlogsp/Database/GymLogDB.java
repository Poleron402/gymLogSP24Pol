package com.example.gymlogsp.Database;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gymlogsp.Database.entities.GymLog;
import com.example.gymlogsp.Database.entities.User;
import com.example.gymlogsp.Database.typeConverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@TypeConverters(LocalDateTypeConverter.class)
@Database(entities = {GymLog.class, User.class}, version = 3, exportSchema = false)
public abstract class GymLogDB extends RoomDatabase {
    public static final String gymLogTable = "gymLogTable";
    public static final String userTable = "userTable";
    //this is new from prev class
    private static volatile GymLogDB INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static GymLogDB getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (GymLogDB.class){
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GymLogDB.class,
                            "GymLogDB"
                            ).fallbackToDestructiveMigration().addCallback(addDefaulValues).build();
                    }
                }
            }
        return INSTANCE;
    }
    private static final RoomDatabase.Callback addDefaulValues = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            //TODO: add stuff
            dbWriteExecutor.execute(()->{
                UserDAO dao = INSTANCE.userDAO();
                User admin = new User("admin1", "admin1");
                admin.setAdmin(true);
                User testUser = new User("testuser1", "testuser1");
                dao.insert(admin, testUser);

            });
        }
    };
    public abstract GymLogDAO gymLogDAO();
    public abstract UserDAO userDAO();

}
