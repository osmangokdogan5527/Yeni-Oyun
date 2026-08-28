import re

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

# Fix current.copy back to state.copy for the functions at the end
funcs_to_fix = [
    "fun archiveCustomChipset",
    "fun unarchiveCustomChipset",
    "fun toggleChipsetOemSale",
    "fun unarchiveModel"
]

for func in funcs_to_fix:
    start = text.find(func)
    end = text.find("fun ", start + 10)
    if end == -1: end = len(text)
    
    sub = text[start:end]
    sub = sub.replace("current.copy(", "state.copy(")
    text = text[:start] + sub + text[end:]

# Add dismissTechExpo
dismiss_method = """
    fun dismissTechExpo() {
        _state.update { it.copy(activeTechExpo = null) }
    }
"""
if "fun dismissTechExpo" not in text:
    text = text[:text.rfind('}')] + dismiss_method + "}\n"

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.write(text)
