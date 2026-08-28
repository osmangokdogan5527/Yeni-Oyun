import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    text = f.read()

replacement = """
            val isNegative = state.budget < 0
            val budgetDisplay = if (state.budget >= 0) "$${"%,d".format(state.budget).replace(',', '.')}" else "-$${"%,d".format(kotlin.math.abs(state.budget)).replace(',', '.')}"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenFinance() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("Bütçe", fontSize = 9.sp, color = Slate500)
                androidx.compose.animation.AnimatedContent(
                    targetState = budgetDisplay,
                    label = "BudgetAnimation"
                ) { targetBudget ->
                    Text(
                        targetBudget,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isNegative) Color(0xFFDC2626) else Green500
                    )
                }
            }
"""

text = re.sub(
    r'val isNegative = state\.budget < 0\s*val budgetDisplay = [^\n]*\s*Column\([\s\S]*?\}', 
    replacement.strip(), 
    text
)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(text)
