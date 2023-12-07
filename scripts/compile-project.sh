#!/bin/bash

project=${1:-"all"}

semanticdbVersion=${2:-"latest.release"}

if [ "$project" == "all" ]; then
  sbt "set ThisBuild / semanticdbEnabled := true; set ThisBuild / semanticdbVersion := \"$semanticdbVersion\"; clean; compile"
else
  sbt "project $project; set semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile; Test / compile"
fi


