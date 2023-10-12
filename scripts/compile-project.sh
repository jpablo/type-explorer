#!/bin/bash

project=${1:-""}

semanticdbVersion="4.8.11"

if [ -z "$project" ]; then
  sbt "set ThisBuild / semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile"
else
  sbt "project $project; set semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile; Test / compile"
fi


