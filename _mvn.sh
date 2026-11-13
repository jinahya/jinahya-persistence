#!/bin/sh
# Runs a Maven build of every Jakarta EE generation against every persistence provider, by running each of
#   _mvn_hibernate_orm.sh
#   _mvn_eclipselink.sh
# and continuing past a failure, so every provider is reported.
#
# Usage: ./_mvn.sh [maven args...]   e.g. ./_mvn.sh test
cd "$(dirname "$0")" || exit 1

failed=''
for script in _mvn_hibernate_orm.sh _mvn_eclipselink.sh; do
  ./"$script" "$@" || failed="$failed $script"
done

echo
if [ -n "$failed" ]; then
  echo "FAILED (_mvn.sh):"
  for f in $failed; do echo "  $f"; done
  exit 1
fi
echo "all succeeded (_mvn.sh)"
