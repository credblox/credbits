#!/bin/bash
#
# Exit on first error, print all commands.
set -e

cd "$(dirname "$0")"
cd backup/ && cp -r * ../ && cd ../

java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.StartNetwork