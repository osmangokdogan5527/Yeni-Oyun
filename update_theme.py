import re

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    text = f.read()

# Enable dark theme by default, and fix the comment.
text = re.sub(r'darkTheme: Boolean = false,', 'darkTheme: Boolean = true,', text)

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(text)
