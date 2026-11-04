package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanBorder
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceContainer
import com.example.ui.theme.JarvisSurfaceContainerHigh
import com.example.ui.theme.JarvisSurfaceContainerHighest
import com.example.ui.theme.JarvisSurfaceContainerLow

data class ConnectedApp(
    val name: String,
    val packageName: String,
    val status: String,
    val icon: ImageVector,
    val defaultCmd: String
)

data class ShoppingItem(
    val id: String,
    val title: String,
    val retailer: String,
    val price: String,
    val category: String
)

@Composable
fun AppsScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRetailer by remember { mutableStateOf("All Retailers") }

    val connectedApps = remember {
        listOf(
            ConnectedApp("Amazon", "com.amazon.mShop.android.shopping", "Auto-buy ready", Icons.Default.ShoppingBag, "Open Amazon and search for wireless earbuds"),
            ConnectedApp("Flipkart", "com.flipkart.android", "Sync active", Icons.Default.LocalMall, "Open Flipkart and find laptops"),
            ConnectedApp("Meesho", "com.meesho.supply", "Automation ready", Icons.Default.Storefront, "Launch Meesho deals for shoes"),
            ConnectedApp("YouTube", "com.google.android.youtube", "Media feed linked", Icons.Default.PlayCircle, "Search Python programming tutorial on YouTube"),
            ConnectedApp("Phone Dialer", "com.android.dialer", "Call uplink ready", Icons.Default.Phone, "Call mom via phone"),
            ConnectedApp("Spotify", "com.spotify.music", "Call & audio ready", Icons.Default.Headphones, "Play music on Spotify")
        )
    }

    val products = remember {
        listOf(
            ShoppingItem("1", "Quantum Pods X", "Amazon", "$79.99", "Audio"),
            ShoppingItem("2", "CyberDeck Core i9", "Flipkart", "$1,299.00", "Laptops"),
            ShoppingItem("3", "Aero Pulse High-Tops", "Meesho", "$45.00", "Footwear"),
            ShoppingItem("4", "Neural Sync Band", "Amazon", "$149.50", "Wearables")
        )
    }

    val filteredProducts = products.filter { item ->
        val matchesRetailer = (selectedRetailer == "All Retailers") || item.retailer.equals(selectedRetailer, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true) || item.retailer.contains(searchQuery, ignoreCase = true)
        matchesRetailer && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisSurface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Telemetry Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JarvisSurfaceContainerLow)
                    .border(1.dp, JarvisCyanBorder.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Neural Hub",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "NEURAL LINK // ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(JarvisCyan.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(JarvisCyan)
                        )
                        Text(
                            text = "SYNCED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JarvisCyan
                        )
                    }
                }

                Text(
                    text = "Voice automation pipeline active across 7 installed neural nodes. Ready for hands-free shopping and app orchestration.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section: Connected Applications
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONNECTED APPLICATIONS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "6 NODES ONLINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JarvisCyan
                    )
                }

                // Grid of 2 columns
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (i in connectedApps.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppNodeCard(
                                modifier = Modifier.weight(1f),
                                app = connectedApps[i],
                                onVoiceCmd = { viewModel.handleUserInput(connectedApps[i].defaultCmd) },
                                testTag = "app_card_${connectedApps[i].name.lowercase()}"
                            )
                            if (i + 1 < connectedApps.size) {
                                AppNodeCard(
                                    modifier = Modifier.weight(1f),
                                    app = connectedApps[i + 1],
                                    onVoiceCmd = { viewModel.handleUserInput(connectedApps[i + 1].defaultCmd) },
                                    testTag = "app_card_${connectedApps[i + 1].name.lowercase()}"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Shopping Assistant
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shopping",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "SHOPPING ASSISTANT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "AI DISCOVERY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = JarvisCyan
                    )
                }

                // Search Bar with voice trigger
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shopping_search_input"),
                    placeholder = {
                        Text(
                            "Search products across retailers...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = JarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleVoiceListening() }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice product query",
                                tint = JarvisCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = JarvisSurfaceContainer,
                        unfocusedContainerColor = JarvisSurfaceContainer,
                        focusedBorderColor = JarvisCyanBorder,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Retailer Tabs
                val retailers = listOf("All Retailers", "Amazon", "Flipkart", "Meesho")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(retailers) { ret ->
                        val isSelected = selectedRetailer == ret
                        val bg = if (isSelected) JarvisCyan else JarvisSurfaceContainer
                        val textColor = if (isSelected) Color(0xFF00373A) else MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .clickable { selectedRetailer = ret }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("retailer_tab_${ret.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ret,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = textColor
                            )
                        }
                    }
                }

                // Product Cards Grid
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (i in filteredProducts.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProductCard(
                                modifier = Modifier.weight(1f),
                                product = filteredProducts[i],
                                onVoiceBuy = {
                                    viewModel.handleUserInput("Search for ${filteredProducts[i].title} on ${filteredProducts[i].retailer}")
                                },
                                testTag = "product_card_${filteredProducts[i].id}"
                            )
                            if (i + 1 < filteredProducts.size) {
                                ProductCard(
                                    modifier = Modifier.weight(1f),
                                    product = filteredProducts[i + 1],
                                    onVoiceBuy = {
                                        viewModel.handleUserInput("Search for ${filteredProducts[i + 1].title} on ${filteredProducts[i + 1].retailer}")
                                    },
                                    testTag = "product_card_${filteredProducts[i + 1].id}"
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AppNodeCard(
    modifier: Modifier = Modifier,
    app: ConnectedApp,
    onVoiceCmd: () -> Unit,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(JarvisSurfaceContainer)
            .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(JarvisSurfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.name,
                    tint = JarvisCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(JarvisCyan)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = app.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = app.status,
            style = MaterialTheme.typography.bodySmall,
            color = JarvisCyan.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(JarvisCyan.copy(alpha = 0.1f))
                .clickable(onClick = onVoiceCmd)
                .padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Cmd",
                tint = JarvisCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Voice Cmd",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = JarvisCyan
            )
        }
    }
}

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ShoppingItem,
    onVoiceBuy: () -> Unit,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(JarvisSurfaceContainer)
            .border(1.dp, JarvisCyanBorder.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(JarvisSurfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = product.title,
                tint = JarvisCyan,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = product.retailer,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = JarvisCyan,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(JarvisSurface.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            text = product.price,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = JarvisCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(JarvisCyan)
                .clickable(onClick = onVoiceBuy)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Search & View",
                tint = Color(0xFF00373A),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Voice-Search",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00373A)
            )
        }
    }
}
