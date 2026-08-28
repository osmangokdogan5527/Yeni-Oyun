with open('app/src/main/java/com/example/DashboardScreen.kt', 'r') as f:
    text = f.read()

text = text.replace(
    'import androidx.compose.material.icons.filled.Edit',
    'import androidx.compose.material.icons.filled.Edit\nimport androidx.compose.material.icons.filled.Warning\nimport androidx.compose.material.icons.filled.Business'
)

with open('app/src/main/java/com/example/DashboardScreen.kt', 'w') as f:
    f.write(text)
