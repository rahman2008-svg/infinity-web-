package com.example.ui

import android.content.Intent
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    
    // WebView reference to control back/forward navigation
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var webProgress by remember { mutableStateOf(0) }
    var isWebLoading by remember { mutableStateOf(false) }

    // Intercept hardware Back Press to go back in browser history
    BackHandler(enabled = true) {
        when {
            uiState.showAiCopilotSheet -> viewModel.closeAiAssistant()
            uiState.showTabsManager -> viewModel.setTabsManagerVisible(false)
            uiState.showMenuBottomSheet -> viewModel.setMenuVisible(false)
            uiState.showFindInPage -> viewModel.setFindInPageVisible(false)
            webViewRef?.canGoBack() == true -> webViewRef?.goBack()
            uiState.activeTab?.url != "about:blank" -> viewModel.navigateToUrl("about:blank")
            else -> {
                // If on home, let the activity handle back (exit app)
                (context as? android.app.Activity)?.finish()
            }
        }
    }

    if (uiState.showTabsManager) {
        TabsScreen(
            uiState = uiState,
            onTabSelect = { tabId -> viewModel.selectTab(tabId) },
            onTabClose = { tab -> viewModel.closeTab(tab) },
            onNewTab = { isIncognito -> viewModel.openNewTab(isIncognito = isIncognito) },
            onCloseAll = { viewModel.closeAllTabs() },
            onExitTabsManager = { viewModel.setTabsManagerVisible(false) }
        )
    } else {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (uiState.isNightMode) Color(0xFF16161A) else MaterialTheme.colorScheme.surface)
                        .statusBarsPadding()
                ) {
                    // 1. Sleek Address Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SSL Lock/Incognito Indicator
                        Icon(
                            imageVector = if (uiState.isIncognitoMode) Icons.Default.Lock else Icons.Default.Info,
                            contentDescription = "Security",
                            tint = if (uiState.isIncognitoMode) Color(0xFFFF7600) else Color.Gray,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(18.dp)
                        )

                        // Address Field
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    if (uiState.isNightMode) Color(0xFF292930) else Color(0xFFF2F4F7),
                                    RoundedCornerShape(22.dp)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isIncognitoMode) Color(0xFFFF7600).copy(alpha = 0.5f) else Color.Transparent,
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Real Search Engine icon or logo
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                
                                BasicTextField1(
                                    value = uiState.currentUrlInput,
                                    onValueChange = { viewModel.updateUrlInput(it) },
                                    placeholder = "সার্চ করুন বা URL লিখুন...",
                                    isNightMode = uiState.isNightMode,
                                    onSearch = {
                                        viewModel.navigateToUrl(uiState.currentUrlInput)
                                        keyboardController?.hide()
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                if (uiState.currentUrlInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.updateUrlInput("") },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Bookmark page directly
                        if (uiState.activeTab?.url != "about:blank") {
                            val isCurrentBookmarked = uiState.bookmarks.any { it.url == uiState.activeTab?.url }
                            IconButton(onClick = { viewModel.toggleBookmarkCurrentPage() }) {
                                Icon(
                                    imageVector = if (isCurrentBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isCurrentBookmarked) Color.Red else Color.Gray
                                )
                            }
                        }
                    }

                    // 2. Loading Progress Bar
                    if (isWebLoading && uiState.activeTab?.url != "about:blank") {
                        LinearProgressIndicator(
                            progress = webProgress.toFloat() / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp),
                            color = Color(0xFFFF7600), // UC Orange
                            trackColor = Color.Transparent
                        )
                    }
                }
            },
            bottomBar = {
                // Sleek bottom menu bar matching UC Browser
                Surface(
                    tonalElevation = 8.dp,
                    color = if (uiState.isNightMode) Color(0xFF16161A) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        IconButton(
                            onClick = { webViewRef?.goBack() },
                            enabled = webViewRef?.canGoBack() == true
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (webViewRef?.canGoBack() == true) {
                                    if (uiState.isNightMode) Color.White else Color.Black
                                } else Color.LightGray
                            )
                        }

                        // Forward Button
                        IconButton(
                            onClick = { webViewRef?.goForward() },
                            enabled = webViewRef?.canGoForward() == true
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                tint = if (webViewRef?.canGoForward() == true) {
                                    if (uiState.isNightMode) Color.White else Color.Black
                                } else Color.LightGray
                            )
                        }

                        // Center UC squirrel Mascot / Logo button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF9E00), Color(0xFFFF5E00))
                                    ),
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .clickable { viewModel.setMenuVisible(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Infinity Web Menu",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Tabs Count Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.5.dp,
                                    if (uiState.isNightMode) Color.White else Color.Black,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setTabsManagerVisible(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${uiState.tabs.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isNightMode) Color.White else Color.Black
                            )
                        }

                        // Home Button
                        IconButton(onClick = { viewModel.navigateToUrl("about:blank") }) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (uiState.isNightMode) Color.White else Color.Black
                            )
                        }
                    }
                }
            },
            containerColor = if (uiState.isNightMode) Color(0xFF121214) else Color(0xFFF9FAFC)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Content View
                if (uiState.activeTab == null || uiState.activeTab?.url == "about:blank") {
                    // RENDER RICH CUSTOMIZED HOMEPAGE
                    HomepageContent(
                        uiState = uiState,
                        newsFeed = viewModel.newsFeed,
                        onQuickAccessClick = { url ->
                            if (url == "infinity:ai") {
                                viewModel.openAiAssistant()
                            } else {
                                viewModel.navigateToUrl(url)
                            }
                        },
                        onNewsCategorySelect = { cat -> viewModel.selectNewsCategory(cat) },
                        onNewsItemClick = { newsUrl -> viewModel.navigateToUrl(newsUrl) },
                        onSearchSubmit = { query -> viewModel.navigateToUrl(query) },
                        onToggleDataSaver = { viewModel.toggleDataSaver() },
                        onToggleIncognito = { viewModel.openNewTab(isIncognito = !uiState.isIncognitoMode) }
                    )
                } else {
                    // RENDER ACTIVE WEBVIEW
                    CustomWebView(
                        url = uiState.activeTab?.url ?: "",
                        isAdBlockEnabled = uiState.isAdBlockEnabled,
                        isDataSaverEnabled = uiState.isDataSaverEnabled,
                        onPageStarted = { url ->
                            isWebLoading = true
                            webProgress = 0
                            viewModel.updateUrlInput(url)
                        },
                        onPageFinished = { url, title ->
                            isWebLoading = false
                            webProgress = 100
                            viewModel.onPageFinished(url, title)
                        },
                        onProgressChanged = { progress ->
                            webProgress = progress
                        },
                        onAdBlocked = {
                            viewModel.incrementAdBlocked()
                        },
                        onDownloadRequested = { downloadUrl ->
                            viewModel.startDownload(downloadUrl)
                            Toast.makeText(context, "ডাউনলোড শুরু হয়েছে...", Toast.LENGTH_SHORT).show()
                        },
                        updateWebViewRef = { ref ->
                            webViewRef = ref
                        }
                    )
                }

                // Overlay Dark Filter for Night Mode on top of Web view or screen
                if (uiState.isNightMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                            .clickable(enabled = false) {}
                    )
                }

                // Search Autocomplete Suggestion List dropdown overlay
                if (uiState.currentUrlInput.isNotEmpty() && uiState.searchSuggestions.isNotEmpty() && (uiState.activeTab?.url == "about:blank")) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(uiState.searchSuggestions) { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.navigateToUrl(suggestion)
                                            keyboardController?.hide()
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Suggestion",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = suggestion,
                                        fontSize = 14.sp,
                                        color = if (uiState.isNightMode) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- SHEETS AND DIALOGS ---

    // UC Menu Bottom Sheet
    if (uiState.showMenuBottomSheet) {
        MenuBottomSheet(
            uiState = uiState,
            onDismiss = { viewModel.setMenuVisible(false) },
            onBookmarksClick = {
                viewModel.setMenuVisible(false)
                viewModel.setBookmarksVisible(true)
            },
            onHistoryClick = {
                viewModel.setMenuVisible(false)
                viewModel.setHistoryVisible(true)
            },
            onDownloadsClick = {
                viewModel.setMenuVisible(false)
                viewModel.setDownloadsVisible(true)
            },
            onSettingsClick = {
                viewModel.setMenuVisible(false)
                viewModel.setSettingsVisible(true)
            },
            onNightModeToggle = { viewModel.toggleNightMode() },
            onAdBlockToggle = { viewModel.toggleAdBlock() },
            onDataSaverToggle = { viewModel.toggleDataSaver() },
            onIncognitoToggle = { viewModel.openNewTab(isIncognito = !uiState.isIncognitoMode) },
            onFindInPageClick = {
                viewModel.setMenuVisible(false)
                viewModel.setFindInPageVisible(true)
            },
            onShareClick = {
                viewModel.setMenuVisible(false)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, uiState.activeTab?.url ?: "https://www.google.com")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
            },
            onAddBookmarkClick = {
                viewModel.setMenuVisible(false)
                viewModel.toggleBookmarkCurrentPage()
                Toast.makeText(context, "বুকমার্ক আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            onExitClick = {
                viewModel.setMenuVisible(false)
                (context as? android.app.Activity)?.finish()
            },
            onAiAssistantClick = { viewModel.openAiAssistant() },
            onAboutClick = {
                viewModel.setMenuVisible(false)
                viewModel.setAboutVisible(true)
            }
        )
    }

    // Gemini AI Assistant Bottom Sheet
    if (uiState.showAiCopilotSheet) {
        AiAssistantSheet(
            uiState = uiState,
            onDismiss = { viewModel.closeAiAssistant() },
            onSendMessage = { msg -> viewModel.sendMessageToAi(msg) },
            onSummarizeCurrentPage = {
                viewModel.summarizeCurrentPage(
                    pageTitle = uiState.activeTab?.title ?: "Home",
                    pageUrl = uiState.activeTab?.url ?: "about:blank",
                    injectedText = "We are currently looking at a webpage named ${uiState.activeTab?.title} at address ${uiState.activeTab?.url}."
                )
            }
        )
    }

    // Bookmarks Dialog
    if (uiState.showBookmarksDialog) {
        BookmarksDialog(
            uiState = uiState,
            onDismiss = { viewModel.setBookmarksVisible(false) },
            onOpenBookmark = { url -> viewModel.navigateToUrl(url) },
            onDeleteBookmark = { bookmark -> viewModel.removeBookmark(bookmark) }
        )
    }

    // History Dialog
    if (uiState.showHistoryDialog) {
        HistoryDialog(
            uiState = uiState,
            onDismiss = { viewModel.setHistoryVisible(false) },
            onOpenHistory = { url -> viewModel.navigateToUrl(url) },
            onDeleteHistoryItem = { item -> viewModel.deleteHistoryItem(item) },
            onClearAllHistory = { viewModel.clearHistory() }
        )
    }

    // Downloads Dialog
    if (uiState.showDownloadsDialog) {
        DownloadsDialog(
            uiState = uiState,
            onDismiss = { viewModel.setDownloadsVisible(false) },
            onOpenDownloadUrl = { url -> viewModel.navigateToUrl(url) },
            onDeleteDownload = { item -> viewModel.deleteDownload(item) }
        )
    }

    // Settings Dialog
    if (uiState.showSettingsDialog) {
        SettingsDialog(
            uiState = uiState,
            onDismiss = { viewModel.setSettingsVisible(false) },
            onSearchEngineChange = { engine -> viewModel.changeSearchEngine(engine) },
            onClearCacheClick = {
                viewModel.clearHistory()
                viewModel.closeAllTabs()
                Toast.makeText(context, "ব্রাউজিং ক্যাশ মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            onAboutClick = {
                viewModel.setAboutVisible(true)
            }
        )
    }

    // About Developer Dialog
    if (uiState.showAboutDialog) {
        AboutDeveloperDialog(
            uiState = uiState,
            onDismiss = { viewModel.setAboutVisible(false) },
            onOpenUrl = { url ->
                viewModel.setAboutVisible(false)
                viewModel.navigateToUrl(url)
            }
        )
    }
}

// Basic TextField with text color adjustments for Theme
@Composable
fun BasicTextField1(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isNightMode: Boolean,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        textStyle = LocalTextStyle.current.copy(
            color = if (isNightMode) Color.White else Color.Black,
            fontSize = 14.sp
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            innerTextField()
        }
    )
}

// Custom Visually Stunning Bento-Style Homepage
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomepageContent(
    uiState: BrowserUiState,
    newsFeed: List<NewsItem>,
    onQuickAccessClick: (String) -> Unit,
    onNewsCategorySelect: (String) -> Unit,
    onNewsItemClick: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onToggleDataSaver: () -> Unit,
    onToggleIncognito: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (uiState.isNightMode) Color(0xFF121214) else Color(0xFFF0F2F5)
            )
    ) {
        // 1. App logo / mascot & greeting section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFF7600), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "∞",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Infinity Web+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(
                    text = "High Speed Mobile Browsing & AI Copilot",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // 2. Large Interactive Search Field (Google-Style)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFFF7600),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        placeholder = { Text("গুগল বা যেকোনো সাইটে সার্চ করুন...", fontSize = 14.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = if (uiState.isNightMode) Color.White else Color.Black,
                            unfocusedTextColor = if (uiState.isNightMode) Color.White else Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchInput.trim().isNotEmpty()) {
                                    onSearchSubmit(searchInput)
                                    keyboardController?.hide()
                                }
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = { searchInput = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
            }
        }

        // 3. Quick Access Bento Grid
        item {
            val items = listOf(
                "Google" to "https://www.google.com",
                "YouTube" to "https://m.youtube.com",
                "Facebook" to "https://m.facebook.com",
                "Cricbuzz" to "https://www.cricbuzz.com",
                "Wikipedia" to "https://en.wikipedia.org",
                "TechCrunch" to "https://techcrunch.com",
                "BBC News" to "https://www.bbc.com/news",
                "Infinity AI" to "infinity:ai"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Quick Access",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render Bento grid style layout
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (row in 0 until 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val (name, url) = items[index]
                                val isAi = name == "Infinity AI"

                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else Color.White
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onQuickAccessClick(url) }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(
                                                    if (uiState.isNightMode) Color(0xFF2D2D34) else Color(0xFFF1F5F9),
                                                    RoundedCornerShape(12.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isAi) {
                                                Text(
                                                    text = "∞",
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFFFF7600)
                                                )
                                            } else {
                                                val (letter, color) = when (name) {
                                                    "Google" -> "G" to Color(0xFF4285F4)
                                                    "YouTube" -> "Y" to Color(0xFFFF0000)
                                                    "Facebook" -> "f" to Color(0xFF1877F2)
                                                    "Cricbuzz" -> "C" to Color(0xFF15803D)
                                                    "Wikipedia" -> "W" to Color(0xFF0F172A)
                                                    "TechCrunch" -> "T" to Color(0xFF0284C7)
                                                    "BBC News" -> "B" to Color(0xFFDC2626)
                                                    else -> name.take(1) to Color(0xFFFF7600)
                                                }
                                                Text(
                                                    text = letter,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (uiState.isNightMode && color == Color(0xFF0F172A)) Color.White else color
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (uiState.isNightMode) Color.White else Color(0xFF475569),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Bento Grid Quick Action Cards (Speed Mode and Private Mode side-by-side)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Speed Mode (Data Saver) Bento Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isNightMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.isNightMode) Color(0xFF334155) else Color(0xFFDBEAFE)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleDataSaver() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Speed Mode",
                            tint = if (uiState.isNightMode) Color(0xFF60A5FA) else Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Speed Mode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isNightMode) Color(0xFFDBEAFE) else Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.isDataSaverEnabled) "Data saving: 45%" else "Data Saver: Off",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isNightMode) Color(0xFF93C5FD) else Color(0xFF2563EB)
                        )
                    }
                }

                // Private (Incognito) Bento Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isNightMode) Color(0xFF3B0764).copy(alpha = 0.35f) else Color(0xFFFAF5FF)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        1.dp,
                        if (uiState.isNightMode) Color(0xFF581C87) else Color(0xFFF3E8FF)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleIncognito() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Private Mode",
                            tint = if (uiState.isNightMode) Color(0xFFC084FC) else Color(0xFF7C3AED),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Private Mode",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isNightMode) Color(0xFFF5F3FF) else Color(0xFF4C1D95)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.isIncognitoMode) "Incognito is On" else "Incognito is Off",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isNightMode) Color(0xFFE9D5FF) else Color(0xFF7C3AED)
                        )
                    }
                }
            }
        }

        // 5. Personalized News Feed Bento Card Section
        item {
            val categories = listOf("All", "Cricket", "Tech", "News", "Entertainment")
            val newsItems = if (uiState.selectedNewsCategory == "All") {
                newsFeed
            } else {
                newsFeed.filter { it.category == uiState.selectedNewsCategory }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else Color.White
                ),
                shape = RoundedCornerShape(28.dp),
                border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Trending News",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                        )
                        
                        Text(
                            text = "See All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF7600),
                            modifier = Modifier.clickable { /* No-op, just visual alignment */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // News Category Tabs Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = uiState.selectedNewsCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNewsCategorySelect(cat) }
                                    .background(
                                        if (isSelected) Color(0xFFFF7600) else {
                                            if (uiState.isNightMode) Color(0xFF2D2D34) else Color(0xFFF1F5F9)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else {
                                        if (uiState.isNightMode) Color.White else Color(0xFF475569)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // News Articles inside the Bento card
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        newsItems.forEachIndexed { idx, news ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onNewsItemClick(news.url) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = news.imageUrl,
                                    contentDescription = news.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = news.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 18.sp,
                                        color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${news.source} • ${news.time}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    Color(0xFFFF7600).copy(alpha = 0.15f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = news.category,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF7600)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            if (idx < newsItems.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(if (uiState.isNightMode) Color(0xFF2D2D34) else Color(0xFFF1F5F9))
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
