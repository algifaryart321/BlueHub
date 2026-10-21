package com.bluehub.app.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DeviceDao _deviceDao;

  private volatile TelemetryDao _telemetryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_devices` (`address` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `protocol` TEXT NOT NULL, `lastConnected` INTEGER NOT NULL, `isAutoConnect` INTEGER NOT NULL, PRIMARY KEY(`address`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `telemetry_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceAddress` TEXT NOT NULL, `label` TEXT NOT NULL, `value` REAL NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6a1eb4fa240df50eccc306c67c956e71')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `saved_devices`");
        db.execSQL("DROP TABLE IF EXISTS `telemetry_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSavedDevices = new HashMap<String, TableInfo.Column>(6);
        _columnsSavedDevices.put("address", new TableInfo.Column("address", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedDevices.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedDevices.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedDevices.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedDevices.put("lastConnected", new TableInfo.Column("lastConnected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedDevices.put("isAutoConnect", new TableInfo.Column("isAutoConnect", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedDevices = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavedDevices = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavedDevices = new TableInfo("saved_devices", _columnsSavedDevices, _foreignKeysSavedDevices, _indicesSavedDevices);
        final TableInfo _existingSavedDevices = TableInfo.read(db, "saved_devices");
        if (!_infoSavedDevices.equals(_existingSavedDevices)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_devices(com.bluehub.app.data.local.db.DeviceEntity).\n"
                  + " Expected:\n" + _infoSavedDevices + "\n"
                  + " Found:\n" + _existingSavedDevices);
        }
        final HashMap<String, TableInfo.Column> _columnsTelemetryLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsTelemetryLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLogs.put("deviceAddress", new TableInfo.Column("deviceAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLogs.put("label", new TableInfo.Column("label", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLogs.put("value", new TableInfo.Column("value", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTelemetryLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTelemetryLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTelemetryLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTelemetryLogs = new TableInfo("telemetry_logs", _columnsTelemetryLogs, _foreignKeysTelemetryLogs, _indicesTelemetryLogs);
        final TableInfo _existingTelemetryLogs = TableInfo.read(db, "telemetry_logs");
        if (!_infoTelemetryLogs.equals(_existingTelemetryLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "telemetry_logs(com.bluehub.app.data.local.db.TelemetryLogEntity).\n"
                  + " Expected:\n" + _infoTelemetryLogs + "\n"
                  + " Found:\n" + _existingTelemetryLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6a1eb4fa240df50eccc306c67c956e71", "1f5141e04d4c4ee3fc3a5b6eb0c832fa");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "saved_devices","telemetry_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `saved_devices`");
      _db.execSQL("DELETE FROM `telemetry_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DeviceDao.class, DeviceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TelemetryDao.class, TelemetryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DeviceDao deviceDao() {
    if (_deviceDao != null) {
      return _deviceDao;
    } else {
      synchronized(this) {
        if(_deviceDao == null) {
          _deviceDao = new DeviceDao_Impl(this);
        }
        return _deviceDao;
      }
    }
  }

  @Override
  public TelemetryDao telemetryDao() {
    if (_telemetryDao != null) {
      return _telemetryDao;
    } else {
      synchronized(this) {
        if(_telemetryDao == null) {
          _telemetryDao = new TelemetryDao_Impl(this);
        }
        return _telemetryDao;
      }
    }
  }
}
