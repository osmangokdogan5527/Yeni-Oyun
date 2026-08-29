# Gameplay / Patent balance changes

- Added persistent patent assets for completed R&D.
- Completed research now creates a protected patent automatically.
- Added patent strategies: PROTECTED, LICENSED, SOLD, EXPIRED.
- Protected patents can be licensed as a portfolio for recurring income.
- Protected patents can be sold permanently for emergency cash; sold patents no longer generate license revenue.
- Patent lifetime is 120 in-game periods (5 years at 24 periods/year).
- Patent license income is intentionally capped to reduce runaway growth.
- Existing saves with unlocked technologies but no patent data are migrated automatically into protected legacy patents.
- Finance Center patent UI now shows protected/licensed counts, monthly license income, last-period license income, and portfolio actions.
- Added patent revenue fields to GameState with safe defaults for backward-compatible serialization.

## Validation note
The supplied ZIP did not contain `gradle/wrapper/gradle-wrapper.jar`, so a full Gradle build could not be executed in this environment. Static brace/parenthesis checks passed for all modified Kotlin files. The original APK in the source ZIP was intentionally excluded from the corrected source archive because it does not contain these source changes.

## UI text orientation / narrow-screen fix
- Replaced all remaining fixed Material3 `TabRow` / `SecondaryTabRow` usages with `ScrollableTabRow` on M&A, Research, Market, Finance, and Tech Hub screens.
- Added single-line constraints to long tab labels so Turkish text cannot collapse letter-by-letter on narrow displays.
- Confirmed there are no explicit `writing-mode`, `text-orientation`, `rotationZ`, or `rotate(...)` text rules in app source/resources.
- Brace balance check passes.

## Phone Builder visual studio refresh
- Phone preview now clips the entire front/back device artwork to the selected rounded chassis shape, eliminating square/protruding corner artifacts.
- Screen glow/wallpaper effects are also clipped to the actual display glass, so colored effects cannot bleed over the bezel/corners.
- Preview corner radius now follows frame style (armored, flat metal, curved, ultra-thin) instead of using one hard-coded shadow shape.
- Rear camera artwork now follows the selected camera hardware: single, dual, triple/periscope layouts render different lens counts.
- Design style now affects the rear visual (gaming accents, rugged guards, classic inset detailing).
- Foldable OLED, Edge AMOLED and holographic display selections now add distinct screen treatment in the live preview.
- Thickness now changes the side-profile height in the Port view.
- Added a live visual design-analysis panel with Performance, Camera, Battery, Design and Price/Performance bars plus target-audience interest.
- Improved the persistent production bar to always show unit cost, retail price and gross margin before manufacturing.
- These additions are presentation/UX calculations only; they do not directly modify sales/economy formulas.
