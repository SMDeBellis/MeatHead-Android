package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface SetsDao {
    @Query("SELECT * FROM sets")
    Single<List<Sets>> getAll();

    @Query("SELECT * FROM sets WHERE parentExerciseUUID IN (:uuid)")
    Single<List<Sets>> getAllByParentExerciseUUID(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Sets set);
}
