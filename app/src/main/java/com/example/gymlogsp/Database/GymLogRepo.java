package com.example.gymlogsp.Database;

import android.app.Application;
import android.util.Log;

import com.example.gymlogsp.Database.GymLogDAO;
import com.example.gymlogsp.Database.entities.GymLog;
import com.example.gymlogsp.MainActivity;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GymLogRepo {
    private GymLogDAO gymLogDAO;
    private ArrayList<GymLog> allLogs;
    public GymLogRepo(Application application){
        GymLogDB db = GymLogDB.getDatabase(application);
        this.gymLogDAO = db.gymLogDAO();
        this.allLogs = this.gymLogDAO.getAllRecords();
    }

    public ArrayList<GymLog> getAllLogs() {
        Future<ArrayList<GymLog>> future = GymLogDB.dbWriteExecutor.submit(
                new Callable<ArrayList<GymLog>>() {
                    @Override
                    public ArrayList<GymLog> call() throws Exception {
                        return gymLogDAO.getAllRecords();
                    }
                }
        );
        try{
            return future.get();
        }catch(InterruptedException | ExecutionException e){
            Log.i(MainActivity.TAG, "Problem getting GymLogs");
        }
        return null;
    }
    public void insertGymLog(GymLog gymLog){
        GymLogDB.dbWriteExecutor.execute(()->{
          gymLogDAO.insert(gymLog);
        })
    }
}
