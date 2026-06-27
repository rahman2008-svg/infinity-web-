package com.example.data

import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {
    val allHistory: Flow<List<HistoryItem>> = dao.getAllHistory()
    val allBookmarks: Flow<List<BookmarkItem>> = dao.getAllBookmarks()
    val allDownloads: Flow<List<DownloadItem>> = dao.getAllDownloads()
    val allTabs: Flow<List<TabItem>> = dao.getAllTabs()

    suspend fun insertHistory(item: HistoryItem) = dao.insertHistory(item)
    suspend fun deleteHistory(item: HistoryItem) = dao.deleteHistory(item)
    suspend fun clearHistory() = dao.clearHistory()

    suspend fun insertBookmark(item: BookmarkItem) = dao.insertBookmark(item)
    suspend fun deleteBookmark(item: BookmarkItem) = dao.deleteBookmark(item)
    suspend fun isBookmarked(url: String): Boolean = dao.isBookmarked(url)
    suspend fun deleteBookmarkByUrl(url: String) = dao.deleteBookmarkByUrl(url)

    suspend fun insertDownload(item: DownloadItem): Long = dao.insertDownload(item)
    suspend fun updateDownload(item: DownloadItem) = dao.updateDownload(item)
    suspend fun deleteDownload(item: DownloadItem) = dao.deleteDownload(item)

    suspend fun insertTab(item: TabItem): Long = dao.insertTab(item)
    suspend fun updateTab(item: TabItem) = dao.updateTab(item)
    suspend fun deleteTab(item: TabItem) = dao.deleteTab(item)
    suspend fun deleteTabsByIncognito(isIncognito: Boolean) = dao.deleteTabsByIncognito(isIncognito)
    suspend fun deactivateAllTabs() = dao.deactivateAllTabs()
    suspend fun activateTab(id: Long) = dao.activateTab(id)
}
