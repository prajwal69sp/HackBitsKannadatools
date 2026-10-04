package com.hackbitskannada.lab.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserDataDao_Impl(
  __db: RoomDatabase,
) : UserDataDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteEntity: EntityInsertAdapter<FavoriteEntity>

  private val __insertAdapterOfProgressEntity: EntityInsertAdapter<ProgressEntity>

  private val __insertAdapterOfAppSettingsEntity: EntityInsertAdapter<AppSettingsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteEntity = object : EntityInsertAdapter<FavoriteEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `favorites` (`contentType`,`contentId`,`savedAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteEntity) {
        statement.bindText(1, entity.contentType)
        statement.bindText(2, entity.contentId)
        statement.bindLong(3, entity.savedAt)
      }
    }
    this.__insertAdapterOfProgressEntity = object : EntityInsertAdapter<ProgressEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `progress` (`contentType`,`contentId`,`completed`,`updatedAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProgressEntity) {
        statement.bindText(1, entity.contentType)
        statement.bindText(2, entity.contentId)
        val _tmp: Int = if (entity.completed) 1 else 0
        statement.bindLong(3, _tmp.toLong())
        statement.bindLong(4, entity.updatedAt)
      }
    }
    this.__insertAdapterOfAppSettingsEntity = object : EntityInsertAdapter<AppSettingsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `app_settings` (`id`,`theme`,`fontScale`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AppSettingsEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.theme)
        statement.bindDouble(3, entity.fontScale.toDouble())
      }
    }
  }

  public override suspend fun saveFavorite(favorite: FavoriteEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfFavoriteEntity.insert(_connection, favorite)
  }

  public override suspend fun setProgress(progress: ProgressEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfProgressEntity.insert(_connection, progress)
  }

  public override suspend fun saveSettings(settings: AppSettingsEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAppSettingsEntity.insert(_connection, settings)
  }

  public override fun observeFavorites(): Flow<List<FavoriteEntity>> {
    val _sql: String = "SELECT * FROM favorites ORDER BY savedAt DESC"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfContentType: Int = getColumnIndexOrThrow(_stmt, "contentType")
        val _columnIndexOfContentId: Int = getColumnIndexOrThrow(_stmt, "contentId")
        val _columnIndexOfSavedAt: Int = getColumnIndexOrThrow(_stmt, "savedAt")
        val _result: MutableList<FavoriteEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteEntity
          val _tmpContentType: String
          _tmpContentType = _stmt.getText(_columnIndexOfContentType)
          val _tmpContentId: String
          _tmpContentId = _stmt.getText(_columnIndexOfContentId)
          val _tmpSavedAt: Long
          _tmpSavedAt = _stmt.getLong(_columnIndexOfSavedAt)
          _item = FavoriteEntity(_tmpContentType,_tmpContentId,_tmpSavedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeFavorite(type: String, id: String): Flow<Boolean> {
    val _sql: String =
        "SELECT EXISTS(SELECT 1 FROM favorites WHERE contentType = ? AND contentId = ?)"
    return createFlow(__db, false, arrayOf("favorites")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun progress(type: String, id: String): ProgressEntity? {
    val _sql: String = "SELECT * FROM progress WHERE contentType = ? AND contentId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfContentType: Int = getColumnIndexOrThrow(_stmt, "contentType")
        val _columnIndexOfContentId: Int = getColumnIndexOrThrow(_stmt, "contentId")
        val _columnIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: ProgressEntity?
        if (_stmt.step()) {
          val _tmpContentType: String
          _tmpContentType = _stmt.getText(_columnIndexOfContentType)
          val _tmpContentId: String
          _tmpContentId = _stmt.getText(_columnIndexOfContentId)
          val _tmpCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfCompleted).toInt()
          _tmpCompleted = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result = ProgressEntity(_tmpContentType,_tmpContentId,_tmpCompleted,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCompletedLessons(): Flow<List<ProgressEntity>> {
    val _sql: String = "SELECT * FROM progress WHERE contentType = 'tutorial' AND completed = 1"
    return createFlow(__db, false, arrayOf("progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfContentType: Int = getColumnIndexOrThrow(_stmt, "contentType")
        val _columnIndexOfContentId: Int = getColumnIndexOrThrow(_stmt, "contentId")
        val _columnIndexOfCompleted: Int = getColumnIndexOrThrow(_stmt, "completed")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ProgressEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProgressEntity
          val _tmpContentType: String
          _tmpContentType = _stmt.getText(_columnIndexOfContentType)
          val _tmpContentId: String
          _tmpContentId = _stmt.getText(_columnIndexOfContentId)
          val _tmpCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfCompleted).toInt()
          _tmpCompleted = _tmp != 0
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item = ProgressEntity(_tmpContentType,_tmpContentId,_tmpCompleted,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeSettings(): Flow<AppSettingsEntity?> {
    val _sql: String = "SELECT * FROM app_settings WHERE id = 1"
    return createFlow(__db, false, arrayOf("app_settings")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTheme: Int = getColumnIndexOrThrow(_stmt, "theme")
        val _columnIndexOfFontScale: Int = getColumnIndexOrThrow(_stmt, "fontScale")
        val _result: AppSettingsEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTheme: String
          _tmpTheme = _stmt.getText(_columnIndexOfTheme)
          val _tmpFontScale: Float
          _tmpFontScale = _stmt.getDouble(_columnIndexOfFontScale).toFloat()
          _result = AppSettingsEntity(_tmpId,_tmpTheme,_tmpFontScale)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun settingsOnce(): AppSettingsEntity? {
    val _sql: String = "SELECT * FROM app_settings WHERE id = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTheme: Int = getColumnIndexOrThrow(_stmt, "theme")
        val _columnIndexOfFontScale: Int = getColumnIndexOrThrow(_stmt, "fontScale")
        val _result: AppSettingsEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpTheme: String
          _tmpTheme = _stmt.getText(_columnIndexOfTheme)
          val _tmpFontScale: Float
          _tmpFontScale = _stmt.getDouble(_columnIndexOfFontScale).toFloat()
          _result = AppSettingsEntity(_tmpId,_tmpTheme,_tmpFontScale)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeFavorite(type: String, id: String) {
    val _sql: String = "DELETE FROM favorites WHERE contentType = ? AND contentId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearProgress() {
    val _sql: String = "DELETE FROM progress"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
