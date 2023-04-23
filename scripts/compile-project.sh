#!/bin/bash

project=${1:-""}

if [ -z "$project" ]; then
  sbt 'set ThisBuild / semanticdbEnabled := true; clean; compile'
else
  sbt "project $project; set semanticdbEnabled := true; clean; compile"
fi
