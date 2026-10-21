package com.bluehub.app.data.local.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_devices")
data class DeviceEntity(
    @PrimaryKey val address: String,
    val name: String,
    val category: String,
    val protocol: String,
    val lastConnected: Long = System.currentTimeMillis(),
    val isAutoConnect: Boolean = false
)

@Entity(tableName = "telemetry_logs")
data class TelemetryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceAddress: String,
    val label: String,
    val value: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface DeviceDao {
    @Query("SELECT * FROM saved_devices ORDER BY lastConnected DESC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM saved_devices WHERE isAutoConnect = 1")
    suspend fun getAutoConnectDevices(): List<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Query("DELETE FROM saved_devices WHERE address = :address")
    suspend fun deleteDevice(address: String)
}

@Dao
interface TelemetryDao {
    @Query("SELECT * FROM telemetry_logs WHERE deviceAddress = :address ORDER BY timestamp DESC LIMIT 500")
    fun getLogsForDevice(address: String): Flow<List<TelemetryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TelemetryLogEntity)

    @Query("DELETE FROM telemetry_logs WHERE deviceAddress = :address")
    suspend fun clearLogs(address: String)
}

@Database(entities = [DeviceEntity::class, TelemetryLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun telemetryDao(): TelemetryDao
}
