package com.hackbitskannada.lab.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _contentDao: Lazy<ContentDao> = lazy {
    ContentDao_Impl(this)
  }

  private val _userDataDao: Lazy<UserDataDao> = lazy {
    UserDataDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "86090d7fcc66f38e38859779e528b48c", "a5ac1d6fa2881a6876ea7b97779c74f3") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`name` TEXT NOT NULL, `description` TEXT NOT NULL, `section` TEXT NOT NULL, PRIMARY KEY(`name`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `commands` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `command` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT NOT NULL, `syntax` TEXT NOT NULL, `example` TEXT NOT NULL, `expectedUsage` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `tags` TEXT NOT NULL, `warning` TEXT NOT NULL, `relatedCommands` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `scripts` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `difficulty` TEXT NOT NULL, `purpose` TEXT NOT NULL, `code` TEXT NOT NULL, `explanation` TEXT NOT NULL, `howToRun` TEXT NOT NULL, `expectedOutput` TEXT NOT NULL, `safetyNotes` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tools` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT NOT NULL, `purpose` TEXT NOT NULL, `installCommand` TEXT NOT NULL, `basicUsage` TEXT NOT NULL, `example` TEXT NOT NULL, `commonErrors` TEXT NOT NULL, `troubleshooting` TEXT NOT NULL, `safetyNote` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tutorials` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `level` TEXT NOT NULL, `section` TEXT NOT NULL, `lessonOrder` INTEGER NOT NULL, `body` TEXT NOT NULL, `tags` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`contentType` TEXT NOT NULL, `contentId` TEXT NOT NULL, `savedAt` INTEGER NOT NULL, PRIMARY KEY(`contentType`, `contentId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `progress` (`contentType` TEXT NOT NULL, `contentId` TEXT NOT NULL, `completed` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`contentType`, `contentId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `recently_viewed` (`contentType` TEXT NOT NULL, `contentId` TEXT NOT NULL, `viewedAt` INTEGER NOT NULL, PRIMARY KEY(`contentType`, `contentId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `app_settings` (`id` INTEGER NOT NULL, `theme` TEXT NOT NULL, `fontScale` REAL NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '86090d7fcc66f38e38859779e528b48c')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `categories`")
        connection.execSQL("DROP TABLE IF EXISTS `commands`")
        connection.execSQL("DROP TABLE IF EXISTS `scripts`")
        connection.execSQL("DROP TABLE IF EXISTS `tools`")
        connection.execSQL("DROP TABLE IF EXISTS `tutorials`")
        connection.execSQL("DROP TABLE IF EXISTS `favorites`")
        connection.execSQL("DROP TABLE IF EXISTS `progress`")
        connection.execSQL("DROP TABLE IF EXISTS `recently_viewed`")
        connection.execSQL("DROP TABLE IF EXISTS `app_settings`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCategories: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCategories.put("name", TableInfo.Column("name", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("section", TableInfo.Column("section", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCategories: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCategories: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCategories: TableInfo = TableInfo("categories", _columnsCategories,
            _foreignKeysCategories, _indicesCategories)
        val _existingCategories: TableInfo = read(connection, "categories")
        if (!_infoCategories.equals(_existingCategories)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |categories(com.hackbitskannada.lab.data.CategoryEntity).
              | Expected:
              |""".trimMargin() + _infoCategories + """
              |
              | Found:
              |""".trimMargin() + _existingCategories)
        }
        val _columnsCommands: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCommands.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("command", TableInfo.Column("command", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("syntax", TableInfo.Column("syntax", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("example", TableInfo.Column("example", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("expectedUsage", TableInfo.Column("expectedUsage", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("difficulty", TableInfo.Column("difficulty", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("tags", TableInfo.Column("tags", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("warning", TableInfo.Column("warning", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("relatedCommands", TableInfo.Column("relatedCommands", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommands.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCommands: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCommands: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCommands: TableInfo = TableInfo("commands", _columnsCommands, _foreignKeysCommands,
            _indicesCommands)
        val _existingCommands: TableInfo = read(connection, "commands")
        if (!_infoCommands.equals(_existingCommands)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |commands(com.hackbitskannada.lab.data.CommandEntity).
              | Expected:
              |""".trimMargin() + _infoCommands + """
              |
              | Found:
              |""".trimMargin() + _existingCommands)
        }
        val _columnsScripts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsScripts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("difficulty", TableInfo.Column("difficulty", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("purpose", TableInfo.Column("purpose", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("code", TableInfo.Column("code", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("explanation", TableInfo.Column("explanation", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("howToRun", TableInfo.Column("howToRun", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("expectedOutput", TableInfo.Column("expectedOutput", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsScripts.put("safetyNotes", TableInfo.Column("safetyNotes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysScripts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesScripts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoScripts: TableInfo = TableInfo("scripts", _columnsScripts, _foreignKeysScripts,
            _indicesScripts)
        val _existingScripts: TableInfo = read(connection, "scripts")
        if (!_infoScripts.equals(_existingScripts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |scripts(com.hackbitskannada.lab.data.ScriptEntity).
              | Expected:
              |""".trimMargin() + _infoScripts + """
              |
              | Found:
              |""".trimMargin() + _existingScripts)
        }
        val _columnsTools: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTools.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("purpose", TableInfo.Column("purpose", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("installCommand", TableInfo.Column("installCommand", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("basicUsage", TableInfo.Column("basicUsage", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("example", TableInfo.Column("example", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("commonErrors", TableInfo.Column("commonErrors", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("troubleshooting", TableInfo.Column("troubleshooting", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTools.put("safetyNote", TableInfo.Column("safetyNote", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTools: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTools: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTools: TableInfo = TableInfo("tools", _columnsTools, _foreignKeysTools,
            _indicesTools)
        val _existingTools: TableInfo = read(connection, "tools")
        if (!_infoTools.equals(_existingTools)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tools(com.hackbitskannada.lab.data.ToolEntity).
              | Expected:
              |""".trimMargin() + _infoTools + """
              |
              | Found:
              |""".trimMargin() + _existingTools)
        }
        val _columnsTutorials: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTutorials.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("level", TableInfo.Column("level", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("section", TableInfo.Column("section", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("lessonOrder", TableInfo.Column("lessonOrder", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTutorials.put("tags", TableInfo.Column("tags", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTutorials: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTutorials: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTutorials: TableInfo = TableInfo("tutorials", _columnsTutorials,
            _foreignKeysTutorials, _indicesTutorials)
        val _existingTutorials: TableInfo = read(connection, "tutorials")
        if (!_infoTutorials.equals(_existingTutorials)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tutorials(com.hackbitskannada.lab.data.TutorialEntity).
              | Expected:
              |""".trimMargin() + _infoTutorials + """
              |
              | Found:
              |""".trimMargin() + _existingTutorials)
        }
        val _columnsFavorites: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavorites.put("contentType", TableInfo.Column("contentType", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("contentId", TableInfo.Column("contentId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavorites.put("savedAt", TableInfo.Column("savedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavorites: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavorites: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavorites: TableInfo = TableInfo("favorites", _columnsFavorites,
            _foreignKeysFavorites, _indicesFavorites)
        val _existingFavorites: TableInfo = read(connection, "favorites")
        if (!_infoFavorites.equals(_existingFavorites)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorites(com.hackbitskannada.lab.data.FavoriteEntity).
              | Expected:
              |""".trimMargin() + _infoFavorites + """
              |
              | Found:
              |""".trimMargin() + _existingFavorites)
        }
        val _columnsProgress: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProgress.put("contentType", TableInfo.Column("contentType", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProgress.put("contentId", TableInfo.Column("contentId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProgress.put("completed", TableInfo.Column("completed", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProgress.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProgress: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProgress: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoProgress: TableInfo = TableInfo("progress", _columnsProgress, _foreignKeysProgress,
            _indicesProgress)
        val _existingProgress: TableInfo = read(connection, "progress")
        if (!_infoProgress.equals(_existingProgress)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |progress(com.hackbitskannada.lab.data.ProgressEntity).
              | Expected:
              |""".trimMargin() + _infoProgress + """
              |
              | Found:
              |""".trimMargin() + _existingProgress)
        }
        val _columnsRecentlyViewed: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecentlyViewed.put("contentType", TableInfo.Column("contentType", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentlyViewed.put("contentId", TableInfo.Column("contentId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentlyViewed.put("viewedAt", TableInfo.Column("viewedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecentlyViewed: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecentlyViewed: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRecentlyViewed: TableInfo = TableInfo("recently_viewed", _columnsRecentlyViewed,
            _foreignKeysRecentlyViewed, _indicesRecentlyViewed)
        val _existingRecentlyViewed: TableInfo = read(connection, "recently_viewed")
        if (!_infoRecentlyViewed.equals(_existingRecentlyViewed)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |recently_viewed(com.hackbitskannada.lab.data.RecentlyViewedEntity).
              | Expected:
              |""".trimMargin() + _infoRecentlyViewed + """
              |
              | Found:
              |""".trimMargin() + _existingRecentlyViewed)
        }
        val _columnsAppSettings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAppSettings.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("theme", TableInfo.Column("theme", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAppSettings.put("fontScale", TableInfo.Column("fontScale", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAppSettings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAppSettings: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAppSettings: TableInfo = TableInfo("app_settings", _columnsAppSettings,
            _foreignKeysAppSettings, _indicesAppSettings)
        val _existingAppSettings: TableInfo = read(connection, "app_settings")
        if (!_infoAppSettings.equals(_existingAppSettings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |app_settings(com.hackbitskannada.lab.data.AppSettingsEntity).
              | Expected:
              |""".trimMargin() + _infoAppSettings + """
              |
              | Found:
              |""".trimMargin() + _existingAppSettings)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "categories", "commands",
        "scripts", "tools", "tutorials", "favorites", "progress", "recently_viewed", "app_settings")
  }

  public override fun clearAllTables() {
    super.performClear(false, "categories", "commands", "scripts", "tools", "tutorials",
        "favorites", "progress", "recently_viewed", "app_settings")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ContentDao::class, ContentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserDataDao::class, UserDataDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun contentDao(): ContentDao = _contentDao.value

  public override fun userDataDao(): UserDataDao = _userDataDao.value
}
