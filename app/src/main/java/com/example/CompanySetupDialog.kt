package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

data class CompanyLogoItem(
    val id: String,
    val name: String,
    val styleName: String,
    val description: String,
    val drawableRes: Int
)

val COMPANY_LOGO_OPTIONS = listOf(
    CompanyLogoItem("ic_logo_diamond", "Minimal Elmas", "Minimal Elmas", "Zarif geometrik elmas mühür", R.drawable.ic_logo_diamond),
    CompanyLogoItem("ic_logo_star", "Nexus Yıldızı", "Nexus Yıldızı", "Fütüristik 4 uçlu nova amblemi", R.drawable.ic_logo_star),
    CompanyLogoItem("ic_logo_apex", "Apex Üçgen", "Apex Üçgen", "Modern prizmatik tepe üçgeni", R.drawable.ic_logo_apex),
    CompanyLogoItem("ic_logo_infinity", "Sonsuzluk Loop", "Sonsuzluk Loop", "Zarif sonsuzluk döngüsü", R.drawable.ic_logo_infinity),
    CompanyLogoItem("ic_logo_shield", "Siber Kalkan", "Siber Kalkan", "Sağlam siber zırh kalkanı", R.drawable.ic_logo_shield),
    CompanyLogoItem("ic_logo_nova", "Neon Nova", "Neon Nova", "Yüksek teknoloji orbital yörünge", R.drawable.ic_logo_nova),
    CompanyLogoItem("ic_logo_monogram", "Stüdyo Monogram", "Stüdyo Monogram", "Çift halkalı kurumsal monogram", R.drawable.ic_logo_monogram),
    CompanyLogoItem("ic_logo_bolt", "Turbo Volt", "Nexus Yıldızı", "Hızlı enerji & dinamik güç", R.drawable.ic_logo_bolt),
    CompanyLogoItem("ic_logo_rocket", "Aero Roket", "Apex Üçgen", "Yükselen teknoloji vizyonu", R.drawable.ic_logo_rocket),
    CompanyLogoItem("ic_logo_crown", "Imperial Taç", "Minimal Elmas", "Liderlik & prestij sembolü", R.drawable.ic_logo_crown)
)

data class BrandColorOption(
    val name: String,
    val hex: Long,
    val color: Color
)

val BRAND_COLOR_OPTIONS = listOf(
    BrandColorOption("Elektrik Mavisi", 0xFF2563EB, Color(0xFF2563EB)),
    BrandColorOption("Siber Turkuaz", 0xFF0284C7, Color(0xFF0284C7)),
    BrandColorOption("Zümrüt Yeşili", 0xFF059669, Color(0xFF059669)),
    BrandColorOption("Neon Mor", 0xFF7C3AED, Color(0xFF7C3AED)),
    BrandColorOption("Ateş Kırmızı", 0xFFDC2626, Color(0xFFDC2626)),
    BrandColorOption("Günbatımı Turuncu", 0xFFEA580C, Color(0xFFEA580C)),
    BrandColorOption("Altın Sarısı", 0xFFD97706, Color(0xFFD97706)),
    BrandColorOption("Lüks Pembe", 0xFFDB2777, Color(0xFFDB2777)),
    BrandColorOption("Koyu Grafit", 0xFF334155, Color(0xFF334155)),
    BrandColorOption("Gece Siyahı", 0xFF0F172A, Color(0xFF0F172A))
)

val NAME_SUGGESTIONS = listOf(
    "Apex Mobile", "Nova Tech", "Titan Phone", "Aura Mobile",
    "Zenith", "Quantum Device", "Cyber Dynamics", "Nexus Labs",
    "Vortex", "Aero Mobile", "Prism Tech", "Echo Mobile"
)

val SLOGAN_SUGGESTIONS = listOf(
    "Geleceğin Akıllı Telefonları",
    "Yeniliğin Zirvesi",
    "Sınırsız Güç & Zarif Tasarım",
    "Herkes İçin İleri Teknoloji",
    "Mobil Dünyanın Yeni Standartı",
    "Mükemmellik ve Güvenilirlik"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySetupDialog(
    initialName: String = "Apex Mobile",
    initialLogoId: String = "ic_logo_diamond",
    initialBrandColorHex: Long = 0xFF2563EB,
    initialSlogan: String = "Geleceğin Akıllı Telefonları",
    isFirstLaunch: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    onSaveProfile: (name: String, logoId: String, logoStyle: String, brandColorHex: Long, slogan: String) -> Unit
) {
    var nameInput by remember { mutableStateOf(initialName) }
    var selectedLogoId by remember { mutableStateOf(initialLogoId) }
    var selectedColorHex by remember { mutableLongStateOf(initialBrandColorHex) }
    var sloganInput by remember { mutableStateOf(initialSlogan) }

    val selectedLogo = COMPANY_LOGO_OPTIONS.find { it.id == selectedLogoId } ?: COMPANY_LOGO_OPTIONS.first()
    val brandColor = Color(selectedColorHex)

    Dialog(
        onDismissRequest = {
            if (!isFirstLaunch && onDismiss != null) onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isFirstLaunch,
            dismissOnClickOutside = !isFirstLaunch
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFirstLaunch) 0.dp else 16.dp),
            shape = if (isFirstLaunch) RoundedCornerShape(0.dp) else RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isFirstLaunch) "🚀 Şirketinizi Kurun" else "⚙️ Şirket Kimliğini Düzenle",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900
                            )
                        }
                        Text(
                            text = if (isFirstLaunch) 
                                "Akıllı telefon sektöründe devrim yaratacak şirketinizi ve logonuzu belirleyin." 
                            else 
                                "Şirket adınızı, logonuzu ve kurumsal renklerinizi güncelleyin.",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }

                    if (!isFirstLaunch && onDismiss != null) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Slate600)
                        }
                    }
                }

                // Live Brand Identity Preview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF0F172A),
                                        brandColor.copy(alpha = 0.35f),
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Logo Box
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(brandColor)
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = selectedLogo.drawableRes),
                                    contentDescription = selectedLogo.name,
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nameInput.ifBlank { "Şirket İsmi" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sloganInput.ifBlank { "Kurumsal Slogan" },
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = brandColor.copy(alpha = 0.3f)
                                    ) {
                                        Text(
                                            text = selectedLogo.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF334155)
                                    ) {
                                        Text(
                                            text = "2010 Kurulum",
                                            fontSize = 10.sp,
                                            color = Color(0xFFCBD5E1),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. Şirket İsmi Bölümü
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Şirket / Firma İsmi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = { Text("Örn: Apex Mobile") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, tint = brandColor)
                        }
                    )

                    // Suggestion Chips
                    Text(
                        text = "Popüler İsim Önerileri:",
                        fontSize = 11.sp,
                        color = Slate600
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NAME_SUGGESTIONS.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { nameInput = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (nameInput == suggestion) brandColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }

                // 2. Marka Logosu & Amblem Seçimi
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Marka Logosu / Amblemi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Seçili: ${selectedLogo.name}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandColor
                        )
                    }

                    Text(
                        text = "Seçtiğiniz amblem üreteceğiniz tüm telefonların arka kapağında ve pazarda markanızı temsil edecek.",
                        fontSize = 11.sp,
                        color = Slate600
                    )

                    // 2-Column Minimal Grid for Logos
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        COMPANY_LOGO_OPTIONS.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { logoItem ->
                                    val isSelected = selectedLogoId == logoItem.id
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedLogoId = logoItem.id },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) brandColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ),
                                        border = if (isSelected) BorderStroke(2.dp, brandColor) else null
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) brandColor else Color(0xFF334155))
                                                    .padding(6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = logoItem.drawableRes),
                                                    contentDescription = logoItem.name,
                                                    tint = Color.White,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = logoItem.name,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) brandColor else Slate900,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = logoItem.description,
                                                    fontSize = 9.sp,
                                                    color = Slate600,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // 3. Marka İmzası Rengi
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "3. Kurumsal Marka İmzası Rengi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BRAND_COLOR_OPTIONS.forEach { colorOpt ->
                            val isSelected = selectedColorHex == colorOpt.hex
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedColorHex = colorOpt.hex }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(colorOpt.color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Slate900 else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (colorOpt.hex == 0xFFF1F5F9) Color.Black else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = colorOpt.name.take(6),
                                    fontSize = 9.sp,
                                    color = if (isSelected) brandColor else Slate600,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // 4. Şirket Sloganı / Vizyonu
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "4. Şirket Sloganı & Vizyonu",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    OutlinedTextField(
                        value = sloganInput,
                        onValueChange = { sloganInput = it },
                        placeholder = { Text("Örn: Geleceğin Akıllı Telefonları") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SLOGAN_SUGGESTIONS.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { sloganInput = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (sloganInput == suggestion) brandColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                Button(
                    onClick = {
                        val safeName = nameInput.trim().ifBlank { "Apex Mobile" }
                        val safeSlogan = sloganInput.trim().ifBlank { "Geleceğin Akıllı Telefonları" }
                        onSaveProfile(
                            safeName,
                            selectedLogo.id,
                            selectedLogo.styleName,
                            selectedColorHex,
                            safeSlogan
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandColor)
                ) {
                    Text(
                        text = if (isFirstLaunch) "ŞİRKETİ KUR VE PAZARA GİRİŞ YAP 🚀" else "DEĞİŞİKLİKLERİ KAYDET",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
