#!/bin/bash

project=${1:-""}

semanticdbVersion="4.8.2"

if [ -z "$project" ]; then
  sbt 'set ThisBuild / semanticdbEnabled := true; clean; compile'
else
  sbt "project $project; set semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile; Test / compile"
fi


