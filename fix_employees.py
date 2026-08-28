with open('app/src/main/java/com/example/EmployeesScreen.kt', 'r') as f:
    lines = f.readlines()

del lines[32:36]

with open('app/src/main/java/com/example/EmployeesScreen.kt', 'w') as f:
    f.writelines(lines)
