package com.sdrockstarstudios.meatheadandroid.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.sdrockstarstudios.meatheadandroid.model.daos.ExerciseDoa;
import com.sdrockstarstudios.meatheadandroid.model.daos.SetsDao;
import com.sdrockstarstudios.meatheadandroid.model.daos.WorkoutDao;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Weight;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

@Database(entities = {Workout.class, Exercise.class, Sets.class, Weight.class}, version = 2)
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
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull @io.reactivex.annotations.NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Weight` (`weightUUID` TEXT NOT NULL, "
                    + "`weight` INTEGER NOT NULL, `date` INTEGER NOT NULL, PRIMARY KEY(`weightUUID`))");
        }
    };
}


