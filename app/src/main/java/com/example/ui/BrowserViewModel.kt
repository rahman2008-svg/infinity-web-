package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class QuickAccessItem(
    val name: String,
    val url: String,
    val category: String = "Web",
    val isSystem: Boolean = false
)

data class NewsItem(
    val id: Int,
    val title: String,
    val source: String,
    val time: String,
    val url: String,
    val category: String,
    val imageUrl: String
)

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class BrowserUiState(
    val tabs: List<TabItem> = emptyList(),
    val activeTab: TabItem? = null,
    val bookmarks: List<BookmarkItem> = emptyList(),
    val history: List<HistoryItem> = emptyList(),
    val downloads: List<DownloadItem> = emptyList(),
    val isNightMode: Boolean = false,
    val isAdBlockEnabled: Boolean = true,
    val isDataSaverEnabled: Boolean = false, // Speed Mode
    val isIncognitoMode: Boolean = false,
    val searchEngine: String = "Google", // Google, Bing, Yahoo, DuckDuckGo, Gemini AI
    val adBlockedCount: Int = 0,
    val currentUrlInput: String = "",
    val isSearching: Boolean = false,
    val searchSuggestions: List<String> = emptyList(),
    
    // Dialog / Sheet Visibility States
    val showTabsManager: Boolean = false,
    val showMenuBottomSheet: Boolean = false,
    val showBookmarksDialog: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val showDownloadsDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val showFindInPage: Boolean = false,
    val findQuery: String = "",
    
    // AI Copilot State
    val showAiCopilotSheet: Boolean = false,
    val aiChatHistory: List<ChatMessage> = listOf(
        ChatMessage("ai", "স্বাগতম! আমি Infinity AI, আপনার ব্রাউজিং সহকারী। আপনি যেকোনো ওয়েবসাইটের বিষয়বস্তু সারসংক্ষেপ করতে পারেন অথবা যেকোনো প্রশ্ন করতে পারেন।")
    ),
    val aiLoading: Boolean = false,
    
    // Active News Category
    val selectedNewsCategory: String = "All"
)

class BrowserViewModel(
    application: Application,
    private val repository: BrowserRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    // Default speed dial (quick access) links
    val quickAccessList = listOf(
        QuickAccessItem("Google", "https://www.google.com"),
        QuickAccessItem("Facebook", "https://m.facebook.com"),
        QuickAccessItem("YouTube", "https://m.youtube.com"),
        QuickAccessItem("Cricbuzz", "https://www.cricbuzz.com"),
        QuickAccessItem("Wikipedia", "https://en.wikipedia.org"),
        QuickAccessItem("TechCrunch", "https://techcrunch.com"),
        QuickAccessItem("BBC News", "https://www.bbc.com/news"),
        QuickAccessItem("Infinity AI", "infinity:ai", isSystem = true)
    )

    // Rich UC Browser News Feed (High Visual Fidelity)
    val newsFeed = listOf(
        NewsItem(1, "বাংলাদেশ ক্রিকেটের নতুন যুগ: সিরিজ জয়ে নতুন উচ্চতায় টাইগাররা", "Cricbuzz", "১ ঘণ্টা আগে", "https://www.cricbuzz.com", "Cricket", "https://picsum.photos/id/102/300/200"),
        NewsItem(2, "Gemini 3.5 Flash-এর চমৎকার ফিচার প্রকাশ: এআই প্রযুক্তিতে বৈপ্লবিক পরিবর্তন", "TechCrunch", "২ ঘণ্টা আগে", "https://techcrunch.com", "Tech", "https://picsum.photos/id/103/300/200"),
        NewsItem(3, "ঢাকায় গ্রীন এনার্জি ট্রানজিশন সাবমিট ২০২৬ শুরু: জলবায়ু পরিবর্তন মোকাবেলার রূপরেখা", "The Daily Star", "৩ ঘণ্টা আগে", "https://www.thedailystar.net", "News", "https://picsum.photos/id/104/300/200"),
        NewsItem(4, "এআই এবং কোয়ান্টাম কম্পিউটিংয়ে ট্রিলিয়ন ডলারের নতুন বৈশ্বিক চুক্তি", "Wired", "৪ ঘণ্টা আগে", "https://www.wired.com", "Tech", "https://picsum.photos/id/106/300/200"),
        NewsItem(5, "আসন্ন সায়েন্স ফিকশন থ্রিলার চলচ্চিত্র ‘স্টারডাস্ট’ অবমুক্তির ঘোষণা", "IMDb", "৫ ঘণ্টা আগে", "https://www.imdb.com", "Entertainment", "https://picsum.photos/id/107/300/200"),
        NewsItem(6, "উইম্বলডন ২০২৬: প্রথম রাউন্ডে ফেভারিটদের দাপুটে জয়", "ESPN", "৬ ঘণ্টা আগে", "https://www.espncricinfo.com", "Cricket", "https://picsum.photos/id/108/300/200"),
        NewsItem(7, "স্মার্টফোনের ব্যাটারি লাইফ দ্বিগুণ করার নতুন ন্যানো-প্রযুক্তি আবিষ্কার", "BBC News", "১২ ঘণ্টা আগে", "https://www.bbc.com/news", "Tech", "https://picsum.photos/id/109/300/200"),
        NewsItem(8, "বাংলা একাডেমির নতুন বইমেলার সময়সূচী ও বিস্তারিত আয়োজন", "Prothom Alo", "১ দিন আগে", "https://www.prothomalo.com", "News", "https://picsum.photos/id/110/300/200")
    )

    init {
        // Collect DB resources reactively
        viewModelScope.launch {
            repository.allTabs.collect { tabsList ->
                val active = tabsList.find { it.isActive }
                
                // If tabs are empty on startup, insert a default Home tab
                if (tabsList.isEmpty()) {
                    val defaultTab = TabItem(
                        url = "about:blank",
                        title = "Infinity Web+",
                        isActive = true
                    )
                    repository.insertTab(defaultTab)
                } else {
                    _uiState.update { state ->
                        state.copy(
                            tabs = tabsList,
                            activeTab = active ?: tabsList.firstOrNull()?.also {
                                viewModelScope.launch { repository.activateTab(it.id) }
                            },
                            isIncognitoMode = active?.isIncognito ?: false
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.allBookmarks.collect { bookmarksList ->
                _uiState.update { it.copy(bookmarks = bookmarksList) }
            }
        }

        viewModelScope.launch {
            repository.allHistory.collect { historyList ->
                _uiState.update { it.copy(history = historyList) }
            }
        }

        viewModelScope.launch {
            repository.allDownloads.collect { downloadsList ->
                _uiState.update { it.copy(downloads = downloadsList) }
            }
        }
    }

    // Tab Management
    fun openNewTab(url: String = "about:blank", isIncognito: Boolean = false) {
        viewModelScope.launch {
            repository.deactivateAllTabs()
            val newTab = TabItem(
                url = url,
                title = if (url == "about:blank") "Infinity Web+" else "Loading...",
                isIncognito = isIncognito,
                isActive = true
            )
            repository.insertTab(newTab)
            _uiState.update { state ->
                state.copy(
                    isIncognitoMode = isIncognito,
                    currentUrlInput = if (url == "about:blank") "" else url
                )
            }
        }
    }

    fun selectTab(tabId: Long) {
        viewModelScope.launch {
            repository.deactivateAllTabs()
            repository.activateTab(tabId)
            val selected = _uiState.value.tabs.find { it.id == tabId }
            _uiState.update { state ->
                state.copy(
                    activeTab = selected,
                    isIncognitoMode = selected?.isIncognito ?: false,
                    currentUrlInput = if (selected?.url == "about:blank") "" else (selected?.url ?: ""),
                    showTabsManager = false
                )
            }
        }
    }

    fun closeTab(tab: TabItem) {
        viewModelScope.launch {
            repository.deleteTab(tab)
            // If we closed the active tab, switch active status to another tab
            if (tab.isActive) {
                val remaining = _uiState.value.tabs.filter { it.id != tab.id }
                if (remaining.isNotEmpty()) {
                    selectTab(remaining.first().id)
                } else {
                    // Create default blank tab
                    openNewTab()
                }
            }
        }
    }

    fun closeAllTabs(isIncognitoOnly: Boolean = false) {
        viewModelScope.launch {
            if (isIncognitoOnly) {
                repository.deleteTabsByIncognito(true)
            } else {
                _uiState.value.tabs.forEach { repository.deleteTab(it) }
                openNewTab()
            }
        }
    }

    // URL Navigation & Search
    fun navigateToUrl(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        val finalUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> {
                // Perform search based on default search engine
                val encodedQuery = java.net.URLEncoder.encode(trimmed, "UTF-8")
                when (_uiState.value.searchEngine) {
                    "Bing" -> "https://www.bing.com/search?q=$encodedQuery"
                    "Yahoo" -> "https://search.yahoo.com/search?p=$encodedQuery"
                    "DuckDuckGo" -> "https://duckduckgo.com/?q=$encodedQuery"
                    "Gemini AI" -> {
                        // Open built-in AI assistant and execute query
                        openAiAssistant()
                        sendMessageToAi(trimmed)
                        return
                    }
                    else -> "https://www.google.com/search?q=$encodedQuery"
                }
            }
        }

        viewModelScope.launch {
            _uiState.value.activeTab?.let { active ->
                val updated = active.copy(url = finalUrl, title = "Loading...")
                repository.updateTab(updated)
                _uiState.update { it.copy(activeTab = updated, currentUrlInput = finalUrl) }
            }
        }
    }

    fun updateUrlInput(url: String) {
        _uiState.update { it.copy(currentUrlInput = url) }
        
        // Search suggestions based on common sites and input
        if (url.isNotEmpty()) {
            val suggestions = listOf(
                "google.com", "facebook.com", "youtube.com", "wikipedia.org", "cricbuzz.com",
                "news in bangladesh", "cricket live score", "tech news today", "gemini ai assistant"
            ).filter { it.contains(url, ignoreCase = true) }
            _uiState.update { it.copy(searchSuggestions = suggestions) }
        } else {
            _uiState.update { it.copy(searchSuggestions = emptyList()) }
        }
    }

    fun onPageFinished(url: String, title: String) {
        viewModelScope.launch {
            _uiState.value.activeTab?.let { active ->
                if (active.url != url || active.title != title) {
                    val updated = active.copy(url = url, title = title)
                    repository.updateTab(updated)
                }
            }

            // Save to history if not incognito
            if (!_uiState.value.isIncognitoMode && url != "about:blank" && !url.startsWith("infinity:")) {
                repository.insertHistory(HistoryItem(url = url, title = title))
            }
        }
    }

    // Bookmarks
    fun toggleBookmarkCurrentPage() {
        val active = _uiState.value.activeTab ?: return
        if (active.url == "about:blank" || active.url.startsWith("infinity:")) return

        viewModelScope.launch {
            val alreadyBookmarked = repository.isBookmarked(active.url)
            if (alreadyBookmarked) {
                repository.deleteBookmarkByUrl(active.url)
            } else {
                repository.insertBookmark(BookmarkItem(url = active.url, title = active.title))
            }
        }
    }

    fun addBookmark(title: String, url: String) {
        viewModelScope.launch {
            repository.insertBookmark(BookmarkItem(url = url, title = title))
        }
    }

    fun removeBookmark(item: BookmarkItem) {
        viewModelScope.launch {
            repository.deleteBookmark(item)
        }
    }

    // History
    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            repository.deleteHistory(item)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Simulated Downloads Manager (high fidelity, responsive)
    fun startDownload(url: String) {
        val uri = android.net.Uri.parse(url)
        val fileName = uri.lastPathSegment ?: "downloaded_file_${System.currentTimeMillis() % 10000}.bin"
        
        viewModelScope.launch {
            val downloadId = repository.insertDownload(
                DownloadItem(
                    url = url,
                    fileName = fileName,
                    filePath = "/sdcard/Downloads/InfinityWeb+/$fileName",
                    fileSize = (1024 * 1024 * (2 + (Math.random() * 8))).toLong(), // 2MB to 10MB
                    progress = 0,
                    status = "DOWNLOADING"
                )
            )

            // Simulate file download increment
            viewModelScope.launch(Dispatchers.Default) {
                var progress = 0
                while (progress < 100) {
                    delay(500)
                    progress += (10..25).random()
                    if (progress > 100) progress = 100
                    
                    val currentProgress = progress
                    withContext(Dispatchers.Main) {
                        val items = _uiState.value.downloads
                        val existing = items.find { it.id == downloadId }
                        if (existing != null) {
                            val updated = existing.copy(
                                progress = currentProgress,
                                status = if (currentProgress == 100) "COMPLETED" else "DOWNLOADING"
                            )
                            repository.updateDownload(updated)
                        }
                    }
                }
            }
        }
    }

    fun deleteDownload(item: DownloadItem) {
        viewModelScope.launch {
            repository.deleteDownload(item)
        }
    }

    // Settings / UC Tools
    fun toggleNightMode() {
        _uiState.update { it.copy(isNightMode = !it.isNightMode) }
    }

    fun toggleAdBlock() {
        _uiState.update { it.copy(isAdBlockEnabled = !it.isAdBlockEnabled) }
    }

    fun toggleDataSaver() {
        _uiState.update { it.copy(isDataSaverEnabled = !it.isDataSaverEnabled) }
    }

    fun changeSearchEngine(engine: String) {
        _uiState.update { it.copy(searchEngine = engine) }
    }

    fun incrementAdBlocked() {
        if (_uiState.value.isAdBlockEnabled) {
            _uiState.update { it.copy(adBlockedCount = it.adBlockedCount + 1) }
        }
    }

    fun selectNewsCategory(category: String) {
        _uiState.update { it.copy(selectedNewsCategory = category) }
    }

    // UI Controls Visibility
    fun setMenuVisible(visible: Boolean) {
        _uiState.update { it.copy(showMenuBottomSheet = visible) }
    }

    fun setTabsManagerVisible(visible: Boolean) {
        _uiState.update { it.copy(showTabsManager = visible) }
    }

    fun setBookmarksVisible(visible: Boolean) {
        _uiState.update { it.copy(showBookmarksDialog = visible) }
    }

    fun setHistoryVisible(visible: Boolean) {
        _uiState.update { it.copy(showHistoryDialog = visible) }
    }

    fun setDownloadsVisible(visible: Boolean) {
        _uiState.update { it.copy(showDownloadsDialog = visible) }
    }

    fun setSettingsVisible(visible: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = visible) }
    }

    fun setAboutVisible(visible: Boolean) {
        _uiState.update { it.copy(showAboutDialog = visible) }
    }

    fun setFindInPageVisible(visible: Boolean) {
        _uiState.update { it.copy(showFindInPage = visible) }
    }

    fun setFindQuery(query: String) {
        _uiState.update { it.copy(findQuery = query) }
    }

    // Gemini AI Assistant Integration (Copilot)
    fun openAiAssistant() {
        _uiState.update { it.copy(showAiCopilotSheet = true, showMenuBottomSheet = false) }
    }

    fun closeAiAssistant() {
        _uiState.update { it.copy(showAiCopilotSheet = false) }
    }

    fun sendMessageToAi(text: String) {
        if (text.trim().isEmpty()) return
        
        val userMsg = ChatMessage("user", text)
        _uiState.update { state ->
            state.copy(
                aiChatHistory = state.aiChatHistory + userMsg,
                aiLoading = true
            )
        }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _uiState.update { state ->
                        state.copy(
                            aiChatHistory = state.aiChatHistory + ChatMessage("ai", "ত্রুটি: Gemini API Key সেট করা নেই! অনুগ্রহ করে AI Studio-এর Secrets প্যানেল থেকে আপনার GEMINI_API_KEY কনফিগার করুন।"),
                            aiLoading = false
                        )
                    }
                    return@launch
                }

                val currentHistory = _uiState.value.aiChatHistory
                // Build a short chat history for context
                val contents = currentHistory.map { msg ->
                    GeminiContent(parts = listOf(GeminiPart(text = msg.text)))
                }

                val systemInstruction = GeminiContent(parts = listOf(GeminiPart(
                    text = "You are Infinity AI, the smart web browser assistant inside the 'Infinity Web+' app (inspired by UC Browser). " +
                           "You must help the user with questions, searches, summarizing content, explaining articles, etc. " +
                           "Speak in Bangla (Bengali) naturally and helpfully unless the user queries in English. Be polite and concise."
                )))

                val request = GeminiRequest(
                    contents = contents,
                    systemInstruction = systemInstruction
                )

                val response = withContext(Dispatchers.IO) {
                    GeminiClient.api.generateContent(apiKey, request)
                }

                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "আমি দুঃখিত, আমি এই অনুরোধটি বুঝতে পারছি না।"

                _uiState.update { state ->
                    state.copy(
                        aiChatHistory = state.aiChatHistory + ChatMessage("ai", aiText),
                        aiLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        aiChatHistory = state.aiChatHistory + ChatMessage("ai", "দুঃখিত, সংযোগে সমস্যা হয়েছে: ${e.localizedMessage}"),
                        aiLoading = false
                    )
                }
            }
        }
    }

    fun summarizeCurrentPage(pageTitle: String, pageUrl: String, injectedText: String = "") {
        openAiAssistant()
        _uiState.update { state ->
            state.copy(aiLoading = true)
        }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _uiState.update { state ->
                        state.copy(
                            aiChatHistory = state.aiChatHistory + ChatMessage("ai", "ত্রুটি: Gemini API Key কনফিগার করা নেই।"),
                            aiLoading = false
                        )
                    }
                    return@launch
                }

                val prompt = "অনুগ্রহ করে এই ওয়েব পেজটি সুন্দরভাবে বাংলায় সারসংক্ষেপ করে দিন:\n" +
                        "শিরোনাম: $pageTitle\n" +
                        "লিংক: $pageUrl\n" +
                        if (injectedText.isNotEmpty()) "তথ্য: $injectedText" else ""

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(
                        text = "You are a professional summarizer. Summarize the webpage content in Bangla with clear bullet points."
                    )))
                )

                val response = withContext(Dispatchers.IO) {
                    GeminiClient.api.generateContent(apiKey, request)
                }

                val summary = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "সারসংক্ষেপ তৈরি করা সম্ভব হয়নি।"

                _uiState.update { state ->
                    state.copy(
                        aiChatHistory = state.aiChatHistory + ChatMessage("ai", summary),
                        aiLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        aiChatHistory = state.aiChatHistory + ChatMessage("ai", "সারসংক্ষেপ করতে সমস্যা হয়েছে: ${e.localizedMessage}"),
                        aiLoading = false
                    )
                }
            }
        }
    }
}

class BrowserViewModelFactory(
    private val application: Application,
    private val repository: BrowserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowserViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
