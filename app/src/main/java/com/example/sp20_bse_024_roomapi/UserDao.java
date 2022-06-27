package com.example.sp20_bse_024_roomapi;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE username LIKE :name")
    User findByName(String name);

    @Insert
    void insertOne(User user);

    @Update
    void updateOne(User user);

    @Delete
    void delete(User user);
}

