#! /bin/sh
set -e
./gradlew lintFix
./gradlew detektMain
set +e
git add -A && git commit -m "lint"
gt upstack submit
