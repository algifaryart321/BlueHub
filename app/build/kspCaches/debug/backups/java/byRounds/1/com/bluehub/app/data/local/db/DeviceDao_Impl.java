package com.bluehub.app.data.local.db;

import android.database.Cursor;
import android.os.CancellationSignal;
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
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DeviceEntity> __insertionAdapterOfDeviceEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDevice;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDeviceEntity = new EntityInsertionAdapter<DeviceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `saved_devices` (`address`,`name`,`category`,`protocol`,`lastConnected`,`isAutoConnect`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DeviceEntity entity) {
        statement.bindString(1, entity.getAddress());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getProtocol());
        statement.bindLong(5, entity.getLastConnected());
        final int _tmp = entity.isAutoConnect() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__preparedStmtOfDeleteDevice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_devices WHERE address = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDevice(final DeviceEntity device,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDeviceEntity.insert(device);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDevice(final String address, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDevice.acquire();
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
          __preparedStmtOfDeleteDevice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DeviceEntity>> getAllDevices() {
    final String _sql = "SELECT * FROM saved_devices ORDER BY lastConnected DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_devices"}, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfLastConnected = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnected");
          final int _cursorIndexOfIsAutoConnect = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoConnect");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final long _tmpLastConnected;
            _tmpLastConnected = _cursor.getLong(_cursorIndexOfLastConnected);
            final boolean _tmpIsAutoConnect;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoConnect);
            _tmpIsAutoConnect = _tmp != 0;
            _item = new DeviceEntity(_tmpAddress,_tmpName,_tmpCategory,_tmpProtocol,_tmpLastConnected,_tmpIsAutoConnect);
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

  @Override
  public Object getAutoConnectDevices(final Continuation<? super List<DeviceEntity>> $completion) {
    final String _sql = "SELECT * FROM saved_devices WHERE isAutoConnect = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DeviceEntity>>() {
      @Override
      @NonNull
      public List<DeviceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfLastConnected = CursorUtil.getColumnIndexOrThrow(_cursor, "lastConnected");
          final int _cursorIndexOfIsAutoConnect = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoConnect");
          final List<DeviceEntity> _result = new ArrayList<DeviceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DeviceEntity _item;
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpProtocol;
            _tmpProtocol = _cursor.getString(_cursorIndexOfProtocol);
            final long _tmpLastConnected;
            _tmpLastConnected = _cursor.getLong(_cursorIndexOfLastConnected);
            final boolean _tmpIsAutoConnect;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoConnect);
            _tmpIsAutoConnect = _tmp != 0;
            _item = new DeviceEntity(_tmpAddress,_tmpName,_tmpCategory,_tmpProtocol,_tmpLastConnected,_tmpIsAutoConnect);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
