import os
import re

dir_path = 'app/src/main/java/com/example'

for root, dirs, files in os.walk(dir_path):
    for file in files:
        if file.endswith('.kt') and file != 'Color.kt' and file != 'Theme.kt':
            file_path = os.path.join(root, file)
            with open(file_path, 'r') as f:
                content = f.read()
            
            # Text colors
            content = re.sub(r'\bSlate900\b', 'MaterialTheme.colorScheme.onSurface', content)
            content = re.sub(r'\bSlate800\b', 'MaterialTheme.colorScheme.onSurface', content)
            content = re.sub(r'\bSlate700\b', 'MaterialTheme.colorScheme.onSurfaceVariant', content)
            
            # Light backgrounds that should be theme-aware
            content = re.sub(r'\bSlate50\b', 'MaterialTheme.colorScheme.background', content)
            content = re.sub(r'\bSlate100\b', 'MaterialTheme.colorScheme.surfaceVariant', content)
            
            # Color.Black is mostly text, but some might be literal. Let's just do it for common text attributes.
            content = re.sub(r'color\s*=\s*Color\.Black', 'color = MaterialTheme.colorScheme.onSurface', content)
            
            with open(file_path, 'w') as f:
                f.write(content)
