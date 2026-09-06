---
name: issue-grind
description: Work the GitHub issue backlog one item at a time - pick the most obvious open issue, verify it independently on both sides (self + Codex, isolated so neither clobbers the other), agree a plan with Codex, fix it, and update the issue. Use when asked to grind through issues, work the backlog, or repeatedly fix the most obvious issue until none are left.
---

# Issue grind

One issue per iteration. Loop until no *obvious* item remains, then stop and say why.

## Picking the issue

Prefer, in order:

1. `severity: high` + `verified` — reproduced already, so verification is a re-run.
2. A defect with a **mechanical** fix (one wrong predicate, one misplaced statement). Prefer
   these over items whose fix is a design decision.
3. A defect whose blast radius is understood.

**Stop the loop** when what is left is only: design decisions with no obvious right answer,
items needing a product call from the maintainer, large refactors, or "smaller items"
grab-bags. Say which, and list what remains. Do not manufacture obviousness to keep going.

Skip anything already closed. Check for issues that another iteration's fix has silently
resolved and close those with a note rather than re-fixing.

## 1. Verify — twice, isolated

The point of two verifications is independence, so do not let them contaminate each other.

**Order matters.** Verify first, alone. Finish. *Then* hand off. Never have both sides
touching the tree at once — a concurrent `mvn clean` under another agent's build produces
garbage results that look like real findings.

- Your pass: reproduce the failure for real. A failing test, a build that dies, a probe you
  ran. Not "the code looks like it would."
- Then go hands-off and tell Codex explicitly: what you already changed (so it does not
  redo it), that it has exclusive use of the tree, and whether it may run builds. If it only
  needs to read, say read-only. For anything needing a build, give it exclusive access or
  have it use a scratch copy under /tmp — a `git worktree` will not carry uncommitted changes.
- Ask Codex to **refute**, not confirm. Ask it to enumerate cases you missed. That is where
  it earns its keep: on past iterations it found a corrupting case where the declared and
  runtime types were *identical*, which the original write-up had missed entirely.

## 2. Plan, and argue about it

Send Codex the plan with the reasoning, not just the diff. Include:

- the root cause in one sentence;
- the option you chose **and the ones you rejected, with why**;
- the consequence you are least sure about, named explicitly.

Where two directions exist (raise CI vs lower the language level; surgical fix vs refactor),
say which you picked and what evidence decided it. Prefer the smaller fix; note the larger
one as follow-up rather than doing it.

## 3. Fix

Match surrounding style. Add a regression test that **fails before and passes after** — run
it both ways and keep the before/after output; it goes in the issue.

**Use `clean` for the verification you report.** Incremental `target/` state lies in both
directions: it has masked a real compile failure (a previous JDK's classes being reused), and
it has invented a failure that did not exist (broken classes and stale surefire reports left by
an earlier experiment, including reports for a test class no longer in the tree). Iterate
incrementally if you like, but the run you cite in an issue must start from `clean`.

Verify across the matrix the project actually supports. Here that means both persistence
providers: `./mvnw -o -pl <module> test` and again with
`-P__eclipselink-5.0-jakarta-ee-11,___hibernate-validator-9.1-jakarta-ee-11,___weld-6-jakarta-ee-11`.
Then the full reactor.

XML comments cannot contain `--`. YAML ones can.

## 4. Update the issue

Comment with: what was reproduced (paste the actual failure), why it was missed, the fix and
the direction not taken, verification output, and anything the fix does *not* cover. Close as
completed. If the fix creates new work, file it and link it.

State plainly when a fix is working-tree only and unpushed.

## Reporting each iteration

Lead with what changed and what it cost. Say when Codex corrected you — that is signal, not
embarrassment. Never claim a verification you did not run.
