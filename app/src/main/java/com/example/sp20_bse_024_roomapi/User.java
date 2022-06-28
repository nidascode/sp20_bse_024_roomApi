package com.example.sp20_bse_024_roomapi;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "email")
    public String email = "";

    @ColumnInfo(name = "username")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "marital status")
    public int marital_status;
}

