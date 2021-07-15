package com.sdrockstarstudios.meatheadandroid.model.tables;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.sdrockstarstudios.meatheadandroid.model.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "weight")
public class Weight {
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "weightUUID")
    public String weightUUID;

    @ColumnInfo(name = "date")
    @NonNull
    @TypeConverters(DateConverter.class)
    public Date date;
}
