package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BrandLogo(
    companyId: String? = null,
    companyName: String? = null,
    isPlayer: Boolean = false,
    playerLogoId: String? = null,
    playerBrandColorHex: Long? = null,
    size: Dp = 38.dp,
    shapeRadius: Dp = 10.dp
) {
    val name = (companyName ?: "").lowercase()
    val id = (companyId ?: "").lowercase()

    val isPlayerBrand = isPlayer || id == "player_corp" || name.contains("şirket") || name.contains("sirket")
    val isApple = id == "comp_apple" || name.contains("apple") || name.contains("iphone")
    val isSamsung = id == "comp_samsung" || name.contains("samsung") || name.contains("galaxy")
    val isXiaomi = id == "comp_xiaomi" || name.contains("xiaomi") || name.contains("redmi") || name.contains("poco")
    val isOppo = id == "comp_oppo" || name.contains("oppo")
    val isVivo = id == "comp_vivo" || name.contains("vivo")
    val isHuawei = id == "comp_huawei" || name.contains("huawei") || name.contains("harmony") || name.contains("pura") || name.contains("mate")
    val isGoogle = id == "comp_google" || name.contains("google") || name.contains("pixel") || name.contains("nexus")
    val isMotorola = id == "comp_motorola" || name.contains("motorola") || name.contains("moto") || name.contains("razr")
    val isOnePlus = id == "comp_oneplus" || name.contains("oneplus")
    val isRealme = id == "comp_realme" || name.contains("realme")
    val isHonor = id == "comp_honor" || name.contains("honor")
    val isSony = id == "comp_sony" || name.contains("sony") || name.contains("xperia")
    val isAsus = id == "comp_asus" || name.contains("asus") || name.contains("rog") || name.contains("zenfone")
    val isNokia = id == "comp_nokia" || name.contains("nokia") || name.contains("hmd")
    val isTecno = id == "comp_tecno" || name.contains("tecno") || name.contains("phantom") || name.contains("camon")
    val isInfinix = id == "comp_infinix" || name.contains("infinix")
    val isNothing = id == "comp_nothing" || name.contains("nothing")
    val isZte = id == "comp_zte" || name.contains("zte") || name.contains("nubia") || name.contains("redmagic")
    val isTcl = id == "comp_tcl" || name.contains("tcl")
    val isFairphone = id == "comp_fairphone" || name.contains("fairphone")

    val playerDrawable = when (playerLogoId) {
        "ic_logo_diamond" -> R.drawable.ic_logo_diamond
        "ic_logo_star" -> R.drawable.ic_logo_star
        "ic_logo_apex" -> R.drawable.ic_logo_apex
        "ic_logo_infinity" -> R.drawable.ic_logo_infinity
        "ic_logo_shield" -> R.drawable.ic_logo_shield
        "ic_logo_nova" -> R.drawable.ic_logo_nova
        "ic_logo_monogram" -> R.drawable.ic_logo_monogram
        "ic_logo_bolt" -> R.drawable.ic_logo_bolt
        "ic_logo_rocket" -> R.drawable.ic_logo_rocket
        "ic_logo_crown" -> R.drawable.ic_logo_crown
        else -> R.drawable.ic_brand_player
    }
    val playerColor = if (playerBrandColorHex != null) Color(playerBrandColorHex) else Color(0xFF2563EB)

    val (drawableRes, bgColor, tintColor) = when {
        isPlayerBrand -> Triple(playerDrawable, playerColor, Color.White)
        isApple -> Triple(R.drawable.ic_brand_apple, Color(0xFF0F172A), Color.White)
        isSamsung -> Triple(R.drawable.ic_brand_samsung, Color(0xFF0D47A1), Color.Unspecified)
        isXiaomi -> Triple(R.drawable.ic_brand_xiaomi, Color(0xFFFF6900), Color.Unspecified)
        isOppo -> Triple(R.drawable.ic_brand_oppo, Color(0xFF008A4B), Color.White)
        isVivo -> Triple(R.drawable.ic_brand_vivo, Color(0xFF0057FF), Color.White)
        isHuawei -> Triple(R.drawable.ic_brand_huawei, Color(0xFFFFFFFF), Color.Unspecified)
        isGoogle -> Triple(R.drawable.ic_brand_google, Color(0xFFFFFFFF), Color.Unspecified)
        isMotorola -> Triple(R.drawable.ic_brand_motorola, Color(0xFF001489), Color.Unspecified)
        isOnePlus -> Triple(R.drawable.ic_brand_oneplus, Color(0xFFEB0028), Color.Unspecified)
        isRealme -> Triple(R.drawable.ic_brand_realme, Color(0xFFFFC915), Color.Unspecified)
        isHonor -> Triple(R.drawable.ic_brand_honor, Color(0xFF0F172A), Color.Unspecified)
        isSony -> Triple(R.drawable.ic_brand_sony, Color(0xFF000000), Color.Unspecified)
        isAsus -> Triple(R.drawable.ic_brand_asus, Color(0xFF111827), Color.Unspecified)
        isNokia -> Triple(R.drawable.ic_brand_nokia, Color(0xFF124191), Color.Unspecified)
        isTecno -> Triple(R.drawable.ic_brand_tecno, Color(0xFF0072CE), Color.Unspecified)
        isInfinix -> Triple(R.drawable.ic_brand_infinix, Color(0xFF1E824C), Color.Unspecified)
        isNothing -> Triple(R.drawable.ic_brand_nothing, Color(0xFF18181B), Color.Unspecified)
        isZte -> Triple(R.drawable.ic_brand_zte, Color(0xFF008CD6), Color.Unspecified)
        isTcl -> Triple(R.drawable.ic_brand_tcl, Color(0xFFED1C24), Color.Unspecified)
        isFairphone -> Triple(R.drawable.ic_brand_fairphone, Color(0xFF0084A8), Color.Unspecified)
        else -> Triple(R.drawable.ic_brand_player, Color(0xFF334155), Color.Unspecified)
    }

    val internalPadding = when {
        isApple -> (size.value * 0.18f).dp
        isSamsung -> (size.value * 0.08f).dp
        isXiaomi -> 0.dp
        isGoogle || isHuawei -> (size.value * 0.14f).dp
        isOppo || isVivo -> (size.value * 0.12f).dp
        else -> (size.value * 0.10f).dp
    }

    val needsBorder = isGoogle || isHuawei

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(shapeRadius))
            .background(bgColor)
            .then(
                if (needsBorder) Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(shapeRadius))
                else Modifier
            )
            .padding(internalPadding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = drawableRes),
            contentDescription = companyName ?: "Marka Logosu",
            tint = tintColor,
            modifier = Modifier.fillMaxSize()
        )
    }
}

data class MarketParticipant(
    val id: String,
    val name: String,
    val logoEmoji: String,
    val isPlayer: Boolean,
    val marketShare: Float,
    val monthlySales: Int,
    val currentModel: String,
    val modelPrice: Int,
    val modelScore: Int,
    val strategy: String,
    val brandColorHex: Long,
    val logoId: String? = null
)
