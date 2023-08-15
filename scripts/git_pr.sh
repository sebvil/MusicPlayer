#! /bin/sh
set -e
./gradlew ktlintFormat
./gradlew detekt
set +e
git add -A && git commit -m "lint"
gt upstack submit
