with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    balance += line.count('{') - line.count('}')
    if balance < 0:
        print(f"Negative balance at line {i+1}: {line.strip()}")
        break
    if balance == 0 and i > 50:
        print(f"Class GameViewModel ends at line {i+1}: {line.strip()}")
        break
