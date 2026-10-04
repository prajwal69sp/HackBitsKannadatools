package com.hackbitskannada.lab.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
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
public class ContentDao_Impl(
  __db: RoomDatabase,
) : ContentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRecentlyViewedEntity: EntityInsertAdapter<RecentlyViewedEntity>

  private val __insertAdapterOfCategoryEntity: EntityInsertAdapter<CategoryEntity>

  private val __insertAdapterOfCommandEntity: EntityInsertAdapter<CommandEntity>

  private val __insertAdapterOfScriptEntity: EntityInsertAdapter<ScriptEntity>

  private val __insertAdapterOfToolEntity: EntityInsertAdapter<ToolEntity>

  private val __insertAdapterOfTutorialEntity: EntityInsertAdapter<TutorialEntity>

  private val __insertAdapterOfCategoryEntity_1: EntityInsertAdapter<CategoryEntity>

  private val __insertAdapterOfCommandEntity_1: EntityInsertAdapter<CommandEntity>

  private val __insertAdapterOfScriptEntity_1: EntityInsertAdapter<ScriptEntity>

  private val __insertAdapterOfToolEntity_1: EntityInsertAdapter<ToolEntity>

  private val __insertAdapterOfTutorialEntity_1: EntityInsertAdapter<TutorialEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRecentlyViewedEntity = object :
        EntityInsertAdapter<RecentlyViewedEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `recently_viewed` (`contentType`,`contentId`,`viewedAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RecentlyViewedEntity) {
        statement.bindText(1, entity.contentType)
        statement.bindText(2, entity.contentId)
        statement.bindLong(3, entity.viewedAt)
      }
    }
    this.__insertAdapterOfCategoryEntity = object : EntityInsertAdapter<CategoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `categories` (`name`,`description`,`section`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CategoryEntity) {
        statement.bindText(1, entity.name)
        statement.bindText(2, entity.description)
        statement.bindText(3, entity.section)
      }
    }
    this.__insertAdapterOfCommandEntity = object : EntityInsertAdapter<CommandEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `commands` (`id`,`title`,`command`,`category`,`description`,`syntax`,`example`,`expectedUsage`,`difficulty`,`tags`,`warning`,`relatedCommands`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CommandEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.command)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.syntax)
        statement.bindText(7, entity.example)
        statement.bindText(8, entity.expectedUsage)
        statement.bindText(9, entity.difficulty)
        statement.bindText(10, entity.tags)
        statement.bindText(11, entity.warning)
        statement.bindText(12, entity.relatedCommands)
        statement.bindLong(13, entity.createdAt)
      }
    }
    this.__insertAdapterOfScriptEntity = object : EntityInsertAdapter<ScriptEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `scripts` (`id`,`title`,`category`,`difficulty`,`purpose`,`code`,`explanation`,`howToRun`,`expectedOutput`,`safetyNotes`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ScriptEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.difficulty)
        statement.bindText(5, entity.purpose)
        statement.bindText(6, entity.code)
        statement.bindText(7, entity.explanation)
        statement.bindText(8, entity.howToRun)
        statement.bindText(9, entity.expectedOutput)
        statement.bindText(10, entity.safetyNotes)
      }
    }
    this.__insertAdapterOfToolEntity = object : EntityInsertAdapter<ToolEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `tools` (`id`,`name`,`category`,`description`,`purpose`,`installCommand`,`basicUsage`,`example`,`commonErrors`,`troubleshooting`,`safetyNote`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ToolEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.purpose)
        statement.bindText(6, entity.installCommand)
        statement.bindText(7, entity.basicUsage)
        statement.bindText(8, entity.example)
        statement.bindText(9, entity.commonErrors)
        statement.bindText(10, entity.troubleshooting)
        statement.bindText(11, entity.safetyNote)
      }
    }
    this.__insertAdapterOfTutorialEntity = object : EntityInsertAdapter<TutorialEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `tutorials` (`id`,`title`,`level`,`section`,`lessonOrder`,`body`,`tags`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TutorialEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.level)
        statement.bindText(4, entity.section)
        statement.bindLong(5, entity.lessonOrder.toLong())
        statement.bindText(6, entity.body)
        statement.bindText(7, entity.tags)
      }
    }
    this.__insertAdapterOfCategoryEntity_1 = object : EntityInsertAdapter<CategoryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `categories` (`name`,`description`,`section`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CategoryEntity) {
        statement.bindText(1, entity.name)
        statement.bindText(2, entity.description)
        statement.bindText(3, entity.section)
      }
    }
    this.__insertAdapterOfCommandEntity_1 = object : EntityInsertAdapter<CommandEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `commands` (`id`,`title`,`command`,`category`,`description`,`syntax`,`example`,`expectedUsage`,`difficulty`,`tags`,`warning`,`relatedCommands`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CommandEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.command)
        statement.bindText(4, entity.category)
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.syntax)
        statement.bindText(7, entity.example)
        statement.bindText(8, entity.expectedUsage)
        statement.bindText(9, entity.difficulty)
        statement.bindText(10, entity.tags)
        statement.bindText(11, entity.warning)
        statement.bindText(12, entity.relatedCommands)
        statement.bindLong(13, entity.createdAt)
      }
    }
    this.__insertAdapterOfScriptEntity_1 = object : EntityInsertAdapter<ScriptEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `scripts` (`id`,`title`,`category`,`difficulty`,`purpose`,`code`,`explanation`,`howToRun`,`expectedOutput`,`safetyNotes`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ScriptEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.difficulty)
        statement.bindText(5, entity.purpose)
        statement.bindText(6, entity.code)
        statement.bindText(7, entity.explanation)
        statement.bindText(8, entity.howToRun)
        statement.bindText(9, entity.expectedOutput)
        statement.bindText(10, entity.safetyNotes)
      }
    }
    this.__insertAdapterOfToolEntity_1 = object : EntityInsertAdapter<ToolEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `tools` (`id`,`name`,`category`,`description`,`purpose`,`installCommand`,`basicUsage`,`example`,`commonErrors`,`troubleshooting`,`safetyNote`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ToolEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.purpose)
        statement.bindText(6, entity.installCommand)
        statement.bindText(7, entity.basicUsage)
        statement.bindText(8, entity.example)
        statement.bindText(9, entity.commonErrors)
        statement.bindText(10, entity.troubleshooting)
        statement.bindText(11, entity.safetyNote)
      }
    }
    this.__insertAdapterOfTutorialEntity_1 = object : EntityInsertAdapter<TutorialEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `tutorials` (`id`,`title`,`level`,`section`,`lessonOrder`,`body`,`tags`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TutorialEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.level)
        statement.bindText(4, entity.section)
        statement.bindLong(5, entity.lessonOrder.toLong())
        statement.bindText(6, entity.body)
        statement.bindText(7, entity.tags)
      }
    }
  }

  public override suspend fun recordViewed(item: RecentlyViewedEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRecentlyViewedEntity.insert(_connection, item)
  }

  public override suspend fun insertCategories(items: List<CategoryEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCategoryEntity.insert(_connection, items)
  }

  public override suspend fun insertCommands(items: List<CommandEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCommandEntity.insert(_connection, items)
  }

  public override suspend fun insertScripts(items: List<ScriptEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfScriptEntity.insert(_connection, items)
  }

  public override suspend fun insertTools(items: List<ToolEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfToolEntity.insert(_connection, items)
  }

  public override suspend fun insertTutorials(items: List<TutorialEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTutorialEntity.insert(_connection, items)
  }

  public override suspend fun upsertSyncedCategories(items: List<CategoryEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCategoryEntity_1.insert(_connection, items)
  }

  public override suspend fun upsertSyncedCommands(items: List<CommandEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCommandEntity_1.insert(_connection, items)
  }

  public override suspend fun upsertSyncedScripts(items: List<ScriptEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfScriptEntity_1.insert(_connection, items)
  }

  public override suspend fun upsertSyncedTools(items: List<ToolEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfToolEntity_1.insert(_connection, items)
  }

  public override suspend fun upsertSyncedTutorials(items: List<TutorialEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTutorialEntity_1.insert(_connection, items)
  }

  public override fun observeCategories(): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories ORDER BY name"
    return createFlow(__db, false, arrayOf("categories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfSection: Int = getColumnIndexOrThrow(_stmt, "section")
        val _result: MutableList<CategoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CategoryEntity
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpSection: String
          _tmpSection = _stmt.getText(_columnIndexOfSection)
          _item = CategoryEntity(_tmpName,_tmpDescription,_tmpSection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCommands(): Flow<List<CommandEntity>> {
    val _sql: String = "SELECT * FROM commands ORDER BY title"
    return createFlow(__db, false, arrayOf("commands")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCommand: Int = getColumnIndexOrThrow(_stmt, "command")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfSyntax: Int = getColumnIndexOrThrow(_stmt, "syntax")
        val _columnIndexOfExample: Int = getColumnIndexOrThrow(_stmt, "example")
        val _columnIndexOfExpectedUsage: Int = getColumnIndexOrThrow(_stmt, "expectedUsage")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfWarning: Int = getColumnIndexOrThrow(_stmt, "warning")
        val _columnIndexOfRelatedCommands: Int = getColumnIndexOrThrow(_stmt, "relatedCommands")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<CommandEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CommandEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCommand: String
          _tmpCommand = _stmt.getText(_columnIndexOfCommand)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpSyntax: String
          _tmpSyntax = _stmt.getText(_columnIndexOfSyntax)
          val _tmpExample: String
          _tmpExample = _stmt.getText(_columnIndexOfExample)
          val _tmpExpectedUsage: String
          _tmpExpectedUsage = _stmt.getText(_columnIndexOfExpectedUsage)
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          val _tmpWarning: String
          _tmpWarning = _stmt.getText(_columnIndexOfWarning)
          val _tmpRelatedCommands: String
          _tmpRelatedCommands = _stmt.getText(_columnIndexOfRelatedCommands)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              CommandEntity(_tmpId,_tmpTitle,_tmpCommand,_tmpCategory,_tmpDescription,_tmpSyntax,_tmpExample,_tmpExpectedUsage,_tmpDifficulty,_tmpTags,_tmpWarning,_tmpRelatedCommands,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeScripts(): Flow<List<ScriptEntity>> {
    val _sql: String = "SELECT * FROM scripts ORDER BY title"
    return createFlow(__db, false, arrayOf("scripts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfPurpose: Int = getColumnIndexOrThrow(_stmt, "purpose")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfExplanation: Int = getColumnIndexOrThrow(_stmt, "explanation")
        val _columnIndexOfHowToRun: Int = getColumnIndexOrThrow(_stmt, "howToRun")
        val _columnIndexOfExpectedOutput: Int = getColumnIndexOrThrow(_stmt, "expectedOutput")
        val _columnIndexOfSafetyNotes: Int = getColumnIndexOrThrow(_stmt, "safetyNotes")
        val _result: MutableList<ScriptEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ScriptEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpPurpose: String
          _tmpPurpose = _stmt.getText(_columnIndexOfPurpose)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpExplanation: String
          _tmpExplanation = _stmt.getText(_columnIndexOfExplanation)
          val _tmpHowToRun: String
          _tmpHowToRun = _stmt.getText(_columnIndexOfHowToRun)
          val _tmpExpectedOutput: String
          _tmpExpectedOutput = _stmt.getText(_columnIndexOfExpectedOutput)
          val _tmpSafetyNotes: String
          _tmpSafetyNotes = _stmt.getText(_columnIndexOfSafetyNotes)
          _item =
              ScriptEntity(_tmpId,_tmpTitle,_tmpCategory,_tmpDifficulty,_tmpPurpose,_tmpCode,_tmpExplanation,_tmpHowToRun,_tmpExpectedOutput,_tmpSafetyNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTools(): Flow<List<ToolEntity>> {
    val _sql: String = "SELECT * FROM tools ORDER BY name"
    return createFlow(__db, false, arrayOf("tools")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPurpose: Int = getColumnIndexOrThrow(_stmt, "purpose")
        val _columnIndexOfInstallCommand: Int = getColumnIndexOrThrow(_stmt, "installCommand")
        val _columnIndexOfBasicUsage: Int = getColumnIndexOrThrow(_stmt, "basicUsage")
        val _columnIndexOfExample: Int = getColumnIndexOrThrow(_stmt, "example")
        val _columnIndexOfCommonErrors: Int = getColumnIndexOrThrow(_stmt, "commonErrors")
        val _columnIndexOfTroubleshooting: Int = getColumnIndexOrThrow(_stmt, "troubleshooting")
        val _columnIndexOfSafetyNote: Int = getColumnIndexOrThrow(_stmt, "safetyNote")
        val _result: MutableList<ToolEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ToolEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPurpose: String
          _tmpPurpose = _stmt.getText(_columnIndexOfPurpose)
          val _tmpInstallCommand: String
          _tmpInstallCommand = _stmt.getText(_columnIndexOfInstallCommand)
          val _tmpBasicUsage: String
          _tmpBasicUsage = _stmt.getText(_columnIndexOfBasicUsage)
          val _tmpExample: String
          _tmpExample = _stmt.getText(_columnIndexOfExample)
          val _tmpCommonErrors: String
          _tmpCommonErrors = _stmt.getText(_columnIndexOfCommonErrors)
          val _tmpTroubleshooting: String
          _tmpTroubleshooting = _stmt.getText(_columnIndexOfTroubleshooting)
          val _tmpSafetyNote: String
          _tmpSafetyNote = _stmt.getText(_columnIndexOfSafetyNote)
          _item =
              ToolEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpPurpose,_tmpInstallCommand,_tmpBasicUsage,_tmpExample,_tmpCommonErrors,_tmpTroubleshooting,_tmpSafetyNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTutorials(): Flow<List<TutorialEntity>> {
    val _sql: String = "SELECT * FROM tutorials ORDER BY lessonOrder"
    return createFlow(__db, false, arrayOf("tutorials")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfSection: Int = getColumnIndexOrThrow(_stmt, "section")
        val _columnIndexOfLessonOrder: Int = getColumnIndexOrThrow(_stmt, "lessonOrder")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: MutableList<TutorialEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TutorialEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpLevel: String
          _tmpLevel = _stmt.getText(_columnIndexOfLevel)
          val _tmpSection: String
          _tmpSection = _stmt.getText(_columnIndexOfSection)
          val _tmpLessonOrder: Int
          _tmpLessonOrder = _stmt.getLong(_columnIndexOfLessonOrder).toInt()
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _item =
              TutorialEntity(_tmpId,_tmpTitle,_tmpLevel,_tmpSection,_tmpLessonOrder,_tmpBody,_tmpTags)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun command(id: String): CommandEntity? {
    val _sql: String = "SELECT * FROM commands WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCommand: Int = getColumnIndexOrThrow(_stmt, "command")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfSyntax: Int = getColumnIndexOrThrow(_stmt, "syntax")
        val _columnIndexOfExample: Int = getColumnIndexOrThrow(_stmt, "example")
        val _columnIndexOfExpectedUsage: Int = getColumnIndexOrThrow(_stmt, "expectedUsage")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _columnIndexOfWarning: Int = getColumnIndexOrThrow(_stmt, "warning")
        val _columnIndexOfRelatedCommands: Int = getColumnIndexOrThrow(_stmt, "relatedCommands")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: CommandEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCommand: String
          _tmpCommand = _stmt.getText(_columnIndexOfCommand)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpSyntax: String
          _tmpSyntax = _stmt.getText(_columnIndexOfSyntax)
          val _tmpExample: String
          _tmpExample = _stmt.getText(_columnIndexOfExample)
          val _tmpExpectedUsage: String
          _tmpExpectedUsage = _stmt.getText(_columnIndexOfExpectedUsage)
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          val _tmpWarning: String
          _tmpWarning = _stmt.getText(_columnIndexOfWarning)
          val _tmpRelatedCommands: String
          _tmpRelatedCommands = _stmt.getText(_columnIndexOfRelatedCommands)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              CommandEntity(_tmpId,_tmpTitle,_tmpCommand,_tmpCategory,_tmpDescription,_tmpSyntax,_tmpExample,_tmpExpectedUsage,_tmpDifficulty,_tmpTags,_tmpWarning,_tmpRelatedCommands,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun script(id: String): ScriptEntity? {
    val _sql: String = "SELECT * FROM scripts WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDifficulty: Int = getColumnIndexOrThrow(_stmt, "difficulty")
        val _columnIndexOfPurpose: Int = getColumnIndexOrThrow(_stmt, "purpose")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfExplanation: Int = getColumnIndexOrThrow(_stmt, "explanation")
        val _columnIndexOfHowToRun: Int = getColumnIndexOrThrow(_stmt, "howToRun")
        val _columnIndexOfExpectedOutput: Int = getColumnIndexOrThrow(_stmt, "expectedOutput")
        val _columnIndexOfSafetyNotes: Int = getColumnIndexOrThrow(_stmt, "safetyNotes")
        val _result: ScriptEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDifficulty: String
          _tmpDifficulty = _stmt.getText(_columnIndexOfDifficulty)
          val _tmpPurpose: String
          _tmpPurpose = _stmt.getText(_columnIndexOfPurpose)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpExplanation: String
          _tmpExplanation = _stmt.getText(_columnIndexOfExplanation)
          val _tmpHowToRun: String
          _tmpHowToRun = _stmt.getText(_columnIndexOfHowToRun)
          val _tmpExpectedOutput: String
          _tmpExpectedOutput = _stmt.getText(_columnIndexOfExpectedOutput)
          val _tmpSafetyNotes: String
          _tmpSafetyNotes = _stmt.getText(_columnIndexOfSafetyNotes)
          _result =
              ScriptEntity(_tmpId,_tmpTitle,_tmpCategory,_tmpDifficulty,_tmpPurpose,_tmpCode,_tmpExplanation,_tmpHowToRun,_tmpExpectedOutput,_tmpSafetyNotes)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun tool(id: String): ToolEntity? {
    val _sql: String = "SELECT * FROM tools WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPurpose: Int = getColumnIndexOrThrow(_stmt, "purpose")
        val _columnIndexOfInstallCommand: Int = getColumnIndexOrThrow(_stmt, "installCommand")
        val _columnIndexOfBasicUsage: Int = getColumnIndexOrThrow(_stmt, "basicUsage")
        val _columnIndexOfExample: Int = getColumnIndexOrThrow(_stmt, "example")
        val _columnIndexOfCommonErrors: Int = getColumnIndexOrThrow(_stmt, "commonErrors")
        val _columnIndexOfTroubleshooting: Int = getColumnIndexOrThrow(_stmt, "troubleshooting")
        val _columnIndexOfSafetyNote: Int = getColumnIndexOrThrow(_stmt, "safetyNote")
        val _result: ToolEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpPurpose: String
          _tmpPurpose = _stmt.getText(_columnIndexOfPurpose)
          val _tmpInstallCommand: String
          _tmpInstallCommand = _stmt.getText(_columnIndexOfInstallCommand)
          val _tmpBasicUsage: String
          _tmpBasicUsage = _stmt.getText(_columnIndexOfBasicUsage)
          val _tmpExample: String
          _tmpExample = _stmt.getText(_columnIndexOfExample)
          val _tmpCommonErrors: String
          _tmpCommonErrors = _stmt.getText(_columnIndexOfCommonErrors)
          val _tmpTroubleshooting: String
          _tmpTroubleshooting = _stmt.getText(_columnIndexOfTroubleshooting)
          val _tmpSafetyNote: String
          _tmpSafetyNote = _stmt.getText(_columnIndexOfSafetyNote)
          _result =
              ToolEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpPurpose,_tmpInstallCommand,_tmpBasicUsage,_tmpExample,_tmpCommonErrors,_tmpTroubleshooting,_tmpSafetyNote)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun tutorial(id: String): TutorialEntity? {
    val _sql: String = "SELECT * FROM tutorials WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfLevel: Int = getColumnIndexOrThrow(_stmt, "level")
        val _columnIndexOfSection: Int = getColumnIndexOrThrow(_stmt, "section")
        val _columnIndexOfLessonOrder: Int = getColumnIndexOrThrow(_stmt, "lessonOrder")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfTags: Int = getColumnIndexOrThrow(_stmt, "tags")
        val _result: TutorialEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpLevel: String
          _tmpLevel = _stmt.getText(_columnIndexOfLevel)
          val _tmpSection: String
          _tmpSection = _stmt.getText(_columnIndexOfSection)
          val _tmpLessonOrder: Int
          _tmpLessonOrder = _stmt.getLong(_columnIndexOfLessonOrder).toInt()
          val _tmpBody: String
          _tmpBody = _stmt.getText(_columnIndexOfBody)
          val _tmpTags: String
          _tmpTags = _stmt.getText(_columnIndexOfTags)
          _result =
              TutorialEntity(_tmpId,_tmpTitle,_tmpLevel,_tmpSection,_tmpLessonOrder,_tmpBody,_tmpTags)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun commandCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM commands"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecentlyViewed(): Flow<List<RecentlyViewedEntity>> {
    val _sql: String = "SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT 8"
    return createFlow(__db, false, arrayOf("recently_viewed")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfContentType: Int = getColumnIndexOrThrow(_stmt, "contentType")
        val _columnIndexOfContentId: Int = getColumnIndexOrThrow(_stmt, "contentId")
        val _columnIndexOfViewedAt: Int = getColumnIndexOrThrow(_stmt, "viewedAt")
        val _result: MutableList<RecentlyViewedEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RecentlyViewedEntity
          val _tmpContentType: String
          _tmpContentType = _stmt.getText(_columnIndexOfContentType)
          val _tmpContentId: String
          _tmpContentId = _stmt.getText(_columnIndexOfContentId)
          val _tmpViewedAt: Long
          _tmpViewedAt = _stmt.getLong(_columnIndexOfViewedAt)
          _item = RecentlyViewedEntity(_tmpContentType,_tmpContentId,_tmpViewedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
