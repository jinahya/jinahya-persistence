#!/bin/sh
# Runs a Maven build against every supported Jakarta EE 11 combination.
#
# Jakarta EE 11 is the only supported platform generation and its values live in the
# root pom's <properties>, so the platform is identical in every run below; only the
# implementations vary. The profiles form two axes -- persistence provider (__) and
# validator (___) -- and because the defaults are activeByDefault, naming a profile on
# one axis deactivates the others: every combination therefore names all of them.
#
# `clean` is prepended to whatever you pass, and is not optional. The annotation processor writes a
# metamodel under target/generated-*-sources per provider, and the two providers do not generate the
# same one; without a clean between combinations the second provider compiles against the first's
# leftovers and every combination after the first fails with "cannot find symbol" on generated
# classes which look nothing like the problem. That failure is silent about its cause and reads
# exactly like the profiles being broken, which they are not.
#
# Usage: ./_mvn_jakarta_ee_11.sh [maven args...]   e.g. ./_mvn_jakarta_ee_11.sh test
cd "$(dirname "$0")" || exit 1

PROVIDERS='__hibernate-orm-7.4-jakarta-ee-11 __hibernate-orm-7.2-jakarta-ee-11 __eclipselink-5.0-jakarta-ee-11'
VALIDATORS='___hibernate-validator-9.1-jakarta-ee-11 ___hibernate-validator-9.0-jakarta-ee-11'
CDI='___weld-6-jakarta-ee-11'

failed=''
for provider in $PROVIDERS; do
  for validator in $VALIDATORS; do
    profiles="$provider,$validator,$CDI"
    echo
    echo "=== $profiles"
    if ./mvnw -P"$profiles" clean "$@"; then
      echo "=== OK   $profiles"
    else
      echo "=== FAIL $profiles"
      failed="$failed $profiles"
    fi
  done
done

echo
if [ -n "$failed" ]; then
  echo "FAILED combinations:"
  for f in $failed; do echo "  $f"; done
  exit 1
fi
echo "all combinations succeeded"
