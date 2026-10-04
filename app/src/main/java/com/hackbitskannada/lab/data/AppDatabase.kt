package com.hackbitskannada.lab.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM commands ORDER BY title")
    fun observeCommands(): Flow<List<CommandEntity>>

    @Query("SELECT * FROM scripts ORDER BY title")
    fun observeScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM tools ORDER BY name")
    fun observeTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tutorials ORDER BY lessonOrder")
    fun observeTutorials(): Flow<List<TutorialEntity>>

    @Query("SELECT * FROM commands WHERE id = :id LIMIT 1")
    suspend fun command(id: String): CommandEntity?

    @Query("SELECT * FROM scripts WHERE id = :id LIMIT 1")
    suspend fun script(id: String): ScriptEntity?

    @Query("SELECT * FROM tools WHERE id = :id LIMIT 1")
    suspend fun tool(id: String): ToolEntity?

    @Query("SELECT * FROM tutorials WHERE id = :id LIMIT 1")
    suspend fun tutorial(id: String): TutorialEntity?

    @Query("SELECT COUNT(*) FROM commands")
    suspend fun commandCount(): Int

    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT 8")
    fun observeRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordViewed(item: RecentlyViewedEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(items: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCommands(items: List<CommandEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertScripts(items: List<ScriptEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTools(items: List<ToolEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTutorials(items: List<TutorialEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedCategories(items: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedCommands(items: List<CommandEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedScripts(items: List<ScriptEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedTools(items: List<ToolEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncedTutorials(items: List<TutorialEntity>)
}

@Dao
interface UserDataDao {
    @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE contentType = :type AND contentId = :id)")
    fun observeFavorite(type: String, id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE contentType = :type AND contentId = :id")
    suspend fun removeFavorite(type: String, id: String)

    @Query("SELECT * FROM progress WHERE contentType = :type AND contentId = :id LIMIT 1")
    suspend fun progress(type: String, id: String): ProgressEntity?

    @Query("SELECT * FROM progress WHERE contentType = 'tutorial' AND completed = 1")
    fun observeCompletedLessons(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setProgress(progress: ProgressEntity)

    @Query("DELETE FROM progress")
    suspend fun clearProgress()

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun settingsOnce(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}

@Database(
    entities = [
        CategoryEntity::class,
        CommandEntity::class,
        ScriptEntity::class,
        ToolEntity::class,
        TutorialEntity::class,
        FavoriteEntity::class,
        ProgressEntity::class,
        RecentlyViewedEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hackbits-kannada.db"
            ).build().also { instance = it }
        }
    }
}