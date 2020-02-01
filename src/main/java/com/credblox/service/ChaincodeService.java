package com.credblox.service;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.credblox.adapter.client.CAClient;
import com.credblox.adapter.client.HyperLedgerFabricClient;

import com.credblox.adapter.client.ChannelClient;
import com.credblox.constants.NetworkConstants;
import com.credblox.domain.UserContext;
import main.java.com.credblox.util.NetworkUtil;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChaincodeService {

    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    public static void instantiate(String chainCodeName) {
        try {
            String CHAINCODE_1_PATH = "github.com/"+chainCodeName;
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(NetworkConstants.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder = new File(NetworkConstants.ORG1_USR_ADMIN_CERT);
            File[] certFiles = certFolder.listFiles();
            Enrollment enrollOrg1Admin = NetworkUtil.getEnrollment(NetworkConstants.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    NetworkConstants.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId("Org1MSP");
            org1Admin.setName("admin");

            UserContext org2Admin = new UserContext();
            File pkFolder2 = new File(NetworkConstants.ORG2_USR_ADMIN_PK);
            File[] pkFiles2 = pkFolder2.listFiles();
            File certFolder2 = new File(NetworkConstants.ORG2_USR_ADMIN_CERT);
            File[] certFiles2 = certFolder2.listFiles();
            Enrollment enrollOrg2Admin = NetworkUtil.getEnrollment(NetworkConstants.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
                    NetworkConstants.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollOrg2Admin);
            org2Admin.setMspId(NetworkConstants.ORG2_MSP);
            org2Admin.setName(NetworkConstants.ADMIN);

            HyperLedgerFabricClient fabClient = new HyperLedgerFabricClient(org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(NetworkConstants.CHANNEL_NAME);
            Orderer orderer = fabClient.getInstance().newOrderer(NetworkConstants.ORDERER_NAME, NetworkConstants.ORDERER_URL);
            Peer peer0_org1 = fabClient.getInstance().newPeer(NetworkConstants.ORG1_PEER_0, NetworkConstants.ORG1_PEER_0_URL);
            Peer peer1_org1 = fabClient.getInstance().newPeer(NetworkConstants.ORG1_PEER_1, NetworkConstants.ORG1_PEER_1_URL);
            Peer peer0_org2 = fabClient.getInstance().newPeer(NetworkConstants.ORG2_PEER_0, NetworkConstants.ORG2_PEER_0_URL);
            Peer peer1_org2 = fabClient.getInstance().newPeer(NetworkConstants.ORG2_PEER_1, NetworkConstants.ORG2_PEER_1_URL);
            mychannel.addOrderer(orderer);
            mychannel.addPeer(peer0_org1);
            mychannel.addPeer(peer1_org1);
            mychannel.addPeer(peer0_org2);
            mychannel.addPeer(peer1_org2);
            mychannel.initialize();

            List<Peer> org1Peers = new ArrayList<Peer>();
            org1Peers.add(peer0_org1);
            org1Peers.add(peer1_org1);

            List<Peer> org2Peers = new ArrayList<Peer>();
            org2Peers.add(peer0_org2);
            org2Peers.add(peer1_org2);

            Collection<ProposalResponse> response = fabClient.deployChainCode(chainCodeName,
                    CHAINCODE_1_PATH, NetworkConstants.CHAINCODE_ROOT_DIR, Type.JAVA.toString(),
                    NetworkConstants.CHAINCODE_1_VERSION, org1Peers);


            for (ProposalResponse res : response) {
                Logger.getLogger(ChaincodeService.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            fabClient.getInstance().setUserContext(org2Admin);

            response = fabClient.deployChainCode(chainCodeName,
                    CHAINCODE_1_PATH, NetworkConstants.CHAINCODE_ROOT_DIR, Type.JAVA.toString(),
                    NetworkConstants.CHAINCODE_1_VERSION, org2Peers);


            for (ProposalResponse res : response) {
                Logger.getLogger(ChaincodeService.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code deployment " + res.getStatus());
            }

            ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            String[] arguments = { "" };
            response = channelClient.instantiateChainCode(chainCodeName, NetworkConstants.CHAINCODE_1_VERSION,
                    CHAINCODE_1_PATH, Type.JAVA.toString(), "init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(ChaincodeService.class.getName()).log(Level.INFO,
                        chainCodeName + "- Chain code instantiation " + res.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String query(String chainCodeName) {
        String response = "";
        try {
            String CHAINCODE_1_PATH = "github.com/"+chainCodeName;
            NetworkUtil.cleanUp();
            String caUrl = NetworkConstants.CA_ORG1_URL;
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(NetworkConstants.ADMIN);
            adminUserContext.setAffiliation(NetworkConstants.ORG1);
            adminUserContext.setMspId(NetworkConstants.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(NetworkConstants.ADMIN, NetworkConstants.ADMIN_PASSWORD);

            HyperLedgerFabricClient fabClient = new HyperLedgerFabricClient(adminUserContext);

            ChannelClient channelClient = fabClient.createChannelClient(NetworkConstants.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(NetworkConstants.ORG1_PEER_0, NetworkConstants.ORG1_PEER_0_URL);
            EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(NetworkConstants.ORDERER_NAME, NetworkConstants.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(chainCodeName).build();
            request.setChaincodeID(ccid);
            request.setFcn("createAsset");
            String[] arguments = { "Asset1", "Chevy", "Volt", "Red", "Nick" };
            request.setArgs(arguments);
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
            // in transient map
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
            tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
            tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
            request.setTransientMap(tm2);
            Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);

			/*Thread.sleep(10000);

			Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode("fabcar", "queryAllCars", null);
			for (ProposalResponse pres : responsesQuery) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				System.out.println(stringResponse);
			}*/

            //Thread.sleep(10000);
            String[] args1 = {"Asset1"};
            Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode(chainCodeName, "queryAsset", args1);
            for (ProposalResponse pres : responses1Query) {
                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
                response = response.concat(stringResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}