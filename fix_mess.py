import re

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

# Remove the incorrectly placed investInDeveloperFund
wrong_part = """    fun releaseOsHotfix() {}
    fun investInDeveloperFund(amount: Long) {
        val currentState = _state.value
        if (currentState.budget < amount) {
            _state.update { it.copy(noticeMessage = "Geliştirici Teşvik Fonu için yetersiz bütçe! Gereken: $${\"%,d\".format(amount)}") }
            return
        }

        val customOs = currentState.customOs
        val updatedOs = customOs.copy(
            devFundBalance = customOs.devFundBalance + amount,
            ecosystemScore = (customOs.ecosystemScore + (amount / 2000000L).toInt().coerceIn(1, 10)).coerceAtMost(100)
        )

        _state.update {
            it.copy(
                budget = it.budget - amount,
                customOs = updatedOs,
                noticeMessage = "Geliştirici Fonuna $${\"%,d\".format(amount)} aktarıldı! (Zamanla organik uygulama artışı sağlayacak)"
            )
        }
        autoSaveGame()
    }"""

text = text.replace(wrong_part, "    fun releaseOsHotfix() {")

# Append investInDeveloperFund to the end, before the last }
dev_fund_method = """
    fun investInDeveloperFund(amount: Long) {
        val currentState = _state.value
        if (currentState.budget < amount) {
            _state.update { it.copy(noticeMessage = "Geliştirici Teşvik Fonu için yetersiz bütçe! Gereken: $${\"%,d\".format(amount)}") }
            return
        }

        val customOs = currentState.customOs
        val updatedOs = customOs.copy(
            devFundBalance = customOs.devFundBalance + amount,
            ecosystemScore = (customOs.ecosystemScore + (amount / 2000000L).toInt().coerceIn(1, 10)).coerceAtMost(100)
        )

        _state.update {
            it.copy(
                budget = it.budget - amount,
                customOs = updatedOs,
                noticeMessage = "Geliştirici Fonuna $${\"%,d\".format(amount)} aktarıldı! (Zamanla organik uygulama artışı sağlayacak)"
            )
        }
        autoSaveGame()
    }
"""

if "fun investInDeveloperFund" not in text:
    text = text[:text.rfind('}')] + dev_fund_method + "}\n"

with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
    f.write(text)
