package com.sdrockstarstudios.meatheadandroid.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sdrockstarstudios.meatheadandroid.model.daos.ExerciseDoa;
import com.sdrockstarstudios.meatheadandroid.model.daos.SetsDao;
import com.sdrockstarstudios.meatheadandroid.model.daos.WorkoutDao;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

@Database(entities = {Workout.class, Exercise.class, Sets.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WorkoutDao workoutDao();
    public abstract ExerciseDoa exerciseDoa();
    public abstract SetsDao setsDao();

    volatile private AppDatabase instance = null;

    AppDatabase getInstance(Context context){
        if(instance != null){
            return instance;
        }
        else{
            synchronized (this){
                instance = buildDatabase(context);
                return instance;
            }
        }
    }

    private AppDatabase buildDatabase(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "MeatHeadAndroid-db")
                .enableMultiInstanceInvalidation()
                .build();
    }
}
