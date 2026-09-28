#!/bin/sh
# Runs a Maven build against both supported persistence providers.
#
# Jakarta EE 11 is the only supported platform generation, and every implementation in this build is
# the single release aligned with it, pinned in the root pom's <properties>. So there is no version
# matrix here: Hibernate Validator, Expressly and Weld are never varied (varying them would test
# those projects, not this one), and each provider has exactly one version. The only axis left is
# WHICH persistence provider implements Jakarta Persistence -- Hibernate ORM or EclipseLink -- which
# is a genuine behavioural difference this project has to keep working against.
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

PROVIDERS='jakarta-ee-11-hibernate-orm jakarta-ee-11-eclipselink'

failed=''
for provider in $PROVIDERS; do
  echo
  echo "=== $provider"
  if ./mvnw -P"$provider" clean "$@"; then
    echo "=== OK   $provider"
  else
    echo "=== FAIL $provider"
    failed="$failed $provider"
  fi
done

echo
if [ -n "$failed" ]; then
  echo "FAILED providers:"
  for f in $failed; do echo "  $f"; done
  exit 1
fi
echo "all providers succeeded"
