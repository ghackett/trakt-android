# Building Locally

Non-obvious setup needed to build this repo on a fresh machine. Check these
before diagnosing a build failure as a code problem.

## One-time setup

- **`local.properties`** at the repo root must define (quoted values for the
  `String` build config fields):

  ```properties
  TRAKT_API_KEY="..."
  TRAKT_API_SECRET="..."
  YOUNIFY_API_KEY="..."
  KEYSTORE_ALIAS=...
  KEYSTORE_KEY_PASSWORD=...
  KEYSTORE_PASSWORD=...
  ```

  Placeholder values are enough to compile; real Trakt keys are only needed
  for the app to sign in. The file is gitignored — never commit it.

- **Generate the OpenAPI client**: run `./gradlew openApiGenerate` once on a
  fresh checkout. The generated client lands in `build/generate-resources/`
  and is not checked in, so compilation fails with unresolved `*Api`
  references until this has run.

- **Give Gradle enough memory.** The repo pins `org.gradle.jvmargs=-Xmx2048m`
  in `gradle.properties`, which can drive the Kotlin daemon into
  `OutOfMemoryError: GC overhead limit exceeded` on large Compose screens.
  The symptom is a compile that appears hung at full CPU for 20+ minutes
  (GC thrash) before failing with a backend `CompilationException`. Fix it
  machine-locally in `~/.gradle/gradle.properties`:

  ```properties
  org.gradle.jvmargs=-Xmx6g -Dfile.encoding=UTF-8
  kotlin.daemon.jvmargs=-Xmx6g
  ```

## Build commands

- Phone app, single flavor (fastest): `./gradlew :app:assemblePlaystoreDebug`
  or `:app:assembleInternalDebug`.
- `./gradlew :app:assembleDebug` builds **both** flavors (`internal` +
  `playstore`), roughly doubling compile time. `:app` depends on `:tv`, so
  the TV module compiles as part of any app build.
- APKs land in `app/build/outputs/apk/<flavor>/debug/`.

## Known failure on main

`:resources:validateStringPlaceholders` fails due to pre-existing placeholder
type mismatches in `values-ar-rSA` (Crowdin-owned translation files — do not
hand-edit them; see `localization.md`). Until fixed upstream in Crowdin, skip
the task:

```bash
./gradlew :app:assembleDebug -x :resources:validateStringPlaceholders
```
