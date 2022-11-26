package com.nemodroid.weatherapp.framework.offline_database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.framework.offline_database.dao_city.CityDAO
import com.nemodroid.weatherapp.framework.offline_database.dao_weather.WeatherDAO

@Database(entities = [City::class, WeatherHistory::class], version = 1, exportSchema = false)
abstract class DatabaseInstance : RoomDatabase() {

    //region city dao
    abstract fun cityDAO(): CityDAO
    //endregion

    //region weather history dao
    abstract fun weatherDAO(): WeatherDAO
    //endregion

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: DatabaseInstance? = null

        fun getInstance(context: Context): DatabaseInstance {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DatabaseInstance::class.java,
                    context.getString(R.string.database_file_name)
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                instance
            }
        }

        private class DatabaseCallback() : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}