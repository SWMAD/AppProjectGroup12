package dk.au.mad21spring.spacenewsapplication.Database;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class NewsDAO_Impl implements NewsDAO {
  private final RoomDatabase __db;

  public NewsDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public LiveData<List<NewsDTO>> getReadLaterList() {
    final String _sql = "SELECT * FROM newsDTO";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"newsDTO"}, false, new Callable<List<NewsDTO>>() {
      @Override
      public List<NewsDTO> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final List<NewsDTO> _result = new ArrayList<NewsDTO>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NewsDTO _item;
            _item = new NewsDTO();
            _item.uid = _cursor.getInt(_cursorIndexOfUid);
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
}
