package com.example.gymlogsp.Database;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import com.example.gymlogsp.Database.entities.User;

import java.util.List;
@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... user);
    @Delete
    void delete(User user);

    @Query("select * from "+ GymLogDB.userTable+" order by username")
    LiveData<List<User>> getAllUsers();
    @Query("delete from "+ GymLogDB.userTable)
    void deleteAll();
    @Query("select * from "+GymLogDB.userTable+ " WHERE username == :username")
    LiveData<User> getUserByUserName(String username);

    @Query("select * from "+GymLogDB.userTable+ " WHERE id == :userId")
    LiveData<User> getUserById(int userId);
}
