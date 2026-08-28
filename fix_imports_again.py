import os

files_to_fix = [
    'app/src/main/java/com/example/NewsScreen.kt',
    'app/src/main/java/com/example/PhoneBuilderComponents.kt',
    'app/src/main/java/com/example/PhoneVisualCanvasDrawers.kt',
    'app/src/main/java/com/example/ui/BenchmarkScreen.kt'
]

for file_path in files_to_fix:
    with open(file_path, 'r') as f:
        content = f.read()
    if 'import androidx.compose.material3.MaterialTheme' not in content:
        # Just prepend it after the package declaration
        content = content.replace(
            'package com.example',
            'package com.example\n\nimport androidx.compose.material3.MaterialTheme'
        ).replace(
            'package com.example.ui',
            'package com.example.ui\n\nimport androidx.compose.material3.MaterialTheme'
        )
        with open(file_path, 'w') as f:
            f.write(content)
