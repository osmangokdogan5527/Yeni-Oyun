with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    lines = f.readlines()

new_lines = lines[:2761]

dismiss_method = """
    fun dismissTechExpo() {
        _state.update { it.copy(activeTechExpo = null) }
    }
}
"""

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.writelines(new_lines)
    f.write(dismiss_method)
