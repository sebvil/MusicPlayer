#! /bin/sh
set -e
./gradlew detektMain
set +e
git add -A && git commit -m "lint"
gt upstack submit
