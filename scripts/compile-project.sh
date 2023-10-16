#!/bin/bash

project=${1:-"all"}

semanticdbVersion=${2:-"4.8.11"}

if [ "$project" == "all" ]; then
  sbt -client "set ThisBuild / semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile"
else
  sbt -client "project $project; set semanticdbEnabled := true; set semanticdbVersion := \"$semanticdbVersion\"; clean; compile; Test / compile"
fi


