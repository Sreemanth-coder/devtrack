# DevTrack Command Center

Build a polished production-quality full-stack frontend for my existing application called "DevTrack — Developer Career Command Center".

IMPORTANT:
This frontend will connect to an existing Spring Boot REST backend.
Do NOT create a mock backend.
Do NOT invent API endpoints.
Do NOT replace the backend architecture.
Use the API endpoints and response structures specified below.

The goal is to create a premium developer productivity/career dashboard that feels like a real SaaS product rather than a college project.

==================================================
1. PRODUCT
==================================================

DevTrack helps developers track and visualize:

- DSA progress
- LeetCode progress
- Technical skills
- Projects
- GitHub activity
- Career readiness

The frontend should make these different sources feel like one unified developer career command center.

==================================================
2. DESIGN DIRECTION
==================================================

Use a modern dark developer SaaS aesthetic.

Overall feeling:

- premium
- technical
- clean
- minimal
- data-driven
- professional
- modern
- slightly futuristic

Avoid:

- generic admin dashboard appearance
- excessive gradients
- excessive glassmorphism
- huge text everywhere
- excessive animations
- overly colorful cards
- childish UI
- excessive borders
- unnecessary decorative elements

Use:

- dark charcoal/deep navy background
- slightly lighter card surfaces
- subtle borders
- strong typography hierarchy
- one primary accent color
- subtle accent gradients only where appropriate
- rounded cards
- clean spacing
- elegant hover states
- smooth but restrained transitions

Use a professional modern font such as Inter.

The UI should look appropriate for a developer portfolio project being shown to recruiters.

==================================================
3. APPLICATION SHELL
==================================================

Create a responsive application shell.

Desktop:

Left sidebar navigation.

Sidebar sections:

DEVTRACK logo/wordmark

Navigation:

Dashboard
DSA
Skills
Projects
GitHub
Career Analytics

Bottom of sidebar:

User avatar/name
Settings icon

Top bar:

Current page title
Optional breadcrumb
User profile/avatar
Theme control if appropriate

Mobile:

Collapsible sidebar / drawer.

The application must be fully responsive.

==================================================
4. ROUTES
==================================================

Create these frontend routes:

/dashboard
/dsa
/skills
/projects
/github
/career

If authentication is not implemented in the frontend yet, temporarily allow the application shell to load, but structure the code so authentication can easily be connected to the existing backend JWT authentication.

==================================================
5. DASHBOARD
==================================================

Dashboard is the main landing page.

It should immediately communicate the developer's current career state.

Hero section:

"Developer Career Command Center"

Short subtitle:

"Track your coding progress, technical skills, projects and developer activity in one place."

Main visual:

Large circular Career Score card.

Display:

Career Score
56 / 100

Do NOT hardcode this value in the final application.

Fetch it dynamically from:

GET /api/career/analytics

The score must update automatically from the backend.

Below/around the score show:

DSA Score
Skills Score
Projects Score
GitHub Score
Consistency Score

Use visually distinct but consistent metric cards.

Dashboard should also contain:

--------------------------------
DSA SNAPSHOT
--------------------------------

Show:

LeetCode solved
DevTrack solved
Easy
Medium
Hard
Current streak
Longest streak

LeetCode data comes from:

GET /api/leetcode/stats

DevTrack DSA data comes from:

GET /api/dsa/stats

Important:

DevTrack DSA and LeetCode are separate data sources.

Do not combine them into a fake "total solved" value.

--------------------------------
SKILLS SNAPSHOT
--------------------------------

Show:

Total skills
Average skill strength

Use:

GET /api/career/analytics

Also provide a compact skill overview.

--------------------------------
PROJECT SNAPSHOT
--------------------------------

Show:

Total projects
Completed projects
In-progress projects

Use career analytics response.

--------------------------------
GITHUB SNAPSHOT
--------------------------------

Show:

Repositories
Original repositories
Stars
Forks
Commits
Active days

Use:

GET /api/github/analytics

GET /api/github/activity

--------------------------------
RECENT ACTIVITY
--------------------------------

Use GitHub activity data to show a contribution-style visualization.

Use:

GET /api/github/activity

Render commitsByDate as a GitHub-style contribution heatmap.

Do not fabricate dates.

==================================================
6. DSA PAGE
==================================================

Create a dedicated DSA analytics page.

Top section:

DSA Progress

Cards:

Total Problems
Solved
Todo
In Progress
Current Streak
Longest Streak

Source:

GET /api/dsa/stats

Create a clean difficulty donut/pie chart using:

difficultyBreakdown.easy
difficultyBreakdown.medium
difficultyBreakdown.hard

Create a topic progress section.

Use:

topicProgress

Each topic should show:

topic name
total problems
solved problems
progress percentage

Create a problems-over-time chart using:

problemsSolvedOverTime

If the array is empty, DO NOT fabricate chart data.

Instead show an elegant empty state:

"No DevTrack DSA activity yet"

Then create a separate LeetCode section.

Use:

GET /api/leetcode/stats

Display:

Total solved
Easy
Medium
Hard
Ranking

Make LeetCode visually distinct from DevTrack DSA.

Show the Easy / Medium / Hard distribution visually.

==================================================
7. SKILLS PAGE
==================================================

Create a skills management/analytics page.

Fetch:

GET /api/skills

Display skills grouped by category.

Categories include:

LANGUAGE
FRAMEWORK

Create attractive skill cards.

Each skill should show:

name
category

Also create an evidence/strength section using:

GET /api/github/skill-evidence

Display:

skill name
repository count
stars
forks
evidence level

Evidence levels:

NO_EVIDENCE
BEGINNER
DEVELOPING
STRONG

Use visual badges with clear hierarchy.

Do not invent skill strength values when they are not provided by the API.

Use Career Analytics for:

averageStrength
totalSkills

==================================================
8. PROJECTS PAGE
==================================================

Fetch:

GET /api/projects

Create premium project cards.

Each project card should display:

name
description
status
skills
GitHub link
live link

Status should have clear visual badges:

COMPLETED
IN_PROGRESS
PLANNED
ARCHIVED

Project details can include:

key features
challenges
learnings
resume description

Provide a clean project detail view/modal/page.

Do not display fake projects.

Use the actual API response.

==================================================
9. GITHUB PAGE
==================================================

Create a dedicated GitHub analytics page.

Use:

GET /api/github/analytics

Show:

Total repositories
Original repositories
Forked repositories
Total stars
Total forks
Most used language

Create a language distribution chart using:

languageDistribution

Example current data:

Java: 2
C++: 2
JavaScript: 2
Python: 1

Do not hardcode these values.

Fetch them dynamically.

--------------------------------
GITHUB ACTIVITY
--------------------------------

Use:

GET /api/github/activity

Display:

Total commits
Active days
First activity
Last activity

Most important visualization:

GitHub-style contribution heatmap using commitsByDate.

The heatmap should use the dates returned by the API.

Use different intensity levels based on commit count.

Make this one of the visually strongest elements on the GitHub page.

--------------------------------
GITHUB SKILL EVIDENCE
--------------------------------

Use:

GET /api/github/skill-evidence

Create a table/card layout showing:

Skill
Repository count
Stars
Forks
Evidence level

==================================================
10. CAREER ANALYTICS PAGE
==================================================

This should be the most analytical page.

Fetch:

GET /api/career/analytics

Display the Career Score prominently.

Show:

DSA Score
Skills Score
Projects Score
GitHub Score
Consistency Score

Create a clean visual comparison of the five scores.

Then display detailed sections.

DSA:

DevTrack solved
LeetCode solved
Easy
Medium
Hard
Current streak
Longest streak

Skills:

Total skills
Average strength

Projects:

Total projects
Completed projects
In-progress projects

GitHub:

Repositories
Original repositories
Stars
Forks
Active days
Total commits

Consistency:

DSA current streak
DSA longest streak
GitHub active days

The Career Score should be presented as a calculated metric, not as an arbitrary rating generated by the frontend.

==================================================
11. API CLIENT
==================================================

Create a centralized API client.

Do NOT scatter fetch/axios calls throughout components.

Prefer a structure such as:

src/
  api/
    client
    auth
    dsa
    skills
    projects
    github
    leetcode
    career

Use a centralized API base URL from an environment variable.

Example:

VITE_API_BASE_URL

The frontend must support:

GET /api/users/me
GET /api/dsa/stats
GET /api/skills
GET /api/projects
GET /api/github/analytics
GET /api/github/activity
GET /api/github/skill-evidence
GET /api/leetcode/stats
GET /api/career/analytics

==================================================
12. AUTHENTICATION
==================================================

The backend uses JWT authentication.

Structure the frontend for JWT-based authentication.

The API client should attach:

Authorization: Bearer <token>

to protected requests.

Do not create fake authentication.

Do not store passwords.

Create clean handling for:

401 Unauthorized
403 Forbidden

If the token is missing or expired, show a clean authentication state rather than a broken page.

==================================================
13. LOADING STATES
==================================================

Every page that fetches API data must have polished loading states.

Use skeleton loaders instead of blank screens.

Cards should show skeleton placeholders while loading.

Charts should also have appropriate loading states.

==================================================
14. ERROR STATES
==================================================

Every API section needs graceful error handling.

Do not expose raw stack traces.

Show messages such as:

"Unable to load GitHub analytics."

Provide a retry action.

If one API fails, do not break the entire dashboard.

For example:

GitHub failure should not prevent DSA and Skills from rendering.

==================================================
15. EMPTY STATES
==================================================

Handle empty arrays properly.

Examples:

No DSA problems:

"No DevTrack DSA activity yet."

No projects:

"No projects added yet."

No GitHub activity:

"No GitHub activity found for the selected period."

Do not generate fake data to make empty pages look populated.

==================================================
16. CHARTS
==================================================

Use a reliable React charting library such as Recharts if available.

Charts should be:

- clean
- minimal
- readable
- responsive
- dark-mode compatible

Avoid chart overload.

Important charts:

Career score/category comparison
DSA difficulty distribution
GitHub language distribution
DSA topic progress
GitHub activity heatmap

==================================================
17. RESPONSIVENESS
==================================================

Desktop:

Use the full dashboard layout.

Tablet:

Adapt grids.

Mobile:

Single-column layouts where appropriate.

Charts must resize correctly.

Sidebar becomes a mobile drawer.

Tables should become responsive cards where necessary.

==================================================
18. COMPONENT ARCHITECTURE
==================================================

Keep the frontend maintainable.

Create reusable components for:

MetricCard
ScoreCard
ProgressBar
StatusBadge
SkillBadge
ChartCard
SectionHeader
EmptyState
ErrorState
LoadingSkeleton
ContributionHeatmap
Sidebar
TopBar

Avoid giant page components.

==================================================
19. DATA RULE
==================================================

CRITICAL:

Never invent backend data.

Never replace API responses with static mock values after API integration.

Use real backend responses.

If an API returns an empty array, display a proper empty state.

If an API field is null, handle it gracefully.

==================================================
20. VISUAL QUALITY
==================================================

The final product should look like a polished developer SaaS product.

Prioritize:

1. visual hierarchy
2. spacing
3. typography
4. consistency
5. meaningful data visualization
6. responsive behavior
7. subtle animations
8. accessibility

Use animations sparingly.

Examples:

- card hover elevation
- subtle score progress animation
- smooth chart entrance
- sidebar transitions

Do not animate everything.

==================================================
21. IMPORTANT BACKEND DATA SHAPES
==================================================

GET /api/users/me

{
  "id": 1,
  "name": "Test User",
  "email": "test@devtrack.com",
  "githubUsername": "Sreemanth-coder",
  "createdAt": "2026-08-23T18:26:36.516530Z"
}

GET /api/dsa/stats

{
  "totalProblems": 0,
  "totalSolved": 0,
  "totalTodo": 0,
  "totalInProgress": 0,
  "difficultyBreakdown": {
    "easy": 0,
    "medium": 0,
    "hard": 0
  },
  "topicProgress": [],
  "problemsSolvedOverTime": [],
  "currentStreak": 0,
  "longestStreak": 0
}

GET /api/skills

[
  {
    "id": 2,
    "name": "C++",
    "category": "LANGUAGE",
    "custom": true
  },
  {
    "id": 3,
    "name": "Java",
    "category": "LANGUAGE",
    "custom": true
  },
  {
    "id": 4,
    "name": "JavaScript",
    "category": "LANGUAGE",
    "custom": true
  },
  {
    "id": 5,
    "name": "Python",
    "category": "LANGUAGE",
    "custom": true
  },
  {
    "id": 1,
    "name": "Spring Boot",
    "category": "FRAMEWORK",
    "custom": true
  }
]

GET /api/projects

[
  {
    "id": 1,
    "name": "DevTrack",
    "description": "Developer career tracking platform",
    "status": "COMPLETED",
    "githubUrl": "https://github.com/example/devtrack",
    "liveUrl": "https://example.com",
    "keyFeatures": "DSA tracking, skill tracking, project management",
    "challenges": "Designing the backend architecture",
    "learnings": "Spring Boot and JPA",
    "resumeDescription": "Career tracking platform built with Spring Boot",
    "interviewNotes": "Explain JWT, JPA relationships and REST APIs",
    "skills": [
      {
        "id": 1,
        "name": "Spring Boot",
        "category": "FRAMEWORK",
        "custom": true
      }
    ],
    "createdAt": "2026-08-27T09:46:27.694618Z",
    "updatedAt": "2026-08-27T09:59:37.057204Z"
  }
]

GET /api/github/analytics

{
  "forkedRepositories": 0,
  "languageDistribution": {
    "Java": 2,
    "C++": 2,
    "JavaScript": 2,
    "Python": 1
  },
  "mostUsedLanguage": "Java",
  "originalRepositories": 7,
  "totalForks": 0,
  "totalRepositories": 7,
  "totalStars": 2
}

GET /api/github/activity

{
  "activeDays": 18,
  "commitsByDate": {
    "2026-06-26": 2,
    "2026-06-29": 1,
    "2026-07-01": 3,
    "2026-07-03": 5,
    "2026-07-05": 1,
    "2026-07-10": 1,
    "2026-07-12": 3,
    "2026-07-13": 3,
    "2026-07-14": 2,
    "2026-07-15": 2,
    "2026-07-19": 2,
    "2026-07-24": 1,
    "2026-07-27": 3,
    "2026-08-03": 3,
    "2026-08-04": 2,
    "2026-08-12": 1,
    "2026-08-24": 1,
    "2026-09-11": 1
  },
  "firstActivity": "2026-06-26",
  "lastActivity": "2026-09-11",
  "totalCommits": 37
}

GET /api/github/skill-evidence

[
  {
    "evidenceLevel": "NO_EVIDENCE",
    "repositoryCount": 0,
    "skillId": 1,
    "skillName": "Spring Boot",
    "totalForks": 0,
    "totalStars": 0
  },
  {
    "evidenceLevel": "DEVELOPING",
    "repositoryCount": 2,
    "skillId": 2,
    "skillName": "C++",
    "totalForks": 0,
    "totalStars": 0
  },
  {
    "evidenceLevel": "DEVELOPING",
    "repositoryCount": 2,
    "skillId": 3,
    "skillName": "Java",
    "totalForks": 0,
    "totalStars": 0
  },
  {
    "evidenceLevel": "DEVELOPING",
    "repositoryCount": 2,
    "skillId": 4,
    "skillName": "JavaScript",
    "totalForks": 0,
    "totalStars": 1
  },
  {
    "evidenceLevel": "BEGINNER",
    "repositoryCount": 1,
    "skillId": 5,
    "skillName": "Python",
    "totalForks": 0,
    "totalStars": 1
  }
]

GET /api/leetcode/stats

{
  "easySolved": 73,
  "hardSolved": 16,
  "mediumSolved": 103,
  "ranking": 895258,
  "totalSolved": 192,
  "username": "sree12-code"
}

==================================================
22. CAREER ANALYTICS RESPONSE
==================================================

GET /api/career/analytics

Use the real response dynamically.

Expected structure:

{
  "careerScore": number,

  "consistency": {
    "dsaCurrentStreak": number,
    "dsaLongestStreak": number,
    "githubActiveDays": number
  },

  "consistencyScore": number,

  "dsa": {
    "currentStreak": number,
    "devTrackSolved": number,
    "easySolved": number,
    "hardSolved": number,
    "leetCodeSolved": number,
    "longestStreak": number,
    "mediumSolved": number
  },

  "dsaScore": number,

  "github": {
    "activeDays": number,
    "originalRepositories": number,
    "repositories": number,
    "totalCommits": number,
    "totalForks": number,
    "totalStars": number
  },

  "githubScore": number,

  "projects": {
    "completedProjects": number,
    "inProgressProjects": number,
    "totalProjects": number
  },

  "projectsScore": number,

  "skills": {
    "averageStrength": number,
    "totalSkills": number
  },

  "skillsScore": number
}

Do not hardcode the current score.

==================================================
23. FINAL REQUIREMENT
==================================================

Build the frontend now.

Prioritize the dashboard and application shell first, then implement the remaining pages.

Use real API integration architecture.

Do not ask me to manually design the UI.

Make the design decisions yourself based on the requirements above.

The final result should feel like a polished developer career SaaS product suitable for a professional portfolio.

This project was built with [Lovable](https://lovable.dev).

## Build with Lovable

Continue developing this project in the [Lovable editor](https://lovable.dev/projects/b5416b52-a33d-4c9b-b320-abd1e8763cdb).

- **Ship faster**: describe what you want to build and Lovable handles the code.
- **Stay in sync**: every change made in Lovable is committed straight to this repository.
- **Full ownership**: this code is yours. Push to `main` on GitHub and your changes sync back into Lovable, ready for your next prompt.

## Development

Prefer working locally? You need Node.js and npm — [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating).

```sh
git clone <this-repository-url>
cd <repository-name>
npm i
npm run dev
```
