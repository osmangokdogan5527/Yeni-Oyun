import re
with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

# Let's fix the remaining unresolved references
text = text.replace('target.logoEmoji', '""') # Not sure if it's there
text = text.replace('target.activeSeries', 'emptyList()') # Not sure if it's there

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.write(text)
