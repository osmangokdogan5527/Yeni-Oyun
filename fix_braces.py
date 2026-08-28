import re
with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'r') as f:
    text = f.read()

target = """        if (currentState.budget < cost) {
            _state.update { it.copy(noticeMessage = "Geliştirici Konferansı (DevCon) düzenlemek için yetersiz bütçe! Gereken: $${\"%,d\".format(cost)}") }
            return
        }
            return
        }"""
replacement = """        if (currentState.budget < cost) {
            _state.update { it.copy(noticeMessage = "Geliştirici Konferansı (DevCon) düzenlemek için yetersiz bütçe! Gereken: $${\"%,d\".format(cost)}") }
            return
        }"""

if target in text:
    print("Found target, replacing...")
    text = text.replace(target, replacement)
    with open('app/src/main/java/com/example/viewmodel/GameViewModel.kt', 'w') as f:
        f.write(text)
else:
    print("Target not found")
