package com.sdrockstarstudios.meatheadandroid.model.daos;

import android.util.Log;

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

import java.util.ArrayList;
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
    public void getExerciseAndSetsTestOneExercise(){
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

        List<ExerciseAndSets> exerciseAndSetsList = mDatabase.exerciseDoa().getAllExerciseAndSets();
        assert exerciseAndSetsList.size() == 1;
        ExerciseAndSets exerciseAndSets = exerciseAndSetsList.get(0);
        assert exerciseAndSets.exercise.exerciseName.equals(exercise.exerciseName);
        List<Sets> sets = exerciseAndSets.setList;
        assert sets.size() == 3;
        for (int i = 0; i < sets.size(); i++) {
            assert sets.get(i).index == i + 1;
        }
    }

    @Test
    public void getExerciseAndSetsTestTwoExercises(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise1 = WorkoutFactory.exerciseBuilder("testUUID", "My Awesome exercise.","testUUID");
        Sets set1 = WorkoutFactory.setBuilder(1, "testUUID", 20, 5, false);
        Sets set2 = WorkoutFactory.setBuilder(2, "testUUID", 20, 5, false);
        Sets set3 = WorkoutFactory.setBuilder(3, "testUUID", 20, 5, false);

        Exercise exercise2 = WorkoutFactory.exerciseBuilder("testUUID2", "My second awesome exercise.","testUUID");
        Sets e2Set1 = WorkoutFactory.setBuilder(1, "testUUID2", 20, 5, false);
        Sets e2Set2 = WorkoutFactory.setBuilder(2, "testUUID2", 20, 5, false);
        Sets e2Set3 = WorkoutFactory.setBuilder(3, "testUUID2", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise1).blockingAwait();
        mDatabase.setsDao().insert(set1).blockingAwait();
        mDatabase.setsDao().insert(set2).blockingAwait();
        mDatabase.setsDao().insert(set3).blockingAwait();

        mDatabase.exerciseDoa().insert(exercise2).blockingAwait();
        mDatabase.setsDao().insert(e2Set1).blockingAwait();
        mDatabase.setsDao().insert(e2Set2).blockingAwait();
        mDatabase.setsDao().insert(e2Set3).blockingAwait();

        List<ExerciseAndSets> exerciseAndSetsList = mDatabase.exerciseDoa().getAllExerciseAndSets();
        assert exerciseAndSetsList.size() == 2;

        ExerciseAndSets exerciseAndSets1 = exerciseAndSetsList.get(0);
        assert exerciseAndSets1.exercise.exerciseName.equals(exercise1.exerciseName);
        List<Sets> e1Sets = exerciseAndSets1.setList;
        Log.i("Set size", String.valueOf(e1Sets.size()));
        assert e1Sets.size() == 3;
        for (int i = 0; i < e1Sets.size(); i++) {
            Sets set = e1Sets.get(i);
            assert set.index == i + 1;
            assert set.parentExerciseUUID.equals(exercise1.exerciseUUID);
        }

        ExerciseAndSets exerciseAndSets2 = exerciseAndSetsList.get(1);
        assert exerciseAndSets2.exercise.exerciseName.equals(exercise2.exerciseName);
        List<Sets> e2Sets = exerciseAndSets2.setList;
        Log.i("Set size", String.valueOf(e2Sets.size()));
        assert e2Sets.size() == 3;
        for (int i = 0; i < e2Sets.size(); i++) {
            Sets set = e2Sets.get(i);
            assert set.index == i + 1;
            assert set.parentExerciseUUID.equals(exercise2.exerciseUUID);
        }
    }

    @Test
    public void getExerciseAndSetsByExerciseName(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise1 = WorkoutFactory.exerciseBuilder("testUUID", "Bench Press","testUUID");
        Sets set1 = WorkoutFactory.setBuilder(1, "testUUID", 20, 5, false);
        Sets set2 = WorkoutFactory.setBuilder(2, "testUUID", 20, 5, false);
        Sets set3 = WorkoutFactory.setBuilder(3, "testUUID", 20, 5, false);

        Workout workout2 = WorkoutFactory.workoutBuilder("testUUID2", "My Second Awesome workout");
        Exercise exercise2 = WorkoutFactory.exerciseBuilder("testUUID2", "Bench press","testUUID2");
        Sets e2Set1 = WorkoutFactory.setBuilder(1, "testUUID2", 20, 5, false);
        Sets e2Set2 = WorkoutFactory.setBuilder(2, "testUUID2", 20, 5, false);

        Exercise exercise3 = WorkoutFactory.exerciseBuilder("testUUID3", "Bent Over Rows","testUUID");
        Sets e3Set1 = WorkoutFactory.setBuilder(1, "testUUID3", 20, 5, false);
        Sets e3Set2 = WorkoutFactory.setBuilder(2, "testUUID3", 20, 5, false);
        Sets e3Set3 = WorkoutFactory.setBuilder(3, "testUUID3", 20, 5, false);

        mDatabase.workoutDao().insert(workout).subscribe();
        mDatabase.exerciseDoa().insert(exercise1).subscribe();
        mDatabase.setsDao().insert(set1).subscribe();
        mDatabase.setsDao().insert(set2).subscribe();
        mDatabase.setsDao().insert(set3).subscribe();

        mDatabase.workoutDao().insert(workout2).subscribe();
        mDatabase.exerciseDoa().insert(exercise2).subscribe();
        mDatabase.setsDao().insert(e2Set1).subscribe();
        mDatabase.setsDao().insert(e2Set2).subscribe();

        mDatabase.exerciseDoa().insert(exercise3).subscribe();
        mDatabase.setsDao().insert(e3Set1).subscribe();
        mDatabase.setsDao().insert(e3Set2).subscribe();
        mDatabase.setsDao().insert(e3Set3).subscribe();

        mDatabase.exerciseDoa().getExerciseAndSetsByExerciseName("Bench press")
                .doOnSuccess(exerciseAndSetsList ->{
                    assert exerciseAndSetsList.size() == 2;
                    ExerciseAndSets exerciseAndSets1 = exerciseAndSetsList.get(0);
                    ExerciseAndSets exerciseAndSets2 = exerciseAndSetsList.get(1);
                    assert exerciseAndSets1.exercise.exerciseName.equals(exercise1.exerciseName);
                    assert exerciseAndSets2.exercise.exerciseName.equals(exercise2.exerciseName);
                    List<Sets> exercise1Sets = exerciseAndSets1.setList;
                    List<Sets> exercise2Sets = exerciseAndSets2.setList;
                    assert exercise1Sets.size() == 3;
                    assert exercise2Sets.size() == 2;
                    for (Sets set: exercise1Sets){
                        assert set.parentExerciseUUID.equals(exercise1.exerciseUUID);
                    }
                    for (Sets set: exercise2Sets){
                        assert set.parentExerciseUUID.equals(exercise2.exerciseUUID);
                    }
                })
                .doOnError(e -> {assert false;});
    }


}
