package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.NewsArticle

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedCategory by remember { mutableStateOf("Tümü") }
    var selectedYearFilter by remember { mutableStateOf<Int?>(null) }

    val categories = listOf("Tümü", "Teknoloji", "Sektör", "Pazar", "Şirket")
    
    // Extract distinct years present in newsList
    val availableYears = remember(state.newsList) {
        state.newsList.map { it.year }.distinct().sortedDescending()
    }

    val filteredNews = remember(state.newsList, selectedCategory, selectedYearFilter) {
        state.newsList
            .filter { article ->
                (selectedCategory == "Tümü" || article.category == selectedCategory) &&
                (selectedYearFilter == null || article.year == selectedYearFilter)
            }
            .sortedWith(compareByDescending<NewsArticle> { it.year }.thenByDescending { it.month })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Pazar & Sektör Haberleri",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Text(
                text = "Yıllara ve gelişmelere göre teknoloji akışı",
                fontSize = 12.sp,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Year Filter Chips
            if (availableYears.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Yıl:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate600)
                    
                    FilterChip(
                        selected = selectedYearFilter == null,
                        onClick = { selectedYearFilter = null },
                        label = { Text("Tüm Yıllar", fontSize = 11.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    availableYears.forEach { yr ->
                        FilterChip(
                            selected = selectedYearFilter == yr,
                            onClick = { selectedYearFilter = yr },
                            label = { Text(yr.toString(), fontSize = 11.sp) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        if (filteredNews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bu filtreye ait henüz bir haber yok.",
                    color = Slate600,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNews, key = { it.id }) { news ->
                    NewsItemCard(news)
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(news: NewsArticle) {
    val categoryBgColor = when (news.category) {
        "Teknoloji" -> MaterialTheme.colorScheme.primaryContainer
        "Sektör" -> MaterialTheme.colorScheme.secondaryContainer
        "Pazar" -> MaterialTheme.colorScheme.tertiaryContainer
        "Şirket" -> Color(0xFFE8F5E9)
        else -> Slate200
    }

    val categoryTextColor = when (news.category) {
        "Teknoloji" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Sektör" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Pazar" -> MaterialTheme.colorScheme.onTertiaryContainer
        "Şirket" -> Color(0xFF1B5E20)
        else -> Slate800
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = categoryBgColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = news.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryTextColor
                        )
                    }

                    // If news is related to a brand, show the brand logo
                    val brandCandidates = listOf(
                        "Samsung", "Apple", "Xiaomi", "Oppo", "Vivo", "Huawei", "Google", "Pixel",
                        "Motorola", "OnePlus", "Realme", "Honor", "Sony", "Asus", "Nokia",
                        "Tecno", "Infinix", "Nothing", "ZTE", "TCL", "Fairphone"
                    )
                    val brandName = when {
                        news.category == "Şirket" -> "Şirketiniz"
                        else -> brandCandidates.firstOrNull { brand ->
                            news.title.contains(brand, ignoreCase = true) || news.text.contains(brand, ignoreCase = true)
                        }
                    }

                    if (brandName != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        BrandLogo(
                            companyName = brandName,
                            size = 18.dp,
                            shapeRadius = 4.dp
                        )
                    }
                }

                Text(
                    text = "${news.month}. Ay / ${news.year}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = news.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = news.text,
                fontSize = 13.sp,
                color = Slate600,
                lineHeight = 19.sp
            )
        }
    }
}

