with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "fun setAssignedDevs" in line:
        print(f"Line {i+1}: {line.strip()}")
