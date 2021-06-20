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
import java.util.Collections;
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
        Exercise exercise = WorkoutFactory.exerciseBuilder("testExerciseUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testExerciseUUID", 20, 5, false);

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

    @Test
    public void insertAndDeleteSet(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testExerciseUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testExerciseUUID", 20, 5, false);

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
        mDatabase.setsDao().delete(toInsert).blockingAwait();
        mDatabase.setsDao().getAll()
                .test()
                .assertValue(new Predicate<List<Sets>>() {
                    @Override
                    public boolean test(@NonNull List<Sets> sets) throws Exception {
                        return sets.size() == 0;
                    }
                });
    }

    @Test
    public void insertAndDeleteMultipleSets(){
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

        mDatabase.setsDao().getAllByParentExerciseUUID(exercise.exerciseUUID)
                .test()
                .assertValue(new Predicate<List<Sets>>() {
                    @Override
                    public boolean test(@NonNull List<Sets> sets) throws Exception {
                        assert sets.size() == 3;
                        Collections.sort(sets, (x, y) -> x.index.compareTo(y.index));

                        Sets s1 = sets.get(0);
                        assert s1.index == set1.index
                                && s1.parentExerciseUUID.equals(set1.parentExerciseUUID)
                                && s1.weight == set1.weight
                                && s1.reps == set1.reps
                                && s1.repsOnly == set1.repsOnly;

                        Sets s2 = sets.get(1);
                        assert s2.index == set2.index
                                && s2.parentExerciseUUID.equals(set2.parentExerciseUUID)
                                && s2.weight == set2.weight
                                && s2.reps == set2.reps
                                && s2.repsOnly == set2.repsOnly;

                        Sets s3 = sets.get(2);
                        assert s3.index == set3.index
                                && s3.parentExerciseUUID.equals(set3.parentExerciseUUID)
                                && s3.weight == set3.weight
                                && s3.reps == set3.reps
                                && s3.repsOnly == set3.repsOnly;
                        return true;
                    }
                });

        mDatabase.setsDao().delete(set1).blockingAwait();
        mDatabase.setsDao().delete(set2).blockingAwait();
        mDatabase.setsDao().delete(set3).blockingAwait();
        mDatabase.setsDao().getAll()
                .test()
                .assertValue(new Predicate<List<Sets>>() {
                    @Override
                    public boolean test(@NonNull List<Sets> sets) throws Exception {
                        return sets.size() == 0;
                    }
                });
    }

    @Test
    public void getAllByParentExerciseUUIDWithOneSet(){
        Workout workout = WorkoutFactory.workoutBuilder("testUUID", "My Awesome workout");
        Exercise exercise = WorkoutFactory.exerciseBuilder("testUUID", "My Awesome exercise.","testUUID");
        Sets toInsert = WorkoutFactory.setBuilder(5, "testUUID", 20, 5, false);

        mDatabase.workoutDao().insert(workout).blockingAwait();
        mDatabase.exerciseDoa().insert(exercise).blockingAwait();
        mDatabase.setsDao().insert(toInsert).blockingAwait();
        mDatabase.setsDao().getAllByParentExerciseUUID(exercise.exerciseUUID)
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

    @Test
    public void getAllByParentExerciseUUIDWithThreeSets(){
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
        mDatabase.setsDao().getAllByParentExerciseUUID(exercise.exerciseUUID)
                .test()
                .assertValue(new Predicate<List<Sets>>() {
                    @Override
                    public boolean test(@NonNull List<Sets> sets) throws Exception {
                        assert sets.size() == 3;
                        Collections.sort(sets, (x, y) -> x.index.compareTo(y.index));

                        Sets s1 = sets.get(0);
                        assert s1.index == set1.index
                                && s1.parentExerciseUUID.equals(set1.parentExerciseUUID)
                                && s1.weight == set1.weight
                                && s1.reps == set1.reps
                                && s1.repsOnly == set1.repsOnly;

                        Sets s2 = sets.get(1);
                        assert s2.index == set2.index
                                && s2.parentExerciseUUID.equals(set2.parentExerciseUUID)
                                && s2.weight == set2.weight
                                && s2.reps == set2.reps
                                && s2.repsOnly == set2.repsOnly;

                        Sets s3 = sets.get(2);
                        assert s3.index == set3.index
                                && s3.parentExerciseUUID.equals(set3.parentExerciseUUID)
                                && s3.weight == set3.weight
                                && s3.reps == set3.reps
                                && s3.repsOnly == set3.repsOnly;
                        return true;
                    }
                });
    }
}
