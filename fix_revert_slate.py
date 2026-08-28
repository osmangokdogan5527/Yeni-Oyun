import re
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
        
        # We replaced:
        # Slate900 -> MaterialTheme.colorScheme.onSurface
        # Slate800 -> MaterialTheme.colorScheme.onSurface
        # Slate700 -> MaterialTheme.colorScheme.onSurfaceVariant
        # Slate50 -> MaterialTheme.colorScheme.background
        # Slate100 -> MaterialTheme.colorScheme.surfaceVariant
        
        # Let's just use the direct Color instances instead.
        # But wait, we don't know which was which. Let's just use Color(0xFF...) for all of them.
        
        content = content.replace('MaterialTheme.colorScheme.onSurfaceVariant', 'Color(0xFF334155)')
        content = content.replace('MaterialTheme.colorScheme.onSurface', 'Color(0xFF1E293B)')
        content = content.replace('MaterialTheme.colorScheme.background', 'Color(0xFFF8FAFC)')
        content = content.replace('MaterialTheme.colorScheme.surfaceVariant', 'Color(0xFFF1F5F9)')
        
        # Add import for Color if missing
        if 'import androidx.compose.ui.graphics.Color' not in content:
            content = content.replace('package com.example', 'package com.example\nimport androidx.compose.ui.graphics.Color')
            content = content.replace('package com.example.ui', 'package com.example.ui\nimport androidx.compose.ui.graphics.Color')
            
        with open(file_path, 'w') as f:
            f.write(content)

