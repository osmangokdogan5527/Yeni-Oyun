package com.example

import com.example.ui.ProOutlinedTextField
import com.example.ui.ProTextButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private data class LogoOption(
    val id: String,
    val drawableRes: Int,
    val styleName: String,
    val category: String
)

private val COMPANY_LOGO_OPTIONS = listOf(
    LogoOption("ic_logo_diamond", R.drawable.ic_logo_diamond, "Minimal Elmas", "Kurumsal"),
    LogoOption("ic_logo_star", R.drawable.ic_logo_star, "Nexus Yıldızı", "Premium"),
    LogoOption("ic_logo_apex", R.drawable.ic_logo_apex, "Apex One", "Teknoloji"),
    LogoOption("ic_logo_infinity", R.drawable.ic_logo_infinity, "Infinity Loop", "Yenilikçi"),
    LogoOption("ic_logo_shield", R.drawable.ic_logo_shield, "Core Shield", "Güven"),
    LogoOption("ic_logo_nova", R.drawable.ic_logo_nova, "Nova Pulse", "Dinamik"),
    LogoOption("ic_logo_monogram", R.drawable.ic_logo_monogram, "Studio Monogram", "Minimal"),
    LogoOption("ic_logo_bolt", R.drawable.ic_logo_bolt, "Bolt Vector", "Performans"),
    LogoOption("ic_logo_rocket", R.drawable.ic_logo_rocket, "Rocket Lab", "Agresif"),
    LogoOption("ic_logo_crown", R.drawable.ic_logo_crown, "Crown Signature", "Lüks")
)

private val BRAND_COLOR_CHOICES = listOf(
    0xFF2563EBL, 0xFF10B981L, 0xFFEF4444L, 0xFFF59E0BL,
    0xFF8B5CF6L, 0xFFEC4899L, 0xFF0F172AL, 0xFF06B6D4L
)

/**
 * İlk açılışta (isFirstLaunch = true, kapatılamaz) veya "Şirket Profili" menüsünden
 * (isFirstLaunch = false, onDismiss ile kapatılabilir) şirket adı, logosu, marka
 * rengi ve sloganını belirlemek için kullanılan diyalog.
 */
@Composable
fun CompanySetupDialog(
    initialName: String,
    initialLogoId: String,
    initialBrandColorHex: Long,
    initialSlogan: String,
    isFirstLaunch: Boolean,
    onDismiss: (() -> Unit)? = null,
    onSaveProfile: (name: String, logoId: String, logoStyle: String, brandColorHex: Long, slogan: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var slogan by remember { mutableStateOf(initialSlogan) }
    var selectedLogoId by remember { mutableStateOf(initialLogoId) }
    var selectedColorHex by remember { mutableStateOf(initialBrandColorHex) }

    val isValid = name.isNotBlank()
    val selectedLogo = COMPANY_LOGO_OPTIONS.firstOrNull { it.id == selectedLogoId } ?: COMPANY_LOGO_OPTIONS.first()
    val brandColor = Color(selectedColorHex)

    Dialog(
        onDismissRequest = { if (!isFirstLaunch) onDismiss?.invoke() },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = !isFirstLaunch, dismissOnClickOutside = !isFirstLaunch)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isFirstLaunch) "Şirketini Kur" else "Şirket Profilini Düzenle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isFirstLaunch) "Markanı oluştur, logonu seç ve teknoloji şirketine karakter ver."
                    else "Şirketinin marka kimliğini daha profesyonel hale getir.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )

                BrandPreviewCard(
                    companyName = name.ifBlank { "Yeni Şirket" },
                    slogan = slogan.ifBlank { "Innovation for tomorrow" },
                    logo = selectedLogo,
                    brandColor = brandColor
                )

                ProOutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 24) name = it },
                    label = { Text("Şirket Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ProOutlinedTextField(
                    value = slogan,
                    onValueChange = { if (it.length <= 48) slogan = it },
                    label = { Text("Slogan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Logo Stili", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(188.dp),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(COMPANY_LOGO_OPTIONS) { option ->
                        LogoChoiceCard(
                            option = option,
                            isSelected = option.id == selectedLogoId,
                            brandColor = brandColor,
                            onClick = { selectedLogoId = option.id }
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedLogo.styleName,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = selectedLogo.category,
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = brandColor.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "Seçili",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            )
                        }
                    }
                }

                Text("Marka Rengi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BRAND_COLOR_CHOICES.forEach { colorHex ->
                        val isSelected = colorHex == selectedColorHex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFirstLaunch) {
                        ProTextButton(onClick = { onDismiss?.invoke() }) { Text("Vazgeç") }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button3D(
                        enabled = isValid,
                        onClick = {
                            val logoStyle = COMPANY_LOGO_OPTIONS.first { it.id == selectedLogoId }.styleName
                            onSaveProfile(name.trim(), selectedLogoId, logoStyle, selectedColorHex, slogan.trim())
                        }
                    ) {
                        Text(if (isFirstLaunch) "Şirketi Kur" else "Kaydet")
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandPreviewCard(
    companyName: String,
    slogan: String,
    logo: LogoOption,
    brandColor: Color
) {
    val secondaryColor = brandColor.copy(alpha = 0.18f)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.18f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            brandColor.copy(alpha = 0.18f),
                            secondaryColor,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(brandColor)
                        .border(1.dp, Color.White.copy(alpha = 0.30f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = logo.drawableRes),
                        contentDescription = logo.styleName,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = companyName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = slogan,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.68f)
                        ) {
                            Text(
                                text = logo.styleName,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandColor,
                                maxLines = 1
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
                        ) {
                            Text(
                                text = logo.category,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoChoiceCard(
    option: LogoOption,
    isSelected: Boolean,
    brandColor: Color,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) brandColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    val borderColor = if (isSelected) brandColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 6.dp else 1.dp,
        tonalElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) brandColor else Color.White.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = option.drawableRes),
                        contentDescription = option.styleName,
                        tint = if (isSelected) Color.White else brandColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = option.styleName,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    lineHeight = 10.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(brandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
