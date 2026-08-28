import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    main_activity = f.read()

# Add snackbarHost to Scaffold
if "snackbarHost =" not in main_activity:
    main_activity = main_activity.replace(
        "Scaffold(\n",
        "val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }\n"
        "        LaunchedEffect(state.noticeMessage) {\n"
        "            state.noticeMessage?.let {\n"
        "                snackbarHostState.showSnackbar(it)\n"
        "                viewModel.clearNoticeMessage()\n"
        "            }\n"
        "        }\n"
        "        Scaffold(\n"
        "            snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },\n"
    )
    with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
        f.write(main_activity)

# Remove the AlertDialog from EmployeesScreen.kt
with open('app/src/main/java/com/example/EmployeesScreen.kt', 'r') as f:
    emp = f.read()

emp = re.sub(r'if \(state\.noticeMessage != null\) \{[\s\S]*?\}\n', '', emp)
with open('app/src/main/java/com/example/EmployeesScreen.kt', 'w') as f:
    f.write(emp)
