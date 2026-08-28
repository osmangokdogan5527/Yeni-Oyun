import os
import re

files_to_fix = [
    'app/src/main/java/com/example/NewsScreen.kt',
    'app/src/main/java/com/example/PhoneBuilderComponents.kt',
    'app/src/main/java/com/example/PhoneVisualCanvasDrawers.kt',
    'app/src/main/java/com/example/ui/BenchmarkScreen.kt'
]

for file_path in files_to_fix:
    if os.path.exists(file_path):
        with open(file_path, 'r') as f:
            lines = f.readlines()
        
        new_lines = []
        for line in lines:
            if 'import com.example.ui.theme.Color(' in line or 'import com.example.ui.theme.MaterialTheme' in line:
                continue
            new_lines.append(line)
            
        with open(file_path, 'w') as f:
            f.writelines(new_lines)
