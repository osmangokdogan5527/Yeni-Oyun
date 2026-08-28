import re

with open('app/src/main/java/com/example/SoftwareScreen.kt', 'r') as f:
    text = f.read()

replacement = """
                            val dummyRivals = listOf(
                                com.example.viewmodel.CompetitorOsInfo(
                                    id = "ios", name = "iOS", company = "Apple",
                                    iconEmoji = "🍎", licenseTypeBadge = "Kapalı / Proprietary",
                                    marketSharePercent = 28.5f, techScore = 95, ecosystemScore = 98
                                ),
                                com.example.viewmodel.CompetitorOsInfo(
                                    id = "android", name = "Android", company = "Google",
                                    iconEmoji = "🤖", licenseTypeBadge = "Açık Kaynak",
                                    marketSharePercent = 69.8f, techScore = 92, ecosystemScore = 99
                                )
                            )
                            dummyRivals.forEachIndexed { index, rivalOs ->
"""

text = re.sub(r'val dummyRivals = listOf\([\s\S]*?dummyRivals\.forEachIndexed \{ index, rivalOs ->', replacement, text)

with open('app/src/main/java/com/example/SoftwareScreen.kt', 'w') as f:
    f.write(text)
