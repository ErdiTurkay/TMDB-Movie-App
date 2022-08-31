package com.example.challenge4.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.challenge4.model.Actor
import com.example.challenge4.model.Converters
import com.example.challenge4.model.Movie

class MovieDatabase {
    @Dao
    interface MovieDao {
        @Query("SELECT * FROM movie")
        fun getAll(): List<Movie>

        @Query("SELECT id FROM movie")
        fun getAllIDs(): List<Int>

        @Insert(onConflict = IGNORE)
        fun insert(movie: Movie)

        @Delete
        fun delete(movie: Movie)
    }

    @Dao
    interface ActorDao {
        @Query("SELECT * FROM actor")
        fun getAll(): List<Actor>

        @Query("SELECT id FROM actor")
        fun getAllIDs(): List<Int>

        @Insert(onConflict = IGNORE)
        fun insert(movie: Actor)

        @Delete
        fun delete(movie: Actor)
    }

    @Database(entities = [Movie::class, Actor::class], version = 1)
    @TypeConverters(Converters::class)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun movieDao(): MovieDao
        abstract fun actorDao(): ActorDao
    }
}
