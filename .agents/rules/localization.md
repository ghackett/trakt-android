---
trigger: glob
globs: '{resources/src/main/res/**,**/ui/**/*.kt,**/strings.xml}'
description: 'Crowdin source of truth, R.string namespacing (action_/common_/screen_/error_/a11y_), pluralStringResource, exhaustive API enum lookups returning @StringRes.'
applyTo: '{resources/src/main/res/**,**/ui/**/*.kt,**/strings.xml}'
---

# Localization

## Source of truth

- All user-facing strings live in
  `resources/src/main/res/values/strings.xml` (`en` source).
- Translations under `resources/src/main/res/values-<locale>/`
  (`values-de`, `values-fr`, …), originally populated by Crowdin via
  `.github/workflows/i18n_sync.yml`. This fork has no Crowdin access and
  the sync workflow is manual-dispatch only, so translation files may be
  edited by hand when needed — keep edits minimal (placeholder/type
  fixes, escaping) and don't reword translations unless you know the
  language.
- `buildSrc/.../ValidateStringPlaceholdersTask` validates placeholder
  parity across locales — run before merging strings PRs.

## Reading strings in Compose

Use `stringResource` / `pluralStringResource` from
`androidx.compose.ui.res`:

```kotlin
Text(stringResource(R.string.screen_movie_summary_title))

Text(
    pluralStringResource(
        id = R.plurals.episodes_left,
        count = remaining,
        remaining,
    )
)
```

- **Never** inline raw string literals for user-facing text in composables. Debug logs (via Timber) not localised.
- Reach into `R.string.*` from `resources` package
  (`tv.trakt.trakt.resources.R`).

## Key namespacing

Platform-specific suffixes:

- `_tv` — Android TV-only variant
- `_mobile` — phone-only variant when wording differs

## Pluralisation

Use `<plurals>` in `strings.xml` and `pluralStringResource` in code.
Never `if (count == 1) "1 episode" else "${count} episodes"` — breaks every non-English locale.

```xml
<plurals name="episodes_left">
    <item quantity="one">%d episode left</item>
    <item quantity="other">%d episodes left</item>
</plurals>
```

## Formatting (dates, numbers, durations)

- Dates via `kotlinx.datetime` + locale-aware helpers in
  `common/.../helpers/formatting/`.
- Numbers via `NumberFormat.getInstance(locale)` with compact notation for large values.
- Durations via existing duration helpers; don't hand-code
  `"$hours h $minutes m"`.

## TV-specific strings

- Add `_tv` suffix variant when TV wording differs (shorter text, no tap instructions).
- Reuse phone variant when wording identical.

## CrowdIn workflow

- New keys land in `values/strings.xml` (English source).
- Upstream, Crowdin populated translations; this fork can no longer sync
  (no Crowdin access), so `.github/workflows/i18n_sync.yml` is
  manual-dispatch only. Running it would overwrite local translation
  edits — don't trigger it.
- New keys ship English-only; missing translations fall back to English.
- `ValidateStringPlaceholdersTask` blocks merges with placeholder mismatches.

## Don'ts

- Don't reword `values-<locale>/strings.xml` translations unless you know
  the language — mechanical fixes (placeholder types, escaping) are fine.
- Don't introduce keys without namespace prefix.
- Don't use `String.format` with positional args (`%s %s`) when keys could clash on translator interpretation — use named placeholders via `<xliff:g>` tags.
- Don't concatenate user-facing strings with `+`. Use formatted templates.
- Don't ship locale-specific code paths (`if (locale == "de") …`). All locale logic flows through resources and `Intl.*`-style helpers.
