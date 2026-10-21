package com.bluehub.app.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.room.Room
import com.bluehub.app.data.local.db.AppDatabase
import com.bluehub.app.data.local.db.DeviceDao
import com.bluehub.app.data.local.db.TelemetryDao
import com.bluehub.app.data.repository.BluetoothRepositoryImpl
import com.bluehub.app.domain.repository.BluetoothRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter? {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        return manager?.adapter
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bluehub_database.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()

    @Provides
    fun provideTelemetryDao(database: AppDatabase): TelemetryDao = database.telemetryDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        impl: BluetoothRepositoryImpl
    ): BluetoothRepository
}
