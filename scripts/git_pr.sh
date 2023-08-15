#! /bin/sh
set -e
./gradlew ktlintFormat
./gradlew detekt
git add -A && git commit -m "lint"
gt upstack submit
