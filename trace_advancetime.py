with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i in range(190, 1185):
    line = lines[i]
    if "fun advanceTime" in line:
        balance = 1 # start of advanceTime block
        continue
    balance += line.count('{') - line.count('}')
    if balance == 0:
        print(f"advanceTime closed at line {i+1}: {line.strip()}")
        break
