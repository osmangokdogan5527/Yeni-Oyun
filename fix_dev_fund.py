with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

dev_fund_method = """
    fun investInDeveloperFund(amount: Long) {
        _state.update { current ->
            if (current.budget >= amount) {
                val updatedOs = current.customOs.copy(devFundBalance = current.customOs.devFundBalance + amount)
                current.copy(
                    budget = current.budget - amount,
                    customOs = updatedOs,
                    noticeMessage = "Geliştirici Fonu'na $${"%,d".format(amount)} yatırım yapıldı!"
                )
            } else {
                current.copy(noticeMessage = "Bu yatırım için yeterli bütçeniz yok!")
            }
        }
    }
"""

if "fun investInDeveloperFund" not in text:
    # Append it before the last closing brace
    text = text[:text.rfind('}')] + dev_fund_method + "}\n"
    with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
        f.write(text)
