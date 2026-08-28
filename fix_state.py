import re

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "state.copy(" in line:
        lines[i] = line.replace("state.copy(", "current.copy(")

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.writelines(lines)
