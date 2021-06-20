package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sdrockstarstudios.meatheadandroid.helpers.WorkoutFactory;
import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ExerciseDoaTest  {

    private AppDatabase mDatabase;

    @Before
    public void initDb() throws Exception {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        mDatabase.close();
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getExerciseAndSetsSingleExercise(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testExerciseUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testExerciseUUID", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.setsDao().insert(toInsert).blockingAwait();
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).exercise.exerciseName.equals(exercise.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).setList.size() == 1);
    }

    @Test
    public void deleteExerciseWithNoSetsFromDatabase(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testExerciseUUID", "My Awesome exercise.","testUUID");

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).exercise.exerciseName.equals(exercise.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).setList.size() == 0);
        mDatabase.exerciseDoa().delete(exercise).blockingAwait();
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 0);

    }

    @Test
    public void deleteExerciseWithOneSet(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testExerciseUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testExerciseUUID", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.setsDao().insert(toInsert).blockingAwait();
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).exercise.exerciseName.equals(exercise.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).setList.size() == 1);
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 1);

        mDatabase.exerciseDoa().delete(exercise).blockingAwait();
        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 0);
        mDatabase.setsDao().getAll()
                .test()
                .assertValue(sets -> sets.size() == 0);
    }
}
