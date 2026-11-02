-- AlfaPlay Supabase schema
-- Apply in Supabase SQL Editor. Authentication passwords are managed by Supabase Auth;
-- never store plaintext passwords in public tables.

create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default '',
  avatar_url text,
  role text not null default 'user' check (role in ('user','admin')),
  is_banned boolean not null default false,
  preferred_language text not null default 'uz',
  created_at timestamptz not null default now()
);

create table if not exists public.movies (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  original_title text,
  description text not null default '',
  poster_url text,
  backdrop_url text,
  video_url text,
  year integer,
  genres text[] not null default '{}',
  language text not null default 'uz',
  rating numeric(3,1) not null default 0 check (rating between 0 and 10),
  age_rating text not null default '13+',
  duration_minutes integer,
  is_published boolean not null default false,
  is_premium boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.series (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  description text not null default '',
  poster_url text,
  year integer,
  genres text[] not null default '{}',
  language text not null default 'uz',
  is_published boolean not null default false,
  is_premium boolean not null default false,
  created_at timestamptz not null default now()
);

create table if not exists public.episodes (
  id uuid primary key default gen_random_uuid(),
  series_id uuid not null references public.series(id) on delete cascade,
  season_number integer not null default 1,
  episode_number integer not null,
  title text not null,
  description text not null default '',
  video_url text,
  duration_minutes integer,
  is_published boolean not null default false,
  created_at timestamptz not null default now(),
  unique(series_id, season_number, episode_number)
);

create table if not exists public.favorites (
  user_id uuid not null references public.profiles(id) on delete cascade,
  movie_id uuid not null references public.movies(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key(user_id, movie_id)
);

create table if not exists public.watch_history (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  movie_id uuid references public.movies(id) on delete cascade,
  episode_id uuid references public.episodes(id) on delete cascade,
  position_seconds integer not null default 0,
  updated_at timestamptz not null default now(),
  check ((movie_id is not null)::int + (episode_id is not null)::int = 1)
);

create table if not exists public.reviews (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  movie_id uuid not null references public.movies(id) on delete cascade,
  rating integer not null check (rating between 1 and 10),
  comment text not null default '',
  created_at timestamptz not null default now(),
  unique(user_id, movie_id)
);

create table if not exists public.subscriptions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  plan_code text not null,
  status text not null default 'pending' check (status in ('pending','active','expired','cancelled','rejected')),
  starts_at timestamptz,
  expires_at timestamptz,
  payment_reference text,
  created_at timestamptz not null default now()
);

create table if not exists public.promo_codes (
  id uuid primary key default gen_random_uuid(),
  code text not null unique,
  title text not null default '',
  duration_days integer not null check (duration_days > 0),
  max_uses integer not null default 1 check (max_uses > 0),
  used_count integer not null default 0,
  is_active boolean not null default true,
  expires_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists public.promo_redemptions (
  promo_id uuid not null references public.promo_codes(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  redeemed_at timestamptz not null default now(),
  primary key(promo_id, user_id)
);

create table if not exists public.announcements (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  body text not null default '',
  image_url text,
  is_published boolean not null default false,
  published_at timestamptz,
  created_at timestamptz not null default now()
);

create or replace function public.is_admin()
returns boolean language sql stable security definer set search_path = public
as $$
  select exists (
    select 1 from public.profiles
    where id = auth.uid() and role = 'admin' and is_banned = false
  );
$$;

create or replace function public.has_active_premium()
returns boolean
language sql
stable
security definer
set search_path = public
as $
  select exists (
    select 1
    from public.subscriptions
    where user_id = auth.uid()
      and status = 'active'
      and starts_at <= now()
      and expires_at > now()
  );
$;

alter table public.profiles enable row level security;
alter table public.movies enable row level security;
alter table public.series enable row level security;
alter table public.episodes enable row level security;
alter table public.favorites enable row level security;
alter table public.watch_history enable row level security;
alter table public.reviews enable row level security;
alter table public.subscriptions enable row level security;
alter table public.promo_codes enable row level security;
alter table public.promo_redemptions enable row level security;
alter table public.announcements enable row level security;

drop policy if exists "profiles own read" on public.profiles;
create policy "profiles own read" on public.profiles for select to authenticated using (id = auth.uid() or public.is_admin());
-- Users cannot change role, ban state, or subscription status from the client.
-- Profile changes that require privileged fields must go through a trusted server function.
drop policy if exists "profiles own update" on public.profiles;
drop policy if exists "admin profiles manage" on public.profiles;
create policy "admin profiles manage" on public.profiles for all to authenticated using (public.is_admin()) with check (public.is_admin());

create or replace function public.handle_new_user()
returns trigger language plpgsql security definer set search_path = public
as $$
begin
  insert into public.profiles (id, display_name)
  values (new.id, coalesce(new.raw_user_meta_data ->> 'name', 'AlfaPlay foydalanuvchisi'))
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

drop policy if exists "published movies read" on public.movies;
create policy "published movies read" on public.movies for select to anon, authenticated using ((is_published and (not is_premium or public.has_active_premium())) or public.is_admin());
drop policy if exists "admin movies manage" on public.movies;
create policy "admin movies manage" on public.movies for all to authenticated using (public.is_admin()) with check (public.is_admin());

drop policy if exists "published series read" on public.series;
create policy "published series read" on public.series for select to anon, authenticated using ((is_published and (not is_premium or public.has_active_premium())) or public.is_admin());
drop policy if exists "admin series manage" on public.series;
create policy "admin series manage" on public.series for all to authenticated using (public.is_admin()) with check (public.is_admin());

drop policy if exists "published episodes read" on public.episodes;
create policy "published episodes read" on public.episodes for select to anon, authenticated using (
  public.is_admin() or (
    is_published and exists (
      select 1 from public.series s
      where s.id = series_id
        and s.is_published
        and (not s.is_premium or public.has_active_premium())
    )
  )
);
drop policy if exists "admin episodes manage" on public.episodes;
create policy "admin episodes manage" on public.episodes for all to authenticated using (public.is_admin()) with check (public.is_admin());

drop policy if exists "own favorites" on public.favorites;
create policy "own favorites" on public.favorites for all to authenticated using (user_id = auth.uid() or public.is_admin()) with check (user_id = auth.uid() or public.is_admin());
drop policy if exists "own watch history" on public.watch_history;
create policy "own watch history" on public.watch_history for all to authenticated using (user_id = auth.uid() or public.is_admin()) with check (user_id = auth.uid() or public.is_admin());

drop policy if exists "reviews read" on public.reviews;
create policy "reviews read" on public.reviews for select to anon, authenticated using (true);
drop policy if exists "own reviews" on public.reviews;
create policy "own reviews" on public.reviews for insert to authenticated with check (user_id = auth.uid());
drop policy if exists "own review update" on public.reviews;
create policy "own review update" on public.reviews for update to authenticated using (user_id = auth.uid() or public.is_admin()) with check (user_id = auth.uid() or public.is_admin());

drop policy if exists "own subscriptions read" on public.subscriptions;
create policy "own subscriptions read" on public.subscriptions for select to authenticated using (user_id = auth.uid() or public.is_admin());
drop policy if exists "admin subscriptions manage" on public.subscriptions;
create policy "admin subscriptions manage" on public.subscriptions for all to authenticated using (public.is_admin()) with check (public.is_admin());

drop policy if exists "active promo codes visible" on public.promo_codes;
create policy "active promo codes visible" on public.promo_codes for select to authenticated using (is_active or public.is_admin());
drop policy if exists "admin promo manage" on public.promo_codes;
create policy "admin promo manage" on public.promo_codes for all to authenticated using (public.is_admin()) with check (public.is_admin());
drop policy if exists "own promo redemptions" on public.promo_redemptions;
create policy "own promo redemptions" on public.promo_redemptions for select to authenticated using (user_id = auth.uid() or public.is_admin());

drop policy if exists "published announcements read" on public.announcements;
create policy "published announcements read" on public.announcements for select to anon, authenticated using (is_published or public.is_admin());
drop policy if exists "admin announcements manage" on public.announcements;
create policy "admin announcements manage" on public.announcements for all to authenticated using (public.is_admin()) with check (public.is_admin());

-- IMPORTANT: promote the first admin manually after creating that account:
-- update public.profiles set role = 'admin' where id = '<AUTH_USER_UUID>';
