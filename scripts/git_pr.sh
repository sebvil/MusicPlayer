#! /bin/sh
set -e
./gradlew check
set +e
git add -A && git commit -m "lint"
gt upstack submit
