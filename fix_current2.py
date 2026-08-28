with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

in_state_update = False
for i, line in enumerate(lines):
    if "_state.update { state ->" in line:
        in_state_update = True
    elif "_state.update { current ->" in line or "_state.update {" in line:
        in_state_update = False
        
    if in_state_update and "current.copy(" in line:
        lines[i] = line.replace("current.copy(", "state.copy(")

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.writelines(lines)
