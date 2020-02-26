#!/bin/bash
#
# Exit on first error, print all commands.
set -e

cd "$(dirname "$0")"
echo -e "\Deploying network..."
sleep 1
echo -e "\nTerminating blockchain network."
java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.TeardownNetwork
sleep 1
docker system prune --all
sleep 1
echo -e "\nStarting fresh network"
java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.StartNetwork
sleep 1
echo -e "\nCreating channel"
java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.CreateChannel
sleep 1
echo -e "\nInstantiating chaincode"
java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.InstantiateChaincode asset
sleep 1
echo -e "\nRegistering user"
java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.RegisterEnrollUser
sleep 1