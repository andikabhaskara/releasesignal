# ReleaseSignal — local tasks (from project plan)

Source: Cursor canvas **ReleaseSignal: project plan** (`acf8fd22-78b8-4c92-a9c0-c534cb5bad9b`).

## Milestones (core)

| # | Milestone | Target | Done means |
|---|-----------|--------|------------|
| 1 | Java API + PR CI | 6 Nov | Local and Actions test runs are green; six REST Assured cases; badge in README. |
| 2 | Better failure evidence | 7 Nov | Run ID and request evidence in reports; scheduled workflow; deliberately injected failures captured. |
| 3 | Real infrastructure | 8 Nov | Docker Compose for Kafka and Redis; Testcontainers integration test proves duplicate events cannot double-credit. |
| 4 | Rules-first triage | 9 Nov | Failures grouped by a stable fingerprint, not raw error text; confidence and supporting evidence shown. |
| 5 | AI with an eval gate | 10 Nov | Stubbed LLM adapter; labeled JSONL eval set; precision/recall and cost score; low confidence escalates. |
| 6 | Clean MVP demo | 11 Nov | One command for the demo; README, setup steps, sample report and a short architecture walkthrough. |

## Stretch (7–10)

| # | Milestone | Target | Done means |
|---|-----------|--------|------------|
| 7 | Human-approved Jira | 12 Nov | Explicit approval path creates a deduplicated Jira Free issue; the LLM never owns credentials. |
| 8 | Small web suite | 13 Nov | Three Selenium + BDD scenarios; Selenoid only if parallel browser runs are stable. |
| 9 | BDD generation | 14 Nov | Generated feature files are checked for parsing, known steps and required scenarios before review. |
| 10 | Demo + interview story | 15 Nov | Recorded walkthrough, architecture diagram, trade-offs and limits documented; no work left blocking PayPay. |

## Milestone 1 checklist (tonight)

- [x] Repository layout: Gradle wrapper, Java 21, Spring Boot, `.gitignore` for `.env`, credentials, reports, build outputs
- [x] API: health (`/actuator/health`), `POST /api/transfers` with `Idempotency-Key`, in-memory balances
- [x] Tests: JUnit 5 + REST Assured — health, success, insufficient balance, validation, identical replay, same key different amount
- [x] GitHub Actions: PR + `main`, Java 21 + Gradle cache, `./gradlew test`, upload JUnit XML on failure
- [ ] Replace README badge `OWNER/REPO` after first push
- [ ] **Validate Actions**: open a PR or push to `main` and confirm a green run (or attach failing artifact)

## Tonight’s execution steps (from canvas)

1. Create the public repository (sample data only; `.gitignore` as above).
2. Start the sample service: Spring Boot, Java 21, Gradle wrapper, health + transfer endpoints, in-memory store.
3. Test with REST Assured (six scenarios listed in plan).
4. Wire GitHub Actions (checkout, Java 21, Gradle cache, test, upload reports on failure, README badge).

## Notes

- Scope guard: do not add Kafka, Selenium, AI, or external integrations until their milestone.
- Gatekeeper: do not start the next milestone until tests, README, and CI evidence show the current one works.
