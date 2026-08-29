package com.example
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500

@Composable
fun SelectionGroup(
    title: String,
    options: List<ComponentOption>,
    selectedOption: String,
    unlockedTech: List<String>,
    onOptionSelected: (String) -> Unit,
    onLockedClick: (String) -> Unit = {}
) {
    val selectedItem = options.find { it.name == selectedOption }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                fontSize = 13.sp
            )
            if (selectedItem != null && selectedItem.cost > 0) {
                Text(
                    text = "+$${selectedItem.cost}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
            }
        }

        // Subtitle info for the currently selected item
        if (selectedItem?.desc != null) {
            Text(
                text = selectedItem.desc,
                fontSize = 11.sp,
                color = Slate500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Compact 2-column paired layout for neatness and minimal height
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val chunkedOptions = options.chunked(2)
            chunkedOptions.forEach { rowPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPair.forEach { option ->
                        val isUnlocked = option.requiredTech == null || unlockedTech.contains(option.requiredTech)
                        val isSelected = option.name == selectedOption

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                !isUnlocked -> Color(0xFFF1F5F9).copy(alpha = 0.35f)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    !isUnlocked -> Slate200.copy(alpha = 0.6f)
                                    else -> Slate200
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable {
                                    if (isUnlocked) {
                                        onOptionSelected(option.name)
                                    } else {
                                        onLockedClick(option.requiredTech ?: "Ar-Ge")
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    if (!isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Kilitli",
                                            tint = Slate400,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Text(
                                        text = option.name,
                                        color = when {
                                            !isUnlocked -> Slate400
                                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                            else -> Color(0xFF1E293B)
                                        },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (option.cost > 0 && isUnlocked) {
                                    Text(
                                        text = "+$${option.cost}",
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Slate500,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                } else if (!isUnlocked) {
                                    Text(
                                        text = "Ar-Ge",
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }

                    // If odd number of items, insert spacer for alignment
                    if (rowPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
