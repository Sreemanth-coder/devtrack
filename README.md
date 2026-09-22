# DevTrack — Developer Career Command Center

DevTrack helps developers track DSA prep, skills evidence, project portfolio,
job applications/interviews, and GitHub activity through one analytics
dashboard.

> **Status: Phase 3 of 9 complete** — backend foundation, JWT auth, the DSA
> module, and Skills + Projects. See "Roadmap" below.

## Tech stack

**Backend:** Java 21 · Spring Boot 4.1.1 · Spring Web · Spring Data JPA
(Hibernate 7) · Spring Security 7 · JWT (jjwt) · Bean Validation · Lombok ·
MySQL 8 · Maven

**Frontend (Phase 7):** React · Tailwind CSS · React Router · Axios · Recharts

**Deployment:** Docker, Docker Compose

## Architecture

Layered, modular monolith (no microservices, no message brokers):

```
controller  -> HTTP request/response, validation entry point
service     -> business logic, transactions
repository  -> Spring Data JPA persistence
entity      -> JPA entities
dto         -> request/response contracts (entities are never exposed directly)
security    -> JWT filter, JwtService, UserDetailsService
exception   -> typed exceptions + a single @RestControllerAdvice
config      -> SecurityConfig, typed @ConfigurationProperties
util        -> CurrentUserProvider (resolves the authenticated user from
               the SecurityContext — no module ever trusts a client-supplied
               userId)
```

## Authentication flow

1. `POST /api/auth/register` — validates input, checks for a duplicate email,
   hashes the password with BCrypt, saves the user, returns a JWT + profile.
2. `POST /api/auth/login` — verifies the BCrypt hash, returns a JWT + profile.
3. Every other request must send `Authorization: Bearer <token>`.
   `JwtAuthenticationFilter` validates the token once per request and
   populates the `SecurityContext` with the `User` (which implements
   `UserDetails`) as the principal.
4. Controllers/services never read a `userId` from the request. They call
   `CurrentUserProvider.getCurrentUser()` / `getCurrentUserId()`, which reads
   the authenticated principal. This is the pattern every future module
   (DSA, Skills, Projects, Applications, GitHub) will reuse for ownership
   checks.
5. Password hashes are never serialized: `UserResponse` is a separate DTO
   with no `passwordHash` field, and `User.toString()` excludes it too.

## API (Phase 1)

| Method | Path               | Auth | Description                     |
|--------|--------------------|------|----------------------------------|
| POST   | /api/auth/register | No   | Create an account                |
| POST   | /api/auth/login    | No   | Authenticate, get a JWT           |
| GET    | /api/users/me       | Yes  | Get the authenticated profile    |
| PUT    | /api/users/me       | Yes  | Update the authenticated profile |

## API (Phase 2 — DSA module)

| Method | Path                | Auth | Description                                             |
|--------|---------------------|------|----------------------------------------------------------|
| GET    | /api/problems        | Yes  | List own problems — `search`, `topic`, `difficulty`, `status`, `language` query params, plus standard Spring `page`/`size`/`sort` |
| POST   | /api/problems        | Yes  | Create a problem                                          |
| GET    | /api/problems/{id}    | Yes  | Get one of *your* problems (404, not 403, for someone else's — no existence leak) |
| PUT    | /api/problems/{id}    | Yes  | Update a problem                                           |
| DELETE | /api/problems/{id}    | Yes  | Delete a problem                                           |
| GET    | /api/dsa/stats        | Yes  | Totals, difficulty breakdown, topic progress, problems-over-time, streaks |
| GET    | /api/dsa/activity     | Yes  | Heatmap (date → count) plus weekly/monthly rollups         |
| GET    | /api/dsa/streak       | Yes  | Current streak, longest streak, last active date            |

### How DSA activity/streaks work

`DSAActivity` holds one row per `(user, date)` with a `solved_count`. It's
kept in sync whenever a problem's status changes:

- Creating a problem as `SOLVED`, or updating one *into* `SOLVED`,
  increments the count for its `solvedAt` date (defaulting to today if not
  supplied).
- Updating a problem *out of* `SOLVED`, changing its `solvedAt` while
  solved, or deleting a solved problem decrements the old date's count —
  the row is deleted once its count reaches zero rather than kept at zero,
  so "does this date have activity" is a simple existence check.
- **Current streak**: consecutive active days ending today, or ending
  yesterday if nothing's been solved yet today (so the streak isn't broken
  just because the day isn't over).
- **Longest streak**: the longest run of consecutive active days in the
  user's whole history.

DSA is intentionally isolated from Skills — no `Problem → Skill`
relationship exists, and solving problems never affects skill strength.
That link, if ever added, would be a deliberate Phase 3+ decision, not an
implicit side effect of this module.

All error responses share one shape:

```json
{
  "timestamp": "2026-08-23T10:15:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "fieldErrors": [{ "field": "email", "message": "Email must be valid" }]
}
```

## Database

`users`, `problems`, `dsa_activities`, `projects`, `skills`, `user_skills`,
and the `project_skills` join table exist so far (via
`spring.jpa.hibernate.ddl-auto=update` in dev). Later phases add
`job_applications`, `interview_rounds`, `github_accounts`,
`github_repositories` — matching the ER design in the full spec. No derived
values (streaks, conversion rates, skill strength) are stored; they're
computed in the service layer.

- `problems` has composite indexes on `(user_id)`, `(user_id, status)`, and
  `(user_id, topic)`. `dsa_activities` has a unique constraint on
  `(user_id, activity_date)`.
- `projects` has an index on `(user_id)`.
- `skills` has a unique constraint on `name` (predefined + custom share one
  namespace).
- `user_skills` has a unique constraint on `(user_id, skill_id)` — a user
  can only track a given skill once — plus an index on `(user_id)`.
- `project_skills` is the join table for the `Project N:M Skill`
  relationship (`project_id`, `skill_id`), unidirectional from `Project`.

## Environment variables

Copy `.env.example` to `.env` and fill in real values, especially `JWT_SECRET`
(must be ≥32 bytes — the app fails fast at startup otherwise; generate one
with `openssl rand -base64 48`).

**Two different local-dev paths, two different DB credentials — don't mix
them:**
- Running the backend directly (Maven/IDE) against a MySQL instance already
  on your machine: `.env.example` defaults to `DB_NAME=devtracker`,
  `DB_USERNAME=root`, matching a typical local MySQL install with
  `allowPublicKeyRetrieval=true` on the JDBC URL for caching_sha2_password.
- Running everything via `docker compose up`: the `mysql` service creates
  its own dedicated `devtrack`/`devtrack` app user in a fresh container,
  independent of whatever's on your host. `docker-compose.yml` hardcodes
  those so the two paths can't silently cross-contaminate each other.

## Running locally

**Option A — Docker Compose (recommended):**

```bash
cp .env.example .env    # edit JWT_SECRET at minimum
docker compose up --build
```

Backend will be available at `http://localhost:8080`.

**Option B — run MySQL in Docker, backend from your IDE/Maven:**

```bash
docker compose up mysql
cd backend
export JWT_SECRET=$(openssl rand -base64 48)
./mvnw spring-boot:run
```

## Running tests

```bash
cd backend
./mvnw test
```

Phase 1 tests cover: registration (success + duplicate email), password
hashing, login (success + wrong password + unknown email), that protected
endpoints reject requests without a valid token, and that `passwordHash`
never appears in any response body.

Phase 2 tests cover: creating a problem, field validation, retrieving your
own problem, a 404 (not a leaked 403) when reaching for another user's
problem, updating a problem, deleting a problem, marking a problem solved
and reverting it (and the activity row being created/decremented/deleted
accordingly), invalid enum values in both query params and JSON bodies
returning 400 instead of 500, filtering scoped correctly per-user, and
streak calculation for both a clean consecutive run and a run with a gap.

Phase 3 tests cover: the strength formula in isolation (zero evidence, a
capped-evidence worked example, self-assessment alone capped at 40,
combined evidence + self-assessment, the 3-project cap, and status weights
ordering correctly); project CRUD, cross-user 404s, and attach/remove
skills; predefined-skill visibility to everyone vs. custom-skill visibility
to only its creator, duplicate skill names, and cross-user 404s on
edit/delete of a custom skill; and user-skill creation, duplicate-tracking
rejection, self-assessment updates recalculating strength, cross-user
404s, and an end-to-end case tying a real linked project + self-assessment
through to the exact formula output.

> Note: this was authored in a sandboxed environment without access to
> Maven Central, so the build could not be compiled/run here. Please run
> `./mvnw clean verify` locally as your first step and let me know if
> anything needs adjusting.

## API (Phase 3 — Skills + Projects)

| Method | Path                            | Auth | Description |
|--------|---------------------------------|------|--------------|
| GET    | /api/projects                   | Yes  | List your own projects (embeds attached skills) |
| POST   | /api/projects                   | Yes  | Create a project |
| GET    | /api/projects/{id}               | Yes  | Get one of *your* projects (404 for someone else's) |
| PUT    | /api/projects/{id}               | Yes  | Update a project |
| DELETE | /api/projects/{id}               | Yes  | Delete a project |
| POST   | /api/projects/{id}/skills         | Yes  | Attach one or more skillIds to a project |
| DELETE | /api/projects/{id}/skills/{skillId} | Yes | Remove a skill from a project |
| GET    | /api/skills                      | Yes  | List skills visible to you: all predefined + your own custom ones |
| POST   | /api/skills                      | Yes  | Create a custom skill (globally unique name) |
| GET    | /api/skills/{id}                 | Yes  | Get a skill (404 if it's someone else's private custom skill) |
| PUT    | /api/skills/{id}                 | Yes  | Rename/recategorize *your own* custom skill only |
| DELETE | /api/skills/{id}                 | Yes  | Delete *your own* custom skill only |
| GET    | /api/user-skills                 | Yes  | List your tracked skills, each with computed strength + evidence |
| POST   | /api/user-skills                 | Yes  | Start tracking a skill, optionally with a self-assessment |
| GET    | /api/user-skills/{id}             | Yes  | Get one tracked skill with its full evidence breakdown |
| PUT    | /api/user-skills/{id}             | Yes  | Update your self-assessment (the only mutable field) |
| DELETE | /api/user-skills/{id}             | Yes  | Stop tracking a skill |

### Skill strength formula

Strength is **never stored** — `UserSkill` only stores `selfAssessment`.
Every read recomputes it from current data via `SkillStrengthCalculator`,
so it can't drift out of sync or be hand-edited to an arbitrary number.

**Step 1 — Project Evidence Score (0–100).** Every project linked to the
skill contributes points based on how far along it is:

| Project status | Points |
|-----------------|--------|
| COMPLETED       | 1.0    |
| IN_PROGRESS     | 0.6    |
| ARCHIVED        | 0.5    |
| PLANNED         | 0.3    |

Points are summed, then capped against the equivalent of 3 completed
projects (3.0 points = "substantial, demonstrated evidence"):

```
projectEvidenceScore = min(totalPoints / 3.0, 1.0) * 100
```

*Worked example:* 1 COMPLETED (1.0) + 1 IN_PROGRESS (0.6) = 1.6 points →
`min(1.6/3.0, 1.0) * 100 = 53`.

**Step 2 — Final strength (0–100).** If a self-assessment exists, it's
blended in at a fixed, minority weight — demonstrated evidence counts for
more than a claim about yourself:

```
strength = round(0.6 * projectEvidenceScore + 0.4 * selfAssessment)
```

If there's no self-assessment, strength is just the project evidence score
on its own. *Worked example:* 3 COMPLETED projects (evidence = 100) + a
self-assessment of 80 → `round(0.6*100 + 0.4*80) = round(60 + 32) = 92`.

**Deliberate consequence, stated up front:** a self-assessment with zero
supporting projects caps out at 40 (`0.4 * 100`). That's intentional —
unverified self-report alone shouldn't be able to claim a high score.
Every `UserSkillResponse` includes an `evidence` object (`projectCount`,
`projectEvidenceScore`, `selfAssessment`, and a plain-English `formula`
string) so the UI — and you, in an interview — can show exactly how a
number was reached, never just the number itself.

### Skill visibility & uniqueness

- Predefined skills (`custom = false`) are visible to everyone.
- Custom skills are visible only to their creator.
- Skill names are globally unique (case-insensitive) across predefined and
  custom skills, so "Java" can't fragment into duplicate rows under
  different casings or owners — creating a duplicate name returns a 409.
- A user can only edit/delete their *own* custom skills; predefined skills
  aren't editable through this API. Both "doesn't exist" and "exists but
  isn't yours" return 404, not a 403 that would leak existence.

### DSA independence (unchanged)

No `Problem → Skill` relationship exists anywhere in this phase either.
Solving DSA problems never feeds skill strength — only project evidence
and self-assessment do, exactly as specified.

## Roadmap

- [x] Phase 1 — Backend foundation, database, JWT auth
- [x] Phase 2 — DSA module (problems, activity, streaks, analytics)
- [x] Phase 3 — Skills + Projects
- [ ] Phase 4 — Job Applications + Interview Rounds
- [ ] Phase 5 — GitHub integration
- [ ] Phase 6 — Dashboard/analytics aggregation
- [ ] Phase 7 — React frontend
- [ ] Phase 8 — Integration, testing, polish
- [ ] Phase 9 — Docker + deployment hardening

## Security notes

- Passwords are hashed with BCrypt; plaintext is never stored or logged.
- JWTs are stateless (HS256); the shared secret is read from an environment
  variable and validated for minimum length at startup.
- `.gitignore` should exclude `.env` — never commit real secrets.
- Stack traces are never returned to clients (see `GlobalExceptionHandler`);
  unexpected errors are logged server-side and returned as a generic 500.
