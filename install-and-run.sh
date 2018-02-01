#!/usr/bin/env bash

(cd application && ./gradlew clean build --refresh-dependencies)

docker build -t falconio-assessment -f docker/Dockerfile .

docker-compose -f docker/docker-compose.yml up