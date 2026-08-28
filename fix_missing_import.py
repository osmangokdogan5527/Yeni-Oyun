import os
for root, _, files in os.walk('app/src/main/java/com/example'):
    for file in files:
        if file.endswith('.kt') and file != 'Button3D.kt':
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
                
            if 'Button3D(' in content and 'import com.example.ui.Button3D' not in content:
                print(f"Fixing missing import in {filepath}")
                first_import = content.find('import ')
                if first_import != -1:
                    content = content[:first_import] + 'import com.example.ui.Button3D\n' + content[first_import:]
                    with open(filepath, 'w') as f:
                        f.write(content)
