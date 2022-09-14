package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.Converters


@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid where closeApproachDate >= :today order by closeApproachDate")
    fun getAsteroidsForWeek(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate is :today")
    fun getAsteroidsForToday(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("delete from databaseasteroid where closeApproachDate < :today")
    fun deleteAsteroidsBeforeToday(today: String)

    @Query("select * from databaseasteroid order by closeApproachDate")
    fun getSavedAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DatabaseAsteroid>)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
@TypeConverters(Converters::class)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}
