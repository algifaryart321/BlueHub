package com.bluehub.app.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TelemetryDao_Impl implements TelemetryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TelemetryLogEntity> __insertionAdapterOfTelemetryLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearLogs;

  public TelemetryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTelemetryLogEntity = new EntityInsertionAdapter<TelemetryLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `telemetry_logs` (`id`,`deviceAddress`,`label`,`value`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TelemetryLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDeviceAddress());
        statement.bindString(3, entity.getLabel());
        statement.bindDouble(4, entity.getValue());
        statement.bindLong(5, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfClearLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM telemetry_logs WHERE deviceAddress = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertLog(final TelemetryLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTelemetryLogEntity.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearLogs(final String address, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearLogs.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, address);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TelemetryLogEntity>> getLogsForDevice(final String address) {
    final String _sql = "SELECT * FROM telemetry_logs WHERE deviceAddress = ? ORDER BY timestamp DESC LIMIT 500";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, address);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"telemetry_logs"}, new Callable<List<TelemetryLogEntity>>() {
      @Override
      @NonNull
      public List<TelemetryLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDeviceAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceAddress");
          final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<TelemetryLogEntity> _result = new ArrayList<TelemetryLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TelemetryLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDeviceAddress;
            _tmpDeviceAddress = _cursor.getString(_cursorIndexOfDeviceAddress);
            final String _tmpLabel;
            _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
            final float _tmpValue;
            _tmpValue = _cursor.getFloat(_cursorIndexOfValue);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new TelemetryLogEntity(_tmpId,_tmpDeviceAddress,_tmpLabel,_tmpValue,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
