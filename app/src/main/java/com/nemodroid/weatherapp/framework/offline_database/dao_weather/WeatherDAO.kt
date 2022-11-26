package com.nemodroid.weatherapp.framework.offline_database.dao_weather

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
    @Query("select * from WeatherHistory order by id")
    fun getAllHistory(): Flow<List<WeatherHistory>>

    @Query("select * from WeatherHistory where city=:city  order by id")
    fun getAllHistoryByCity(city: String): Flow<List<WeatherHistory>>

    @Query("delete from city")
    fun deleteAllHistory()

    @Query("delete from city where cityName=:city")
    fun deleteAllHistoryByCity(city: String)

    @Insert
    suspend fun insertHistory(model: WeatherHistory)

    @Delete
    suspend fun deleteHistory(model: WeatherHistory)
}