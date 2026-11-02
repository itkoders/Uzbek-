# AlfaPlay — backend setup

The Android client now contains Supabase authentication and REST integration. A real backend is required before server features can run.

## Create backend
1. Create a Supabase project.
2. Open SQL Editor and run `supabase/schema.sql`.
3. In Authentication, enable Email/Password sign-in.
4. Create your first account in the app/backend, then promote its profile to admin using the SQL comment at the bottom of the schema.
5. For GitHub Actions APK builds, open the repository Settings → Secrets and variables → Actions, then add repository secrets named `SUPABASE_URL` and `SUPABASE_ANON_KEY`. For a local build, pass the matching Gradle properties. Never add the `service_role` key. The anon key is a public-client key and database access is controlled by RLS.

## Database modules
- profiles and admin role
- movies, series, episodes
- favorites and watch history
- reviews
- subscriptions and promo codes
- announcements/news\n- Android client: email sign-up/sign-in, published movie catalog, user favorites sync, and an admin movie add/delete screen. The APK build uses empty defaults until the two Gradle properties above are supplied.

## Important
The SQL creates tables and row-level security policies; it does not create a payment gateway, upload storage bucket, or Android integration by itself. Payment activation must be performed by a trusted server/Edge Function after a verified provider callback. Never let the Android client grant itself Premium.

The existing debug APK remains a local demo until the Supabase credentials and client integration are configured.
