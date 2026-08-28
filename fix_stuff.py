import re

# 1. Fix GameViewModel
with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    gvm = f.read()

gvm = gvm.replace("fun releaseOsHotfix() {\n    fun investInDeveloperFund", "fun releaseOsHotfix() {}\n    fun investInDeveloperFund")

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.write(gvm)

# 2. Fix SoftwareScreen.kt
with open('app/src/main/java/com/example/SoftwareScreen.kt', 'r') as f:
    ss = f.read()

replacement = """
                            val dummyRivals = emptyList<com.example.viewmodel.CompetitorOsInfo>()
                            dummyRivals.forEachIndexed { index, rivalOs ->
"""

ss = re.sub(r'val dummyRivals = listOf\([\s\S]*?dummyRivals\.forEachIndexed \{ index, rivalOs ->', replacement, ss)

with open('app/src/main/java/com/example/SoftwareScreen.kt', 'w') as f:
    f.write(ss)
