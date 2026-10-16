# Debugging and completion notes

Repository cloned from `yadavcmeghasyam/SheFarmsAI-Hackathon`, starting at commit
`be1a2c5`. Work performed on 17 October 2026 in the local checkout.

## Fixed

- Enabled AGP's built-in Kotlin compilation, which had been explicitly disabled
  without applying the alternative Kotlin Android plugin.
- Consolidated KSP configuration into the version catalog and replaced the old
  Kotlin-bound KSP plugin with KSP 2.3.12. Updated Room to 2.8.4 for the current processor.
- Added the referenced release ProGuard file.
- Limited Gradle workers, CPU threads, heap and compiler processes after Windows
  native-memory allocation failures. No unrelated application was terminated.
- Replaced the entire extended Material icon dependency with its nine used icons,
  preserving their source license headers and appearance.
- Removed automatic data seeding from screen ViewModels. Samples now require an
  explicit dashboard action and are visibly labeled in record descriptions.
- Preserved transaction dates, listing dates and listing status during edits.
  Added status selection to the listing editor.
- Rejected NaN/infinity as well as missing, zero and negative monetary/quantity values.
- Added write-error handling and duplicate-save guards. Editors close only after
  successful persistence; a failed write leaves the draft available for retry.
- Persisted chat locally; serialized history operations, preserved failed input,
  reset busy state on failure and made clear-history persistent.
- Added assistant marketplace responses and explicit live-data limitations.
- Corrected assistant/dashboard claims to describe the actual offline topic library.
- Removed destructive database migration fallback; future schema changes require migrations.
- Corrected nested system insets, keyboard padding, decimal keyboards, scrollable
  finance filters and list padding around floating buttons. Editor text survives recreation.
- Corrected StateFlow tests to collect WhileSubscribed state and run scheduled work,
  and added regression/device tests for real behavior.

Build configuration follows the official [built-in Kotlin migration guide](https://developer.android.com/build/migrate-to-built-in-kotlin),
[AGP compatibility table](https://developer.android.com/build/releases/agp-9-4-0-release-notes),
[KSP release notes](https://github.com/google/ksp/releases/tag/2.3.12), and
[Room release notes](https://developer.android.com/jetpack/androidx/releases/room#2.8.4).

## Verification

Verification results are recorded here after the final checks complete.
