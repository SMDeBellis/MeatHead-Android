package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
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

public class WorkoutDoaTest {

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
    public void getWorkoutWithExercise() {
        Workout workout1 = WorkoutFactory.workoutBuilder("testUUID1", "My Awesome workout");
        Workout workout2 = WorkoutFactory.workoutBuilder("testUUID2", "My Awesome other workout");
        Workout workout3 = WorkoutFactory.workoutBuilder("testUUID3", "My Awesome other  other workout");
        Exercise exercise1 = WorkoutFactory.exerciseBuilder("testExerciseUUID1", "My Awesome exercise.", "testUUID1", false);
        Sets set1 = WorkoutFactory.setBuilder(5, "testExerciseUUID1", 20, 5);

        Exercise exercise2 = WorkoutFactory.exerciseBuilder("testExerciseUUID2", "My Awesome exercise.", "testUUID2", false);
        Sets set2 = WorkoutFactory.setBuilder(5, "testExerciseUUID2", 20, 5);

        Exercise exercise3 = WorkoutFactory.exerciseBuilder("testExerciseUUID3", "My Not Awesome exercise.", "testUUID3", false);
        Sets set3 = WorkoutFactory.setBuilder(5, "testExerciseUUID3", 20, 5);

        Exercise exercise4 = WorkoutFactory.exerciseBuilder("testExerciseUUID4", "My Other Not Awesome exercise.", "testUUID1", false);
        Sets set4 = WorkoutFactory.setBuilder(5, "testExerciseUUID4", 20, 5);

        mDatabase.workoutDao().insert(workout1).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise1).blockingAwait();
        mDatabase.setsDao().insert(set1).blockingAwait();
        mDatabase.workoutDao().insert(workout2).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise2).blockingAwait();
        mDatabase.setsDao().insert(set2).blockingAwait();

        mDatabase.workoutDao().insert(workout3).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise3).blockingAwait();
        mDatabase.setsDao().insert(set3).blockingAwait();

        mDatabase.exerciseDoa().insert(exercise4).blockingAwait();
        mDatabase.setsDao().insert(set4).blockingAwait();


        mDatabase.exerciseDoa().getExerciseAndSets()
                .test()
                .assertComplete()
                .assertValue(exerciseAndSets -> exerciseAndSets.size() == 4)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).exercise.exerciseName.equals(exercise1.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(0).setList.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(1).exercise.exerciseName.equals(exercise2.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(1).setList.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(2).exercise.exerciseName.equals(exercise3.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(2).setList.size() == 1)
                .assertValue(exerciseAndSets -> exerciseAndSets.get(3).exercise.exerciseName.equals(exercise4.exerciseName))
                .assertValue(exerciseAndSets -> exerciseAndSets.get(3).setList.size() == 1);

        mDatabase.workoutDao().getAllWorkoutsWithExercise(exercise1.exerciseName)
                .test()
                .assertComplete()
                .assertValue(workoutsAndExercises -> workoutsAndExercises.size() == 2)
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(0).workout.workoutUUID.equals(workout1.workoutUUID))
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(0).exercisesAndSets.size() == 2)
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(0).exercisesAndSets.get(0).exercise.exerciseName.equals(exercise1.exerciseName))
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(1).workout.workoutUUID.equals(workout2.workoutUUID))
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(1).exercisesAndSets.size() == 1)
                .assertValue(workoutsAndExercises -> workoutsAndExercises.get(1).exercisesAndSets.get(0).exercise.exerciseName.equals(exercise1.exerciseName));
    }
}