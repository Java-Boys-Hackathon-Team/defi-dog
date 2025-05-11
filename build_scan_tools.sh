#!/usr/bin/env bash

docker build -t slither-scanner -f slither.Dockerfile .

docker build -t mythril-scanner -f mythril.Dockerfile .