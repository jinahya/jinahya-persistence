#!/bin/sh
# Runs a Maven build of every Jakarta EE generation (jinahya-persistence-NN) against Hibernate ORM, with the
# hibernate-orm profile.
#
# `clean` is prepended to whatever you pass, and is not optional. The annotation processor writes a metamodel under
# target/generated-*-sources per provider, and the two providers do not generate the same one; without a clean the
# second provider compiles against the first's leftovers and fails with "cannot find symbol" on generated classes,
# which reads exactly like the profiles being broken, which they are not.
#
# Usage: ./_mvn_hibernate_orm.sh [maven args...]   e.g. ./_mvn_hibernate_orm.sh test
#        ./_mvn_hibernate_orm.sh -pl jinahya-persistence-11 -amd test   (one generation only)
cd "$(dirname "$0")" || exit 1

echo
echo "=== hibernate-orm"
if ./mvnw -Phibernate-orm clean "$@"; then
  echo "=== OK   hibernate-orm"
else
  echo "=== FAIL hibernate-orm"
  exit 1
fi
