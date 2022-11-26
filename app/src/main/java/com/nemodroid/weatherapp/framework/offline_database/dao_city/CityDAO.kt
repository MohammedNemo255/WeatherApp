package com.nemodroid.weatherapp.framework.offline_database.dao_city

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nemodroid.weatherapp.business.domain.data_model.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDAO {

    @Query("select * from city order by cityName asc")
    fun getAllCities(): Flow<List<City>>

    @Query("delete from city")
    fun deleteAllCities()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)
}