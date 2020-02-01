#!/bin/bash
#
# Exit on first error, print all commands.

cd "$(dirname "$0")"
# Shut down the Docker containers that might be currently running.
docker-compose -f docker-compose.yml stop
echo -e "\nNetwork shutdown completed!!\n"