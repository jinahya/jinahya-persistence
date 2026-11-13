#!/bin/sh
# Runs a Maven build of every Jakarta EE generation (jinahya-persistence-NN) against EclipseLink, with the
# eclipselink profile.
#
# `clean` is prepended to whatever you pass, and is not optional. The annotation processor writes a metamodel under
# target/generated-*-sources per provider, and the two providers do not generate the same one; without a clean the
# second provider compiles against the first's leftovers and fails with "cannot find symbol" on generated classes,
# which reads exactly like the profiles being broken, which they are not.
#
# Usage: ./_mvn_eclipselink.sh [maven args...]   e.g. ./_mvn_eclipselink.sh test
#        ./_mvn_eclipselink.sh -pl jinahya-persistence-11 -amd test   (one generation only)
cd "$(dirname "$0")" || exit 1

echo
echo "=== eclipselink"
if ./mvnw -Peclipselink clean "$@"; then
  echo "=== OK   eclipselink"
else
  echo "=== FAIL eclipselink"
  exit 1
fi
