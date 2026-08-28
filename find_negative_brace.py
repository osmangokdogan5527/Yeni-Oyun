with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    balance += line.count('{') - line.count('}')
    if balance == 0 and i > 50:
        print(f"Class closed at line {i+1}: {line.strip()}")
        for j in range(max(0, i-5), i+5):
            print(f"{j+1}: {lines[j].rstrip()}")
        break
