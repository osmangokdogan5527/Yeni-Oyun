with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    lines = f.readlines()

del lines[163:173]

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.writelines(lines)
