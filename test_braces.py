with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    if "class GameViewModel" in line:
        balance = 1 # manually set
        print(f"Class starts at {i+1}")
        for j in range(i+1, len(lines)):
            line_j = lines[j]
            balance += line_j.count('{') - line_j.count('}')
            if balance == 0:
                print(f"Class ended at {j+1}: {line_j.strip()}")
                break
