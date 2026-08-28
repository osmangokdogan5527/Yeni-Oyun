with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "investInDeveloperFund" in line:
        print(f"Line {i+1}: {line.strip()}")
        # print some context
        for j in range(max(0, i-3), min(len(lines), i+15)):
            print(f"{j+1}: {lines[j].rstrip()}")
        print("-" * 40)
