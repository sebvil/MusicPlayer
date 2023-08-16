#! /bin/sh
set -e
./gradlew detektAll
set +e
git add -A && git commit -m "lint"
gt upstack submit
