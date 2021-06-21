package com.sdrockstarstudios.meatheadandroid.model;

import android.content.Context;
import android.util.Log;

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

    volatile static private AppDatabase instance = null;

    public static AppDatabase getInstance(Context context){
        Log.i("AppDatabase++++++", "Calling getInstance for DB");
        if(instance != null){
            return instance;
        }
        else{
            synchronized (AppDatabase.class){
                Log.i("AppDatabase++++++", "building database.");
                instance = buildDatabase(context);
                return instance;
            }
        }
    }

    private static AppDatabase buildDatabase(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "meathead.db")
                .enableMultiInstanceInvalidation()
                .build();
    }
}
