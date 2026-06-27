package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*

@Composable
fun BookmarksDialog(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenBookmark: (String) -> Unit,
    onDeleteBookmark: (BookmarkItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bookmarks (বুকমার্কসমূহ)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content
                if (uiState.bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Empty",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো বুকমার্ক সংরক্ষিত নেই",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.bookmarks) { bookmark ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.isNightMode) Color(0xFF292930) else Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenBookmark(bookmark.url)
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bookmark.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (uiState.isNightMode) Color.White else Color.Black
                                        )
                                        Text(
                                            text = bookmark.url,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(onClick = { onDeleteBookmark(bookmark) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDialog(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenHistory: (String) -> Unit,
    onDeleteHistoryItem: (HistoryItem) -> Unit,
    onClearAllHistory: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History (ইতিহাস)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clear All Button
                if (uiState.history.isNotEmpty()) {
                    Button(
                        onClick = onClearAllHistory,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear All")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সব ইতিহাস মুছে ফেলুন (Clear All)", fontSize = 12.sp, color = Color.White)
                    }
                }

                // Content
                if (uiState.history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Empty",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো ব্রাউজিং ইতিহাস নেই",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.history) { history ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.isNightMode) Color(0xFF292930) else Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenHistory(history.url)
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = history.title.ifEmpty { "Web Page" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (uiState.isNightMode) Color.White else Color.Black
                                        )
                                        Text(
                                            text = history.url,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(onClick = { onDeleteHistoryItem(history) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadsDialog(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenDownloadUrl: (String) -> Unit,
    onDeleteDownload: (DownloadItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Downloads (ডাউনলোডসমূহ)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content
                if (uiState.downloads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Empty",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "কোনো ডাউনলোড রেকর্ড নেই",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.downloads) { download ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.isNightMode) Color(0xFF292930) else Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = download.fileName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = if (uiState.isNightMode) Color.White else Color.Black
                                            )
                                            Text(
                                                text = "সাইজ: %.2f MB".format(download.fileSize.toDouble() / (1024 * 1024)),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        
                                        IconButton(onClick = { onDeleteDownload(download) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (download.status == "DOWNLOADING") {
                                        LinearProgressIndicator(
                                            progress = download.progress.toFloat() / 100f,
                                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                                            color = Color(0xFF6200EE)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "ডাউনলোড হচ্ছে... ${download.progress}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF6200EE)
                                            )
                                            Text(
                                                text = "স্পীড: 1.2 MB/s",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (download.status == "COMPLETED") Icons.Default.Check else Icons.Default.Warning,
                                                contentDescription = download.status,
                                                tint = if (download.status == "COMPLETED") Color(0xFF00E676) else Color.Red,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (download.status == "COMPLETED") "ডাউনলোড সম্পন্ন হয়েছে" else "ডাউনলোড ব্যর্থ হয়েছে",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (download.status == "COMPLETED") Color(0xFF00E676) else Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onSearchEngineChange: (String) -> Unit,
    onClearCacheClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    var showEngineMenu by remember { mutableStateOf(false) }
    val searchEngines = listOf("Google", "Bing", "Yahoo", "DuckDuckGo", "Gemini AI")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings (সেটিংস)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Default Search Engine Selector
                Text(
                    text = "Default Search Engine (ডিফল্ট সার্চ ইঞ্জিন)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (uiState.isNightMode) Color.White.copy(alpha = 0.8f) else Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (uiState.isNightMode) Color(0xFF292930) else Color(0xFFF5F5F5),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { showEngineMenu = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.searchEngine,
                            fontSize = 14.sp,
                            color = if (uiState.isNightMode) Color.White else Color.Black
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = showEngineMenu,
                        onDismissRequest = { showEngineMenu = false },
                        modifier = Modifier.background(
                            if (uiState.isNightMode) Color(0xFF292930) else Color.White
                        )
                    ) {
                        searchEngines.forEach { engine ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = engine,
                                        color = if (uiState.isNightMode) Color.White else Color.Black
                                    )
                                },
                                onClick = {
                                    onSearchEngineChange(engine)
                                    showEngineMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // AdBlock Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ad Block (বিজ্ঞাপন প্রতিরোধ)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (uiState.isNightMode) Color.White.copy(alpha = 0.8f) else Color.Black
                    )
                    Text(
                        text = if (uiState.isAdBlockEnabled) "সক্রিয় (Active)" else "নিষ্ক্রিয়",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isAdBlockEnabled) Color(0xFF00E676) else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // About Developer & Company Button
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onAboutClick()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF7600)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF7600)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Info, contentDescription = "About Developer", tint = Color(0xFFFF7600))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("About Developer & Company", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Clear Cache Button
                Button(
                    onClick = {
                        onClearCacheClick()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Cache")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ব্রাউজার ক্যাশ ও ডেটা মুছুন", fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AboutDeveloperDialog(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else Color(0xFFF8FAFC)
            ),
            shape = RoundedCornerShape(28.dp),
            border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header (App & Developer info)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFF7600), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "∞",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Infinity Web+",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                            )
                            Text(
                                text = "Developer & Brand info",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = if (uiState.isNightMode) Color.White else Color(0xFF475569)
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // CARD 1: Developer Profile
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) Color(0xFF292932) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Developer",
                                    tint = Color(0xFFFF7600),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "About Developer",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Prince AR Abdur Rahman",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF7600)
                            )
                            Text(
                                text = "Independent App Developer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.isNightMode) Color.LightGray else Color(0xFF475569),
                                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                            )
                            Text(
                                text = "Prince AR Abdur Rahman is an independent App Developer passionate about building modern Android applications, productivity tools, AI-powered experiences, media players, educational apps, and next-generation digital products.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = if (uiState.isNightMode) Color.LightGray.copy(alpha = 0.9f) else Color(0xFF334155)
                            )
                        }
                    }

                    // CARD 2: Company Profile
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) Color(0xFF292932) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Company",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "About Company",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "NexVora Lab's Ofc",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3B82F6)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "NexVora Lab's Ofc focuses on creating innovative Android applications designed to improve productivity, entertainment, learning, and digital experiences.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = if (uiState.isNightMode) Color.LightGray.copy(alpha = 0.9f) else Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (uiState.isNightMode) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Our Mission:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isNightMode) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
                                    )
                                    Text(
                                        text = "Build fast, beautiful, privacy-friendly, and user-focused applications accessible to everyone.",
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (uiState.isNightMode) Color(0xFFDBEAFE) else Color(0xFF1E40AF)
                                    )
                                }
                            }
                        }
                    }

                    // CARD 3: Social & Contact Links
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) Color(0xFF292932) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Contacts",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Contact & Social Media",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // WhatsApp 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenUrl("https://wa.me/8801707424006") }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFE6F4EA), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("WhatsApp Profile 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B))
                                    Text("01707424006 (Click to Chat)", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            // WhatsApp 2
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenUrl("https://wa.me/8801796951709") }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFE6F4EA), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("WhatsApp Profile 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B))
                                    Text("01796951709 (Click to Chat)", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            // Facebook
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenUrl("https://www.facebook.com/share/1BNn32qoJo/") }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFE8F0FE), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Face, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Facebook Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B))
                                    Text("facebook.com/PrinceARAbdurRahman", fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            // Instagram
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenUrl("https://www.instagram.com/ur___abdur____rahman__2008") }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFFCE7F3), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color(0xFFD946EF), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Instagram Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B))
                                    Text("@ur___abdur____rahman__2008", fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    // CARD 4: Technical Information & Credits
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.isNightMode) Color(0xFF292932) else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = if (uiState.isNightMode) null else BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Technical Information & Credits",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isNightMode) Color.White else Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("App Version", fontSize = 11.sp, color = Color.Gray)
                                Text("1.0.0", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color.Black)
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = if (uiState.isNightMode) Color(0xFF383842) else Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Developed By", fontSize = 11.sp, color = Color.Gray)
                                Text("Prince AR Abdur Rahman", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color.Black)
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = if (uiState.isNightMode) Color(0xFF383842) else Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Published By", fontSize = 11.sp, color = Color.Gray)
                                Text("NexVora Lab's Ofc", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (uiState.isNightMode) Color.White else Color.Black)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "© 2026 NexVora Lab's Ofc. All Rights Reserved.",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7600)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

