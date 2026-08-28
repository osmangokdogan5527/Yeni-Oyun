import os

files_to_fix = [
    'app/src/main/java/com/example/NewsScreen.kt',
    'app/src/main/java/com/example/PhoneBuilderComponents.kt',
    'app/src/main/java/com/example/PhoneVisualCanvasDrawers.kt',
    'app/src/main/java/com/example/ui/BenchmarkScreen.kt'
]

for file_path in files_to_fix:
    if os.path.exists(file_path):
        with open(file_path, 'r') as f:
            content = f.read()
        if 'import androidx.compose.material3.MaterialTheme' not in content:
            content = content.replace(
                'import androidx.compose.runtime.Composable',
                'import androidx.compose.material3.MaterialTheme\nimport androidx.compose.runtime.Composable'
            )
            with open(file_path, 'w') as f:
                f.write(content)
