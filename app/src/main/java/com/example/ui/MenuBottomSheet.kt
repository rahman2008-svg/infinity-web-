package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBottomSheet(
    uiState: BrowserUiState,
    onDismiss: () -> Unit,
    onBookmarksClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNightModeToggle: () -> Unit,
    onAdBlockToggle: () -> Unit,
    onDataSaverToggle: () -> Unit,
    onIncognitoToggle: () -> Unit,
    onFindInPageClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddBookmarkClick: () -> Unit,
    onExitClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = if (uiState.isNightMode) Color(0xFF1E1E24) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Drag handle / Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Infinity Web+ Tools",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isNightMode) Color.White else MaterialTheme.colorScheme.onSurface
                )
                
                // Smart AI Copilot banner in Menu
                Button(
                    onClick = onAiAssistantClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "AI",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Infinity AI", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Grid of Actions
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                item {
                    MenuItemCard(
                        title = "Bookmarks",
                        icon = Icons.Default.Favorite,
                        iconTint = Color(0xFFFF4081),
                        onClick = onBookmarksClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "History",
                        icon = Icons.Default.List,
                        iconTint = Color(0xFF00B0FF),
                        onClick = onHistoryClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Downloads",
                        icon = Icons.Default.ArrowDropDown,
                        iconTint = Color(0xFF00E676),
                        badge = if (uiState.downloads.any { it.status == "DOWNLOADING" }) "↓" else null,
                        onClick = onDownloadsClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFF90A4AE),
                        onClick = onSettingsClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                
                // Toggle options row
                item {
                    MenuItemCard(
                        title = "Night Mode",
                        icon = if (uiState.isNightMode) Icons.Default.Star else Icons.Default.Warning,
                        iconTint = if (uiState.isNightMode) Color(0xFFFFD600) else Color(0xFF757575),
                        activeStatus = if (uiState.isNightMode) "ON" else "OFF",
                        onClick = onNightModeToggle,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "AdBlock",
                        icon = Icons.Default.Build,
                        iconTint = if (uiState.isAdBlockEnabled) Color(0xFFFF1744) else Color(0xFF757575),
                        activeStatus = if (uiState.isAdBlockEnabled) "ON" else "OFF",
                        badge = if (uiState.isAdBlockEnabled && uiState.adBlockedCount > 0) "${uiState.adBlockedCount}" else null,
                        onClick = onAdBlockToggle,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Speed Mode",
                        icon = Icons.Default.PlayArrow,
                        iconTint = if (uiState.isDataSaverEnabled) Color(0xFF00E676) else Color(0xFF757575),
                        activeStatus = if (uiState.isDataSaverEnabled) "ON" else "OFF",
                        onClick = onDataSaverToggle,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Incognito",
                        icon = Icons.Default.Lock,
                        iconTint = if (uiState.isIncognitoMode) Color(0xFF212121) else Color(0xFF757575),
                        activeStatus = if (uiState.isIncognitoMode) "ON" else "OFF",
                        onClick = onIncognitoToggle,
                        isNightMode = uiState.isNightMode
                    )
                }

                // Tools row
                item {
                    MenuItemCard(
                        title = "Find in Page",
                        icon = Icons.Default.Search,
                        iconTint = Color(0xFF7E57C2),
                        onClick = onFindInPageClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Share Link",
                        icon = Icons.Default.Share,
                        iconTint = Color(0xFF29B6F6),
                        onClick = onShareClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Add Bookmark",
                        icon = Icons.Default.Add,
                        iconTint = Color(0xFFFFB300),
                        onClick = onAddBookmarkClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "About",
                        icon = Icons.Default.Info,
                        iconTint = Color(0xFFFF7600),
                        onClick = onAboutClick,
                        isNightMode = uiState.isNightMode
                    )
                }
                item {
                    MenuItemCard(
                        title = "Exit",
                        icon = Icons.Default.Close,
                        iconTint = Color(0xFFD32F2F),
                        onClick = onExitClick,
                        isNightMode = uiState.isNightMode
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    activeStatus: String? = null,
    badge: String? = null,
    onClick: () -> Unit,
    isNightMode: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isNightMode) Color(0xFF292930) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with container background
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(iconTint.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = if (isNightMode) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurface
                )

                if (activeStatus != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = activeStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeStatus == "ON") Color(0xFF00E676) else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Notification / Statistics badge
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 6.dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
