import re

with open('app/src/main/java/com/example/DashboardScreen.kt', 'r') as f:
    dash = f.read()

# Animate Reputation
dash = dash.replace(
    'Text("${state.reputation} / 100", fontSize = 16.sp, fontWeight = FontWeight.Bold)',
    'androidx.compose.animation.AnimatedContent(targetState = state.reputation, label="Rep") { rep -> Text("$rep / 100", fontSize = 16.sp, fontWeight = FontWeight.Bold) }'
)

# Animate Total Units Sold
dash = dash.replace(
    'Text("${"%,d".format(totalUnitsSold).replace(\',\', \'.\')} adet", fontSize = 16.sp, fontWeight = FontWeight.Bold)',
    'androidx.compose.animation.AnimatedContent(targetState = totalUnitsSold, label="Sold") { sold -> Text("${"%,d".format(sold).replace(\',\', \'.\')} adet", fontSize = 16.sp, fontWeight = FontWeight.Bold) }'
)

with open('app/src/main/java/com/example/DashboardScreen.kt', 'w') as f:
    f.write(dash)
