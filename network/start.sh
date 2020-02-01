#!/bin/bash
#
# Exit on first error, print all commands.
set -e

cd "$(dirname "$0")"
echo -e "\nSetting up the Hyperledger Fabric 1.4 network..."
docker-compose -f docker-compose.yml up -d
sleep 15
echo -e "\nNetwork setup completed!!\n"

