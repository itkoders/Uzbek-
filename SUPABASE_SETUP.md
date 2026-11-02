# AlfaPlay — backend setup

The Android APK currently has a local-only prototype. This repository now includes a Supabase schema for the production backend.

## Create backend
1. Create a Supabase project.
2. Open SQL Editor and run `supabase/schema.sql`.
3. In Authentication, enable Email/Password sign-in.
4. Create your first account in the app/backend, then promote its profile to admin using the SQL comment at the bottom of the schema.
5. Configure the Android app with the project's Supabase URL and anon/public key. Never put the service_role key in an APK or public repository.

## Database modules
- profiles and admin role
- movies, series, episodes
- favorites and watch history
- reviews
- subscriptions and promo codes
- announcements/news

## Important
The SQL creates tables and row-level security policies; it does not create a payment gateway, upload storage bucket, or Android integration by itself. Payment activation must be performed by a trusted server/Edge Function after a verified provider callback. Never let the Android client grant itself Premium.

The existing debug APK remains a local demo until the Supabase credentials and client integration are configured.
