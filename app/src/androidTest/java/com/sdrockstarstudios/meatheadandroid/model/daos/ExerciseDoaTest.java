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

@RunWith(AndroidJUnit4ClassRunner.class)
public class ExerciseDoaTest {
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
    public void getExerciseAndSetsTest(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testUUID", "My Awesome exercise.","testUUID");
        Sets set1 = WorkoutFactory.setBuilder(1, "testUUID", 20, 5, false);
        Sets set2 = WorkoutFactory.setBuilder(2, "testUUID", 20, 5, false);
        Sets set3 = WorkoutFactory.setBuilder(3, "testUUID", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.setsDao().insert(set1).blockingAwait();
        mDatabase.setsDao().insert(set2).blockingAwait();
        mDatabase.setsDao().insert(set3).blockingAwait();

        List<ExerciseAndSets> exerciseAndSets = mDatabase.exerciseDoa().getExerciseAndSets();
        assert exerciseAndSets.size() == 1;
        assert exerciseAndSets.get(0).exercise.exerciseName.equals(exercise.exerciseName);
    }
}
