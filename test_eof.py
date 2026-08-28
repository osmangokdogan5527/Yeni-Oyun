import re
with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

print(f"Total lines: {len(text.splitlines())}")
print(f"Last 10 lines:")
for line in text.splitlines()[-10:]:
    print(line)
