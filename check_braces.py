with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

balance = 0
for i, line in enumerate(lines):
    balance += line.count('{') - line.count('}')
    if balance < 0:
        print(f"Error at line {i+1}: Negative balance")
        break
    
print(f"Final balance: {balance}")
