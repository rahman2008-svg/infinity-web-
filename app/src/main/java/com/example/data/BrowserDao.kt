package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {
    // History
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryItem)

    @Delete
    suspend fun deleteHistory(item: HistoryItem)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(item: BookmarkItem)

    @Delete
    suspend fun deleteBookmark(item: BookmarkItem)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url LIMIT 1)")
    suspend fun isBookmarked(url: String): Boolean

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(item: DownloadItem): Long

    @Update
    suspend fun updateDownload(item: DownloadItem)

    @Delete
    suspend fun deleteDownload(item: DownloadItem)

    // Tabs
    @Query("SELECT * FROM tabs ORDER BY timestamp ASC")
    fun getAllTabs(): Flow<List<TabItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(item: TabItem): Long

    @Update
    suspend fun updateTab(item: TabItem)

    @Delete
    suspend fun deleteTab(item: TabItem)

    @Query("DELETE FROM tabs WHERE isIncognito = :isIncognito")
    suspend fun deleteTabsByIncognito(isIncognito: Boolean)

    @Query("UPDATE tabs SET isActive = 0")
    suspend fun deactivateAllTabs()

    @Query("UPDATE tabs SET isActive = 1 WHERE id = :id")
    suspend fun activateTab(id: Long)
}
