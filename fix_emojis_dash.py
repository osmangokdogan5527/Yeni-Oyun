import re

with open('app/src/main/java/com/example/DashboardScreen.kt', 'r') as f:
    text = f.read()

text = text.replace('Text(text = "🚨", fontSize = 20.sp)', 'Icon(imageVector = Icons.Default.Warning, contentDescription = "Kriz", tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))')
text = text.replace('"🔥 TREND: ${', '"TREND: ${')
text = text.replace('text = "Trendi Gör 🏆"', 'text = "Trendi Gör"')
text = text.replace('Text("🏢", fontSize = 20.sp)', 'Icon(imageVector = Icons.Default.Business, contentDescription = "Fabrika", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))')
text = text.replace('"🏦 Borç:', '"Borç:')

# Ensure Icons are imported
if "import androidx.compose.material.icons" not in text:
    text = text.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\nimport androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.*\nimport androidx.compose.material3.Icon")

with open('app/src/main/java/com/example/DashboardScreen.kt', 'w') as f:
    f.write(text)
