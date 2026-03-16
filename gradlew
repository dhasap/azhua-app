#!/bin/sh
# Simple Gradle wrapper script
exec java -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"
