package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.viewmodel.AcquisitionTarget
import com.example.viewmodel.CompanyType
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.PostAcquisitionStrategy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcquisitionScreen(viewModel: GameViewModel) {
    val companyProfileState by viewModel.companyProfileState.collectAsState()
    val financeState by viewModel.financeState.collectAsState()

    var selectedTarget by remember { mutableStateOf<AcquisitionTarget?>(null) }
    var bidAmountStr by remember { mutableStateOf("") }
    var selectedStrategy by remember { mutableStateOf(PostAcquisitionStrategy.INDEPENDENT_BRAND) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Şirket Satın Alma (M&A)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Piyasadaki diğer şirketleri satın alıp varlıklarını devralın", fontSize = 11.sp, color = Slate500)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (companyProfileState.acquisitionTargets.isEmpty()) {
                item {
                    Text("Şu an satılık veya satın alınabilecek uygun bir şirket yok.", color = Slate500)
                }
            } else {
                items(companyProfileState.acquisitionTargets) { target ->
                    TargetCompanyCard(target = target, onSelect = {
                        selectedTarget = target
                        bidAmountStr = target.valuation.toString()
                    })
                }
            }
        }
    }

    if (selectedTarget != null) {
        val target = selectedTarget!!
        AlertDialog(
            onDismissRequest = { selectedTarget = null },
            title = { Text("${target.name} İçin Teklif Ver") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Şirket Değerlemesi: $${"%,d".format(target.valuation)}", fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = bidAmountStr,
                        onValueChange = { bidAmountStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Teklif (Bütçeniz: $${"%,d".format(financeState.budget)})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Satın Alma Sonrası Strateji:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedStrategy = PostAcquisitionStrategy.INDEPENDENT_BRAND }) {
                            RadioButton(selected = selectedStrategy == PostAcquisitionStrategy.INDEPENDENT_BRAND, onClick = { selectedStrategy = PostAcquisitionStrategy.INDEPENDENT_BRAND })
                            Column {
                                Text("Bağımsız Alt Marka", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Marka ve serileri otomatik yönetilir, itibarını korur.", fontSize = 11.sp, color = Slate500)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedStrategy = PostAcquisitionStrategy.MERGE_TO_MAIN }) {
                            RadioButton(selected = selectedStrategy == PostAcquisitionStrategy.MERGE_TO_MAIN, onClick = { selectedStrategy = PostAcquisitionStrategy.MERGE_TO_MAIN })
                            Column {
                                Text("Ana Şirkete Birleştir", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Telefon serileri size geçer ancak marka değeri kısmen düşer.", fontSize = 11.sp, color = Slate500)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedStrategy = PostAcquisitionStrategy.LIQUIDATE_ASSETS }) {
                            RadioButton(selected = selectedStrategy == PostAcquisitionStrategy.LIQUIDATE_ASSETS, onClick = { selectedStrategy = PostAcquisitionStrategy.LIQUIDATE_ASSETS })
                            Column {
                                Text("Sadece Varlıkları Kullan", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Patentleri ve çalışanları alın, markayı ve serileri kapatın.", fontSize = 11.sp, color = Slate500)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button3D(onClick = {
                    val bid = bidAmountStr.toLongOrNull() ?: 0L
                    viewModel.bidForCompany(target.id, bid, selectedStrategy)
                    selectedTarget = null
                }) {
                    Text("Teklifi Gönder")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTarget = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun TargetCompanyCard(target: AcquisitionTarget, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(target.logoEmoji, fontSize = 24.sp)
                    Column {
                        Text(target.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(target.type.description, fontSize = 12.sp, color = Slate500)
                    }
                }
                Text("$${"%,d".format(target.valuation)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Marka İtibarı", fontSize = 11.sp, color = Slate500)
                    Text("${target.brandReputation}/100", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Çalışan", fontSize = 11.sp, color = Slate500)
                    Text("${target.employees} Kişi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Patent", fontSize = 11.sp, color = Slate500)
                    Text("${target.patents.size} Adet", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column {
                    Text("Seriler", fontSize = 11.sp, color = Slate500)
                    Text("${target.activeSeries.size} Seri", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            
            if (target.activeSeries.isNotEmpty()) {
                Text("Aktif Telefon Serileri:", fontSize = 11.sp, color = Slate500, modifier = Modifier.padding(top = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    target.activeSeries.forEach { series ->
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)) {
                            Text(series.seriesName, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Button3D(onClick = onSelect, modifier = Modifier.fillMaxWidth()) {
                Text("İncele & Teklif Ver")
            }
        }
    }
}
