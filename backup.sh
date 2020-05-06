#!/bin/bash
#
# Exit on first error, print all commands.
set -e

cd "$(dirname "$0")"
rm -rf peerOrganizations ordererOrganizations backup
mkdir backup

cp -r resources/crypto-config/  backup/
cp -r resources/config/ backup/

docker cp peer0.org1.example.com:/var/hyperledger/production/ backup/peer0.org1/
docker cp peer1.org1.example.com:/var/hyperledger/production/ backup/peer1.org1/

docker cp peer0.org2.example.com:/var/hyperledger/production/ backup/peer0.org2/
docker cp peer1.org2.example.com:/var/hyperledger/production/ backup/peer1.org2/

docker cp orderer.example.com:/var/hyperledger/production/orderer/ backup/orderer/