import re
with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

import_lines = [l for l in text.split('\n') if l.startswith('import')]
print('\n'.join(import_lines))
