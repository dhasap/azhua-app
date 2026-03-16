package com.azhua.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.lang.Integer;
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
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WatchHistory> __insertionAdapterOfWatchHistory;

  private final SharedSQLiteStatement __preparedStmtOfDeleteHistory;

  private final SharedSQLiteStatement __preparedStmtOfClearAllHistory;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWatchHistory = new EntityInsertionAdapter<WatchHistory>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `watch_history` (`animeUrl`,`title`,`coverUrl`,`sourceName`,`episodeUrl`,`episodeName`,`timestampMs`,`durationMs`,`lastWatchedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WatchHistory entity) {
        statement.bindString(1, entity.getAnimeUrl());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getCoverUrl());
        statement.bindString(4, entity.getSourceName());
        statement.bindString(5, entity.getEpisodeUrl());
        statement.bindString(6, entity.getEpisodeName());
        statement.bindLong(7, entity.getTimestampMs());
        statement.bindLong(8, entity.getDurationMs());
        statement.bindLong(9, entity.getLastWatchedAt());
      }
    };
    this.__preparedStmtOfDeleteHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM watch_history WHERE animeUrl = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM watch_history";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final WatchHistory history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWatchHistory.insert(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteHistory(final String url, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteHistory.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, url);
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
          __preparedStmtOfDeleteHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllHistory.acquire();
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
          __preparedStmtOfClearAllHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WatchHistory>> getAllHistory() {
    final String _sql = "SELECT * FROM watch_history ORDER BY lastWatchedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"watch_history"}, new Callable<List<WatchHistory>>() {
      @Override
      @NonNull
      public List<WatchHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAnimeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "animeUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfEpisodeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeUrl");
          final int _cursorIndexOfEpisodeName = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeName");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfLastWatchedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedAt");
          final List<WatchHistory> _result = new ArrayList<WatchHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WatchHistory _item;
            final String _tmpAnimeUrl;
            _tmpAnimeUrl = _cursor.getString(_cursorIndexOfAnimeUrl);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCoverUrl;
            _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpEpisodeUrl;
            _tmpEpisodeUrl = _cursor.getString(_cursorIndexOfEpisodeUrl);
            final String _tmpEpisodeName;
            _tmpEpisodeName = _cursor.getString(_cursorIndexOfEpisodeName);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final long _tmpLastWatchedAt;
            _tmpLastWatchedAt = _cursor.getLong(_cursorIndexOfLastWatchedAt);
            _item = new WatchHistory(_tmpAnimeUrl,_tmpTitle,_tmpCoverUrl,_tmpSourceName,_tmpEpisodeUrl,_tmpEpisodeName,_tmpTimestampMs,_tmpDurationMs,_tmpLastWatchedAt);
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
  public Object getHistoryByAnime(final String url,
      final Continuation<? super WatchHistory> $completion) {
    final String _sql = "SELECT * FROM watch_history WHERE animeUrl = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, url);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WatchHistory>() {
      @Override
      @Nullable
      public WatchHistory call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAnimeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "animeUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfEpisodeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeUrl");
          final int _cursorIndexOfEpisodeName = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeName");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfLastWatchedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedAt");
          final WatchHistory _result;
          if (_cursor.moveToFirst()) {
            final String _tmpAnimeUrl;
            _tmpAnimeUrl = _cursor.getString(_cursorIndexOfAnimeUrl);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCoverUrl;
            _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpEpisodeUrl;
            _tmpEpisodeUrl = _cursor.getString(_cursorIndexOfEpisodeUrl);
            final String _tmpEpisodeName;
            _tmpEpisodeName = _cursor.getString(_cursorIndexOfEpisodeName);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final long _tmpLastWatchedAt;
            _tmpLastWatchedAt = _cursor.getLong(_cursorIndexOfLastWatchedAt);
            _result = new WatchHistory(_tmpAnimeUrl,_tmpTitle,_tmpCoverUrl,_tmpSourceName,_tmpEpisodeUrl,_tmpEpisodeName,_tmpTimestampMs,_tmpDurationMs,_tmpLastWatchedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getHistoryCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM watch_history";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WatchHistory>> searchHistory(final String query) {
    final String _sql = "SELECT * FROM watch_history WHERE title LIKE '%' || ? || '%' ORDER BY lastWatchedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"watch_history"}, new Callable<List<WatchHistory>>() {
      @Override
      @NonNull
      public List<WatchHistory> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAnimeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "animeUrl");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfCoverUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "coverUrl");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfEpisodeUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeUrl");
          final int _cursorIndexOfEpisodeName = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeName");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfDurationMs = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMs");
          final int _cursorIndexOfLastWatchedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastWatchedAt");
          final List<WatchHistory> _result = new ArrayList<WatchHistory>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WatchHistory _item;
            final String _tmpAnimeUrl;
            _tmpAnimeUrl = _cursor.getString(_cursorIndexOfAnimeUrl);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpCoverUrl;
            _tmpCoverUrl = _cursor.getString(_cursorIndexOfCoverUrl);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpEpisodeUrl;
            _tmpEpisodeUrl = _cursor.getString(_cursorIndexOfEpisodeUrl);
            final String _tmpEpisodeName;
            _tmpEpisodeName = _cursor.getString(_cursorIndexOfEpisodeName);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpDurationMs;
            _tmpDurationMs = _cursor.getLong(_cursorIndexOfDurationMs);
            final long _tmpLastWatchedAt;
            _tmpLastWatchedAt = _cursor.getLong(_cursorIndexOfLastWatchedAt);
            _item = new WatchHistory(_tmpAnimeUrl,_tmpTitle,_tmpCoverUrl,_tmpSourceName,_tmpEpisodeUrl,_tmpEpisodeName,_tmpTimestampMs,_tmpDurationMs,_tmpLastWatchedAt);
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
