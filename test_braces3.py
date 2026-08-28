with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
class_started = False
for i, line in enumerate(lines):
    if "class GameViewModel" in line:
        balance = 1
        class_started = True
        continue
    if class_started:
        balance += line.count('{') - line.count('}')
        if balance == 0:
            print(f"Class ends at line {i+1}: {line.strip()}")
            break
