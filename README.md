# blox-network
Blox-network is part of the Blox framework that aims to make building &amp; operating blockchain networks easy &amp; secure

What is blox framework?

    - Blox toolkit provides a framework & set of guiding principles that makes it easy to build & operate a blockchain network
    - Why an open source framework for blockchain?
        - blox <3 open source 
        - Parts of what we could build with blox is powered by what has been a remarkable contribution from open source community to the hyperledger fabric platform
        - Open consensus about the type of network being deployed
    - Basic implementation flow
        - Build network > Invoke & instantiate chain-code > Operate the network

- Blox Support (Slack channel - coming soon!)


# Steps to start a hyperledger fabric network, install & query chaincode 

- cd (your-repository-base-directory)
- mvn install

1. java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.StartNetwork
2. java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.CreateChannel
3. java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.InstantiateChaincode organic
4. java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.RegisterEnrollUser
5. java -cp target/blox-network-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.credblox.application.QueryChaincode organic

