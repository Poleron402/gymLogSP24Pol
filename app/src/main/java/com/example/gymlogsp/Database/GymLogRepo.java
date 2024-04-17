package com.example.gymlogsp.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.gymlogsp.Database.GymLogDAO;
import com.example.gymlogsp.Database.entities.GymLog;
import com.example.gymlogsp.Database.entities.User;
import com.example.gymlogsp.MainActivity;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GymLogRepo {
    private GymLogDAO gymLogDAO;
    private UserDAO userDAO;
    private static GymLogRepo repo;
    private ArrayList<GymLog> allLogs;
    public GymLogRepo(Application application){
        GymLogDB db = GymLogDB.getDatabase(application);
        this.gymLogDAO = db.gymLogDAO();
        this.userDAO = db.userDAO();
        this.allLogs = (ArrayList<GymLog>) this.gymLogDAO.getAllRecords();
    }

    public static GymLogRepo getRepository(Application application) {
        if(repo!= null){
            return repo;
        }
        Future<GymLogRepo> future = GymLogDB.dbWriteExecutor.submit(
                new Callable<GymLogRepo>() {
                    @Override
                    public GymLogRepo call() throws Exception {
                        return new GymLogRepo(application);
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
    public ArrayList<GymLog> getAllLogs() {

        Future<ArrayList<GymLog>> future = GymLogDB.dbWriteExecutor.submit(
                new Callable<ArrayList<GymLog>>() {
                    @Override
                    public ArrayList<GymLog> call() throws Exception {
                        return (ArrayList<GymLog>) gymLogDAO.getAllRecords();
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
        });
    }
    public void insertUser(User... user){
        GymLogDB.dbWriteExecutor.execute(()->{
            userDAO.insert(user);
        });
    }
    public LiveData<User> getUserByUserName(String username){
        return userDAO.getUserByUserName(username);
    }
    public LiveData<User> getUserById(int userId){
        return userDAO.getUserById(userId);
    }
}
