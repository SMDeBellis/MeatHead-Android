package com.sdrockstarstudios.meatheadandroid.model.daos;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sdrockstarstudios.meatheadandroid.helpers.WorkoutFactory;
import com.sdrockstarstudios.meatheadandroid.model.AppDatabase;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SetsDoaTest {
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
    public void insertAndGetSet(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testUUID", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.setsDao().insert(toInsert).blockingAwait();
        mDatabase.setsDao().getAll()
            .test()
            .assertValue(new Predicate<List<Sets>>() {
                @Override
                public boolean test(@NonNull List<Sets> sets) throws Exception {
                    assert sets.size() == 1;
                    Sets set = sets.get(0);
                    return set.index == toInsert.index
                            && set.parentExerciseUUID.equals(toInsert.parentExerciseUUID)
                            && set.weight == toInsert.weight
                            && set.reps == toInsert.reps
                            && set.repsOnly == toInsert.repsOnly;
                }
            });
    }
}
