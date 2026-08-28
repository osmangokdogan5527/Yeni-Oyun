import os
import re

# 1. Update Theme.kt to enforce Light Mode and make it bright white
theme_file = 'app/src/main/java/com/example/ui/theme/Theme.kt'
if os.path.exists(theme_file):
    with open(theme_file, 'r') as f:
        theme_content = f.read()
    
    # Force light mode
    theme_content = re.sub(
        r'darkTheme: Boolean = isSystemInDarkTheme\(\)',
        'darkTheme: Boolean = false',
        theme_content
    )
    theme_content = re.sub(
        r'darkTheme: Boolean = true',
        'darkTheme: Boolean = false',
        theme_content
    )
    
    # Ensure LightColorScheme has bright colors
    if 'private val LightColorScheme = lightColorScheme(' in theme_content:
        # We will just replace the background and surface colors
        theme_content = re.sub(r'background = .*?,', 'background = Color(0xFFFFFFFF),', theme_content)
        theme_content = re.sub(r'surface = .*?,', 'surface = Color(0xFFF1F5F9),', theme_content)
        theme_content = re.sub(r'onBackground = .*?,', 'onBackground = Slate900,', theme_content)
        theme_content = re.sub(r'onSurface = .*?,', 'onSurface = Slate900,', theme_content)
        
    with open(theme_file, 'w') as f:
        f.write(theme_content)

# 2. Replace Button( with Button3D( across all files
for root, _, files in os.walk('app/src/main/java/com/example'):
    for file in files:
        if file.endswith('.kt') and file != 'Button3D.kt':
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
                
            # Regex to find exact word 'Button' followed by optional spaces and '('
            # We use negative lookbehind to avoid TextButton, IconButton, etc.
            new_content = re.sub(r'(?<!\w)Button\s*\(', 'Button3D(', content)
            
            if new_content != content:
                # Add import if missing
                if 'import com.example.ui.Button3D' not in new_content:
                    new_content = new_content.replace('import androidx.compose.material3.*', 'import androidx.compose.material3.*\nimport com.example.ui.Button3D')
                    
                    # If it doesn't have material3.*, try package
                    if 'import com.example.ui.Button3D' not in new_content:
                        new_content = new_content.replace('package ', 'package ', 1) # just to anchor
                        # actually better: find first import and insert before
                        first_import = new_content.find('import ')
                        if first_import != -1:
                            new_content = new_content[:first_import] + 'import com.example.ui.Button3D\n' + new_content[first_import:]
                
                with open(filepath, 'w') as f:
                    f.write(new_content)
                    print(f"Updated {filepath}")
