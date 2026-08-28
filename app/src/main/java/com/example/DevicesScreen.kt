package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ActiveModel
import com.example.viewmodel.CampaignType
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.HardwareCrisis

@Composable
fun DevicesScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel,
    onNewDevice: () -> Unit,
    onNavigateToBenchmark: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedModelForRestock by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedModelForMarketing by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedModelForDiscount by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedModelForRecycle by remember { mutableStateOf<ActiveModel?>(null) }
    var selectedCrisisForResolution by remember { mutableStateOf<HardwareCrisis?>(null) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Prominent Call to Action Button
        Button(
            onClick = onNewDevice,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("YENİ MODEL TASARLA & ÜRET", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        
        if (state.activeModels.isEmpty() && state.manufacturedPhones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(64.dp), tint = Slate400)
                    Text("Henüz hiç telefon üretmediniz.", color = Slate600, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Button(
                        onClick = onNewDevice,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İlk Modelini Tasarla", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Aktif Kriz Bildirim Bannerı
                val unresolvedCrises = state.activeHardwareCrises.filter { !it.isResolved }
                if (unresolvedCrises.isNotEmpty()) {
                    item {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFF87171)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🚨", fontSize = 20.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "AKTİF DONANIM KRİZLERİ MEVCUT!",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = Color(0xFFDC2626)
                                        )
                                        Text(
                                            text = "${unresolvedCrises.size} modelde kronik kusur bildirildi. Hemen müdahale edin.",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF7F1D1D)
                                        )
                                    }
                                }

                                unresolvedCrises.forEach { crisis ->
                                    Button(
                                        onClick = { selectedCrisisForResolution = crisis },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                    ) {
                                        Text(
                                            text = "⚠️ ${crisis.modelName}: ${crisis.crisisType.title} - Masayı Topla",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(state.activeModels.reversed()) { model ->
                    val phone = model.specs
                    val progressFraction = (model.totalSold.toFloat() / model.totalStock.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    val isSelling = !model.isCompleted

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                Color(phone.colorHex),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (phone.colorHex == 0xFFF1F5F9L) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.2f),
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = if (phone.colorHex == 0xFFF1F5F9L) Color.Black else Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(phone.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Slate900)
                                        val hardwareSummary = if (phone.sdCardSupport.contains("Yok", ignoreCase = true)) {
                                            "${phone.ramCapacity} (${phone.ramType}) • ${phone.storage}"
                                        } else {
                                            "${phone.ramCapacity} (${phone.ramType}) • ${phone.storage} + SD"
                                        }
                                        Text("${phone.colorName} • ${phone.logoStyle} • $hardwareSummary", fontSize = 11.sp, color = Slate600)
                                    }
                                }

                                Surface(
                                    color = when {
                                        model.isRecalled -> MaterialTheme.colorScheme.error
                                        isSelling -> MaterialTheme.colorScheme.primary
                                        else -> Slate400
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = when {
                                            model.isRecalled -> "⚠️ GERİ ÇAĞRILDI"
                                            isSelling -> "${model.monthsOnMarket}/${model.maxMonthsOnMarket} Ay"
                                            else -> "Satış Bitti"
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Model Hardware Crisis Banner (if this model has an active crisis)
                            val modelCrisis = state.activeHardwareCrises.firstOrNull { it.modelId == model.id && !it.isResolved }
                            if (modelCrisis != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFEF2F2),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(modelCrisis.crisisType.iconEmoji, fontSize = 14.sp)
                                            Text(
                                                text = "Kronik Sorun: ${modelCrisis.crisisType.title}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                        Text(
                                            text = "KRİZİ ÇÖZ",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF991B1B),
                                            modifier = Modifier.clickable { selectedCrisisForResolution = modelCrisis }
                                        )
                                    }
                                }
                            }

                            // Popularity & Demand Banner
                            val demandLabel = when {
                                model.reviewScore >= 75 -> "🔥 Yoğun Pazar Talebi (24 Ay Satış İzni)"
                                model.reviewScore >= 60 -> "⭐ Yüksek Talep (24 Ay Satış İzni)"
                                else -> "📉 Standart Talep (12 Ay Satış Süresi)"
                            }

                            val demandBgColor = when {
                                model.reviewScore >= 75 -> Color(0xFFFFF3E0)
                                model.reviewScore >= 60 -> Color(0xFFE8F5E9)
                                else -> Slate200
                            }

                            val demandTextColor = when {
                                model.reviewScore >= 75 -> Color(0xFFE65100)
                                model.reviewScore >= 60 -> Color(0xFF2E7D32)
                                else -> Slate800
                            }

                            Surface(
                                color = demandBgColor,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = demandLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = demandTextColor
                                    )
                                    Text(
                                        text = "Puan: ${model.reviewScore}/100",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = demandTextColor
                                    )
                                }
                            }

                            // Trend Match Banner (if phone caught the market trend)
                            if (model.matchesTrend) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFF1B5E20),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("🔥", fontSize = 14.sp)
                                            Text(
                                                text = "Pazar Trendi Yakalandı!",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFA5D6A7)
                                            )
                                        }
                                        val trendBonusPct = ((state.currentTrend.bonusMultiplier - 1.0f) * 100).toInt()
                                        Text(
                                            text = "+%$trendBonusPct Satış Bonusu Devrede",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Active Marketing Campaign Banner
                            model.activeCampaign?.let { camp ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Campaign,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Text(
                                                text = "${camp.type.title} (${camp.remainingMonths} Ay Kaldı)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                        Text(
                                            text = "+%${camp.type.boostPercent} Satış",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Green500
                                        )
                                    }
                                }
                            }

                            // Active Discount Campaign Banner
                            if (model.discountPercent > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFEF2F2),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.LocalOffer,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFFDC2626)
                                            )
                                            Text(
                                                text = "🔥 %${model.discountPercent} İndirim Kampanyası Aktif!",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                        Text(
                                            text = "$${model.effectivePrice} (Eski: $${model.specs.price})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB91C1C)
                                        )
                                    }
                                }
                            }

                            // Depo Şişmesi / Stok Uyarı Bannerı
                            val isStockBuildup = isSelling && model.remainingStock > 20000 && model.monthsOnMarket >= 5 && model.discountPercent == 0
                            val isStockCritical = isSelling && model.remainingStock in 1..2000
                            if (isStockBuildup) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.WarningAmber,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color(0xFFD97706)
                                            )
                                            Text(
                                                text = "📦 Depo Şişmesi Uyarısı (${"%,d".format(model.remainingStock)} stok beklemede)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB45309)
                                            )
                                        }
                                        Text(
                                            text = "İndirim Uygula",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD97706),
                                            modifier = Modifier.clickable { selectedModelForDiscount = model }
                                        )
                                    }
                                }
                            } else if (isStockCritical) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFFF7ED),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("⚡", fontSize = 13.sp)
                                            Text(
                                                text = "Stok Tükenmek Üzere (${"%,d".format(model.remainingStock)} adet kaldı)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC2410C)
                                            )
                                        }
                                        Text(
                                            text = "Stok Ekle",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable { selectedModelForRestock = model }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Sales Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${model.maxMonthsOnMarket} Aylık Satış İlerlemesi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600
                                )
                                Text(
                                    text = "${"%,d".format(model.totalSold)} / ${"%,d".format(model.totalStock)} (%${(progressFraction * 100).toInt()})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Slate200,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (model.hasPendingProduction) {
                                val prodFraction = (model.producedStock.toFloat() / model.totalStock.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "🏭 Fabrikada üretiliyor: ${"%,d".format(model.producedStock)} / ${"%,d".format(model.totalStock)}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFB45309)
                                            )
                                            Text("%${(prodFraction * 100).toInt()}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                        }
                                        LinearProgressIndicator(
                                            progress = { prodFraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp)
                                                .height(4.dp),
                                            color = Color(0xFFF59E0B),
                                            trackColor = Color(0xFFF59E0B).copy(alpha = 0.15f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (!model.isCompleted) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    SalesFactorTag(label = "Kalite", value = model.productQualityLabel, modifier = Modifier.weight(1f))
                                    SalesFactorTag(label = "Talep", value = model.marketDemandLabel, modifier = Modifier.weight(1f))
                                    SalesFactorTag(label = "Marka", value = model.brandStrengthLabel, modifier = Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Fiyat", fontSize = 10.sp, color = Slate500)
                                    Text("$${phone.price}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Kalan Stok", fontSize = 10.sp, color = Slate500)
                                    Text("${"%,d".format(model.remainingStock)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Elde Edilen Ciro", fontSize = 10.sp, color = Slate500)
                                    Text("$${"%,d".format(model.totalRevenue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green500)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Buttons: Benchmark, Marketing, Discount & Restock
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onNavigateToBenchmark,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFFF5722)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFFFCCBC))
                                ) {
                                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("TEST", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }

                                OutlinedButton(
                                    onClick = { selectedModelForMarketing = model },
                                    enabled = !model.isRecalled,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("REKLAM", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }

                                OutlinedButton(
                                    onClick = { selectedModelForDiscount = model },
                                    enabled = !model.isRecalled && model.remainingStock > 0,
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (model.discountPercent > 0) Color(0xFFDC2626) else Color(0xFFD97706)
                                    ),
                                    border = BorderStroke(1.dp, if (model.discountPercent > 0) Color(0xFFFCA5A5) else Color(0xFFFDE68A))
                                ) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        if (model.discountPercent > 0) "%${model.discountPercent} İNDİRİM" else "İNDİRİM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                OutlinedButton(
                                    onClick = { selectedModelForRestock = model },
                                    enabled = !model.isRecalled,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("STOK", fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                            }

                            if (model.remainingStock > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                val unitCost = viewModel.calculateProductionCost(model.specs)
                                val refundEstimate = (unitCost * 0.50f * model.remainingStock).toLong()
                                OutlinedButton(
                                    onClick = { selectedModelForRecycle = model },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFD97706)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                ) {
                                    Text("♻️ KALAN STOKU GERİ DÖNÜŞTÜR (+$${"%,d".format(refundEstimate)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedModelForRestock?.let { targetModel ->
        val unitCost = viewModel.calculateProductionCost(targetModel.specs)
        RestockDialog(
            model = targetModel,
            unitCost = unitCost,
            currentBudget = state.budget,
            factoryPeriodCapacity = state.currentFactoryTier.periodCapacity,
            onDismiss = { selectedModelForRestock = null },
            onConfirmRestock = { qty ->
                viewModel.remanufactureModel(targetModel.id, qty)
                selectedModelForRestock = null
            }
        )
    }

    selectedModelForMarketing?.let { targetModel ->
        MarketingDialog(
            model = targetModel,
            currentBudget = state.budget,
            onDismiss = { selectedModelForMarketing = null },
            onConfirmCampaign = { type ->
                viewModel.launchCampaign(targetModel.id, type)
                selectedModelForMarketing = null
            }
        )
    }

    selectedModelForDiscount?.let { targetModel ->
        DiscountCampaignDialog(
            model = targetModel,
            onDismiss = { selectedModelForDiscount = null },
            onApplyDiscount = { discountPct ->
                viewModel.applyModelDiscount(targetModel.id, discountPct)
                selectedModelForDiscount = null
            }
        )
    }

    selectedModelForRecycle?.let { targetModel ->
        val unitCost = viewModel.calculateProductionCost(targetModel.specs)
        RecycleStockDialog(
            model = targetModel,
            unitCost = unitCost,
            onDismiss = { selectedModelForRecycle = null },
            onConfirmRecycle = {
                viewModel.recycleRemainingStock(targetModel.id)
                selectedModelForRecycle = null
            }
        )
    }

    selectedCrisisForResolution?.let { crisis ->
        val crisisModel = state.activeModels.firstOrNull { it.id == crisis.modelId }
        HardwareCrisisManagementDialog(
            crisis = crisis,
            model = crisisModel,
            currentBudget = state.budget,
            onDismiss = { selectedCrisisForResolution = null },
            onApplyStrategy = { strategy ->
                viewModel.resolveHardwareCrisis(crisis.id, strategy)
                selectedCrisisForResolution = null
            }
        )
    }
}

@Composable
fun DiscountCampaignDialog(
    model: ActiveModel,
    onDismiss: () -> Unit,
    onApplyDiscount: (Int) -> Unit
) {
    var selectedDiscount by remember { mutableIntStateOf(model.discountPercent) }
    val originalPrice = model.specs.price
    val newPrice = originalPrice * (100 - selectedDiscount) / 100
    val marginDropPct = if (model.specs.unitCost > 0) {
        val origMargin = originalPrice - model.specs.unitCost
        val newMargin = newPrice - model.specs.unitCost
        ((1f - (newMargin.toFloat() / origMargin.coerceAtLeast(1).toFloat())) * 100).toInt().coerceAtLeast(0)
    } else 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = Color(0xFFDC2626))
                Text("${model.specs.name} - İndirim Kampanyası", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Depoda bekleyen stokları hızla eritmek ve pazar talebini patlatmak için indirim oranı belirleyin.",
                    fontSize = 12.5.sp,
                    color = Slate600
                )

                // Discount Option Buttons
                Text("İndirim Oranı:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0, 15, 30, 50).forEach { pct ->
                        val isSelected = selectedDiscount == pct
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDiscount = pct },
                            label = {
                                Text(
                                    if (pct == 0) "Standart (%0)" else "%$pct İndirim",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (pct > 0) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Impact Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    border = BorderStroke(1.dp, if (selectedDiscount > 0) Color(0xFFFCA5A5) else Slate200)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Orijinal Liste Fiyatı:", fontSize = 12.sp, color = Slate600)
                            Text("$$originalPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kampanyalı Satış Fiyatı:", fontSize = 12.sp, color = Slate600)
                            Text(
                                "$$newPrice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedDiscount > 0) Color(0xFFDC2626) else Slate900
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Birim Üretim Maliyeti:", fontSize = 12.sp, color = Slate600)
                            Text("$${model.specs.unitCost}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate600)
                        }

                        HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 2.dp))

                        val demandBoostText = when (selectedDiscount) {
                            15 -> "+%45 Satış Talebi Artışı 🔥"
                            30 -> "+%90 Satış Hızı Patlaması 🚀"
                            50 -> "+%150 Şok Tasfiye Talebi ⚡"
                            else -> "Normal Pazar Talebi"
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Beklenen Talep Etkisi:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                            Text(
                                demandBoostText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedDiscount > 0) Green500 else Slate600
                            )
                        }

                        if (selectedDiscount > 0 && newPrice <= model.specs.unitCost) {
                            Text(
                                "⚠️ DİKKAT: İndirimli fiyat birim maliyetin altına veya sınırına iniyor! Kâr marjınız sıfırlanabilir ancak stok hızla erir.",
                                fontSize = 10.5.sp,
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApplyDiscount(selectedDiscount) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDiscount > 0) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (selectedDiscount > 0) "%$selectedDiscount İndirimi Başlat" else "Standart Fiyata Dön",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = Slate600)
            }
        }
    )
}

@Composable
fun RecycleStockDialog(
    model: ActiveModel,
    unitCost: Int,
    onDismiss: () -> Unit,
    onConfirmRecycle: () -> Unit
) {
    val recyclePerUnit = (unitCost * 0.50f).toLong()
    val totalRefund = recyclePerUnit * model.remainingStock.toLong()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("♻️ Kalan Stok Geri Dönüşümü", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "${model.specs.name} modelinin elde kalan tüm stokları parçalarına ayrılarak geri dönüştürülecektir. Birim üretim maliyetinin yarı fiyatı (%50) şirket kasasına nakit olarak aktarılır.",
                    fontSize = 13.sp,
                    color = Slate600
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Elde Kalan Stok:", fontSize = 12.sp, color = Slate600)
                            Text("${"%,d".format(model.remainingStock)} adet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Birim Üretim Maliyeti:", fontSize = 12.sp, color = Slate600)
                            Text("$${unitCost}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Geri İade Değeri (Adet Başı):", fontSize = 12.sp, color = Slate600)
                            Text("$${recyclePerUnit} (%50)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Slate200)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kasaya Aktarılacak Tutar:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text("+$${"%,d".format(totalRefund)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Green500)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmRecycle,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
            ) {
                Text("♻️ Geri Dönüştür (+$${"%,d".format(totalRefund)})", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = Slate600)
            }
        }
    )
}

@Composable
fun MarketingDialog(
    model: ActiveModel,
    currentBudget: Long,
    onDismiss: () -> Unit,
    onConfirmCampaign: (CampaignType) -> Unit
) {
    var selectedType by remember { mutableStateOf(CampaignType.SOCIAL_MEDIA) }
    val canAfford = currentBudget >= selectedType.cost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${model.specs.name} - Pazarlama Kampanyası", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Cihazınızın pazardaki satış hızını ve talebini yükseltmek için bir pazarlama stratejisi seçin.",
                    fontSize = 12.sp,
                    color = Slate600
                )

                CampaignType.entries.forEach { campaign ->
                    val isSelected = selectedType == campaign
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = campaign },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = campaign.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Slate900
                                )
                                Text(
                                    text = "$${"%,d".format(campaign.cost)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${campaign.durationMonths} Ay • +%${campaign.boostPercent} Satış Artışı",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Green500
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = campaign.description,
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                }

                if (!canAfford) {
                    Text(
                        "Yetersiz Bütçe! (Mevcut: $${"%,d".format(currentBudget)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmCampaign(selectedType) },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Kampanyayı Başlat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = Slate600)
            }
        }
    )
}

@Composable
fun SalesFactorTag(label: String, value: String, modifier: Modifier = Modifier) {
    val tagColor = when (value) {
        "Mükemmel" -> Color(0xFF16A34A)
        "İyi" -> Color(0xFF22C55E)
        "Orta" -> Slate500
        "Zayıf" -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = tagColor.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, tagColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = Slate500, maxLines = 1, softWrap = false)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
fun RestockDialog(
    model: ActiveModel,
    unitCost: Int,
    currentBudget: Long,
    factoryPeriodCapacity: Int = 0,
    onDismiss: () -> Unit,
    onConfirmRestock: (Int) -> Unit
) {
    var selectedQty by remember { mutableIntStateOf(10000) }
    val totalCost = unitCost.toLong() * selectedQty
    val canAfford = currentBudget >= totalCost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("${model.specs.name} - Tekrar Üretim", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Bu model için ilave stok üreterek satış kanalını besleyebilirsiniz. Popüler ürünler 24 aya kadar pazarda satılmaya devam eder.",
                    fontSize = 13.sp,
                    color = Slate600
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Birim Üretim Maliyeti:", fontSize = 12.sp, color = Slate600)
                    Text("$${unitCost} / adet", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                }

                Text("Üretilecek Adet:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5000, 10000, 25000, 50000).forEach { qty ->
                        FilterChip(
                            selected = selectedQty == qty,
                            onClick = { selectedQty = qty },
                            label = { Text("${qty / 1000}k", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (factoryPeriodCapacity > 0) {
                    val existingBacklog = (model.totalStock - model.producedStock).coerceAtLeast(0)
                    val totalBacklogAfter = existingBacklog + selectedQty
                    val periodsNeeded = kotlin.math.ceil(totalBacklogAfter / factoryPeriodCapacity.toFloat()).toInt().coerceAtLeast(1)
                    val note = if (totalBacklogAfter <= factoryPeriodCapacity) {
                        "🏭 Fabrikanız bu siparişi (sıradaki bekleyen üretimle birlikte) tek periyotta karşılayabilir."
                    } else {
                        "🏭 Fabrika kapasitesi periyotta ${"%,d".format(factoryPeriodCapacity)} adet." +
                            (if (existingBacklog > 0) " Zaten ${"%,d".format(existingBacklog)} adetlik bekleyen üretim var;" else "") +
                            " bu siparişle birlikte tamamı yaklaşık $periodsNeeded periyotta (${"%.1f".format(periodsNeeded / 2f)} ay) tamamlanır."
                    }
                    Text(
                        text = note,
                        fontSize = 10.sp,
                        color = if (totalBacklogAfter <= factoryPeriodCapacity) Green500 else Color(0xFFB45309),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                HorizontalDivider(color = Slate200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Toplam Maliyet:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Text(
                        "$${"%,d".format(totalCost)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }

                if (!canAfford) {
                    Text(
                        "Yetersiz Bütçe! (Mevcut: $${"%,d".format(currentBudget)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRestock(selectedQty) },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Üret ve Stokla", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = Slate600)
            }
        }
    )
}
