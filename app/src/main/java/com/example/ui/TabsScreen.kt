package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TabItem

@Composable
fun TabsScreen(
    uiState: BrowserUiState,
    onTabSelect: (Long) -> Unit,
    onTabClose: (TabItem) -> Unit,
    onNewTab: (Boolean) -> Unit, // passes isIncognito
    onCloseAll: () -> Unit,
    onExitTabsManager: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(if (uiState.isNightMode) Color(0xFF16161A) else MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tabs (${uiState.tabs.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onCloseAll) {
                        Text(
                            text = "Close All",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    IconButton(onClick = onExitTabsManager) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Tabs Manager",
                            tint = if (uiState.isNightMode) Color.White else Color.Black
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(if (uiState.isNightMode) Color(0xFF1E1E24) else Color(0xFFF5F5FA))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Regular New Tab
                Button(
                    onClick = { onNewTab(false) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF7600) // UC Orange brand
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Tab", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Tab", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Incognito New Tab
                Button(
                    onClick = { onNewTab(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF212121) // Stealth black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "New Incognito Tab", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Incognito", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = if (uiState.isNightMode) Color(0xFF121214) else Color(0xFFEBEFF5)
    ) { innerPadding ->
        if (uiState.tabs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোনো ট্যাব খোলা নেই। নতুন ট্যাব খুলতে নিচে চাপুন।",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(uiState.tabs, key = { it.id }) { tab ->
                    val isActive = tab.isActive
                    val isTabIncognito = tab.isIncognito
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTabIncognito) {
                                Color(0xFF2C2C35)
                            } else {
                                if (uiState.isNightMode) Color(0xFF1E1E24) else Color.White
                            }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = if (isActive) {
                            BorderStroke(2.dp, Color(0xFFFF7600))
                        } else {
                            if (isTabIncognito) BorderStroke(1.dp, Color.DarkGray) else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onTabSelect(tab.id) }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Screen visual placeholder representing page status
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .then(
                                            if (isTabIncognito) {
                                                Modifier.background(Brush.verticalGradient(listOf(Color(0xFF202026), Color(0xFF121215))))
                                            } else {
                                                Modifier.background(if (uiState.isNightMode) Color(0xFF25252C) else Color(0xFFF2F4F7))
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isTabIncognito) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = "Incognito Tab",
                                            tint = Color(0xFFFF7600),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    } else {
                                        Text(
                                            text = if (tab.url == "about:blank") "Home" else tab.url.take(15),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (uiState.isNightMode) Color.White.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                // Bottom Tab Info
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = if (tab.url == "about:blank") "Infinity Web+" else tab.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isNightMode || isTabIncognito) Color.White else Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (tab.url == "about:blank") "Home Page" else tab.url,
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Close button in upper right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .clip(CircleShape)
                                    .clickable { onTabClose(tab) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close Tab",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
