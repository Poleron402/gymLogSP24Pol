package com.example.gymlogsp.Database.entities;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.gymlogsp.Database.GymLog;

@Database(entities = {GymLog.class}, version = 1, exportSchema = false)
public abstract class GymLogDB extends RoomDatabase {
    public static final String gymLogTable = "gymLogTable";

}
