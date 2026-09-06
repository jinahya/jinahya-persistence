# TODO — staging

Working list only. An item lives here while it is being worked out; once it is filed as a
GitHub issue it is **removed from this file**. The backlog itself lives in
[the issue tracker](https://github.com/jinahya/jinahya-persistence/issues).

**Currently staged: nothing.** All 53 items from the audit were filed as issues #4-#56.

## Labels

| Label | Meaning |
| --- | --- |
| `jinahya-persistence-crypto` / `-more` / `-utils` / `-test-utils` / `-more-test-utils` | the module |
| `cross-cutting` | build, CI, POM, `module-info`; spans modules |
| `bug` / `enhancement` / `documentation` / `test-coverage` | what kind of work it is |
| `severity: high` | data loss, silent corruption, or a broken build |
| `severity: medium` | a real defect with a bounded blast radius |
| `severity: low` | cosmetic, documentation, or cleanup |
| `verified` | reproduced by executing it, not only by reading the source |

Note the two stale module labels, `jinahya-persistence-mapped` and
`jinahya-persistence-mapped-test`: they predate the current module names and are unused by
the issues above. Delete them, or map them onto the modules that replaced them.

## Where the backlog came from

Four independent passes over every module, reconciled against each other: one direct read of
all main and test sources plus the poms, root profiles and CI workflow; and three Codex
audits, each run without sight of this file or of the others.

Agreement across passes was the strongest signal, and the cross-check corrected the record
several times in both directions — including three claims that had already been written down
and turned out to be wrong (an empty-delimiter failure mode, an assumption that `@Size` was
honored by `easy-random-bean-validation`, and a Jakarta Persistence citation that could not be
substantiated). Two Codex passes also corrected their own coverage claims. Each issue carries
that provenance in its footer, and the eight `verified` issues were reproduced by execution or
by inspecting a binary rather than by reading.

## Adding to this file

Stage a new item here as `### <short title>` with enough detail to act on — the mechanism,
file:line anchors, a concrete failure scenario, and how it was verified. File it as an issue
when it is ready, then delete it from here.
