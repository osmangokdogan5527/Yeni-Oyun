with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    if "class GameViewModel" in line:
        balance = 1
        continue
    if balance > 0:
        balance += line.count('{') - line.count('}')
        if balance == 0:
            print(f"Balance hit 0 at line {i+1}: {line.strip()}")
            # keep going to see if we go negative
