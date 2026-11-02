# AlfaPlay — backend setup

The Android client now contains Supabase authentication and REST integration. A real backend is required before server features can run.

## Create backend
1. Create a Supabase project.
2. Open SQL Editor and run `supabase/schema.sql`.
3. In Authentication, enable Email/Password sign-in.
4. Create your first account in the app/backend, then promote its profile to admin using the SQL comment at the bottom of the schema.
5. For GitHub Actions builds, open repository Settings → Secrets and variables → Actions, then add repository secrets named `SUPABASE_URL` and `SUPABASE_ANON_KEY`. For a local build, pass the matching Gradle properties. Never add the `service_role` key. The anon key is a public-client key and database access is controlled by RLS.

## Database modules
- profiles and admin role
- movies, series, episodes
- favorites and watch history
- reviews
- subscriptions and promo codes
- announcements/news\n- Android client: email sign-up/sign-in, published movie catalog, user favorites sync, and an admin movie add/delete screen. The APK build uses empty defaults until the two Gradle properties above are supplied.

## Important
The SQL creates tables and row-level security policies; it does not create a payment gateway, upload storage bucket, or Android integration by itself. Payment activation must be performed by a trusted server/Edge Function after a verified provider callback. Never let the Android client grant itself Premium.

The Android client has Supabase email authentication, server movie catalogue, favorites synchronization and an admin movie management screen. The in-app Media3 player is included. Series, watch history, announcements, full admin modules and verified payment processing still need implementation before the complete public release.


## Google Play v1.0.0 release

The app is configured as version 1.0.0 (versionCode 1), compile/target SDK 36. The signed Android App Bundle workflow is `.github/workflows/android-release.yml`.

Before running it, add these GitHub Actions repository secrets:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `ALFAPLAY_KEYSTORE_BASE64` (base64-encoded JKS/keystore file)
- `ALFAPLAY_STORE_PASSWORD`
- `ALFAPLAY_KEY_ALIAS`
- `ALFAPLAY_KEY_PASSWORD`

Keep the signing keystore and passwords in a safe backup. Losing the upload key can delay future updates; do not commit it to the repository.
