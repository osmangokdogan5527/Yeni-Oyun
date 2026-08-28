with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    if i == 22: # class GameViewModel
        balance = 1
        continue
    if i < 22:
        continue
    
    balance += line.count('{') - line.count('}')
    if balance == 0:
        print(f"Balance hit 0 at line {i+1}: {line.strip()}")
        break
    if balance < 0:
        print(f"Balance went negative at line {i+1}")
        break
