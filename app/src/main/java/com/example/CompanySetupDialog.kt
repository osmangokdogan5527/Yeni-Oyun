package com.example

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import com.example.ui.Button3D
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private data class LogoOption(val id: String, val drawableRes: Int, val styleName: String)

private val COMPANY_LOGO_OPTIONS = listOf(
    LogoOption("ic_logo_diamond", R.drawable.ic_logo_diamond, "Minimal Elmas"),
    LogoOption("ic_logo_star", R.drawable.ic_logo_star, "Nexus Yıldızı"),
    LogoOption("ic_logo_apex", R.drawable.ic_logo_apex, "Apex Üçgen"),
    LogoOption("ic_logo_infinity", R.drawable.ic_logo_infinity, "Sonsuzluk Loop"),
    LogoOption("ic_logo_shield", R.drawable.ic_logo_shield, "Siber Kalkan"),
    LogoOption("ic_logo_nova", R.drawable.ic_logo_nova, "Neon Nova"),
    LogoOption("ic_logo_monogram", R.drawable.ic_logo_monogram, "Stüdyo Monogram"),
    LogoOption("ic_logo_bolt", R.drawable.ic_logo_bolt, "Bolt Enerji"),
    LogoOption("ic_logo_rocket", R.drawable.ic_logo_rocket, "Roket İvme"),
    LogoOption("ic_logo_crown", R.drawable.ic_logo_crown, "Taç Elite")
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

    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(dismissOnBackPress = !isFirstLaunch, dismissOnClickOutside = !isFirstLaunch)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .heightIn(max = 560.dp)
            ) {
                Text(
                    text = if (isFirstLaunch) "Şirketini Kur" else "Şirket Profilini Düzenle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isFirstLaunch) "Akıllı telefon imparatorluğun için bir kimlik seç."
                    else "Şirketinin görünümünü istediğin zaman güncelleyebilirsin.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 24) name = it },
                    label = { Text("Şirket Adı") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = slogan,
                    onValueChange = { if (it.length <= 48) slogan = it },
                    label = { Text("Slogan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Logo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(COMPANY_LOGO_OPTIONS) { option ->
                        val isSelected = option.id == selectedLogoId
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(selectedColorHex).copy(alpha = if (isSelected) 1f else 0.12f))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color(selectedColorHex) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedLogoId = option.id },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = option.drawableRes),
                                contentDescription = option.styleName,
                                tint = if (isSelected) Color.White else Color(selectedColorHex),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Marka Rengi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BRAND_COLOR_CHOICES.forEach { colorHex ->
                        val isSelected = colorHex == selectedColorHex
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
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

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (!isFirstLaunch) {
                        TextButton(onClick = { onDismiss?.invoke() }) { Text("Vazgeç") }
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
