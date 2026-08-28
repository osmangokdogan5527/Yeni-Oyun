with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

import re

# We will replace current.copy with state.copy where the block is `_state.update { state ->`
# Let's just find `_state.update { state ->` and within that block change `current.copy` to `state.copy`.

def replace_in_block(match):
    return match.group(0).replace("current.copy(", "state.copy(")

text = re.sub(r'_state\.update\s*\{\s*state\s*->.*?\}', replace_in_block, text, flags=re.DOTALL)

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.write(text)
