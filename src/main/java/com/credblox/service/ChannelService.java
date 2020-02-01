//ToDo: Will create invoke scripts to create network, and channel

/******************************************************
 *  Copyright 2018 IBM Corporation
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.credblox.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

import com.credblox.adapter.client.HyperLedgerFabricClient;
import com.credblox.constants.NetworkConstants;
import com.credblox.domain.UserContext;
import com.credblox.domain.CAEnrollment;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.exception.CryptoException;

public class ChannelService {

    public static void createChannel() {
        try {
            CryptoSuite.Factory.getCryptoSuite();
            cleanUp();
            // Construct Channel
            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(NetworkConstants.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder1 = new File(NetworkConstants.ORG1_USR_ADMIN_CERT);
            File[] certFiles1 = certFolder1.listFiles();
            System.out.println("pkFiles1:");
            System.out.println(pkFiles1);
            System.out.println("certFiles1:");
            System.out.println(certFiles1);
            Enrollment enrollOrg1Admin = getEnrollment(NetworkConstants.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    NetworkConstants.ORG1_USR_ADMIN_CERT, certFiles1[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId(NetworkConstants.ORG1_MSP);
            org1Admin.setName(NetworkConstants.ADMIN);

            UserContext org2Admin = new UserContext();
            File pkFolder2 = new File(NetworkConstants.ORG2_USR_ADMIN_PK);
            File[] pkFiles2 = pkFolder2.listFiles();
            File certFolder2 = new File(NetworkConstants.ORG2_USR_ADMIN_CERT);
            File[] certFiles2 = certFolder2.listFiles();
            Enrollment enrollOrg2Admin = getEnrollment(NetworkConstants.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
                    NetworkConstants.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollOrg2Admin);
            org2Admin.setMspId(NetworkConstants.ORG2_MSP);
            org2Admin.setName(NetworkConstants.ADMIN);

            HyperLedgerFabricClient fabClient = new HyperLedgerFabricClient(org1Admin);

            // Create a new channel
            Orderer orderer = fabClient.getInstance().newOrderer(NetworkConstants.ORDERER_NAME, NetworkConstants.ORDERER_URL);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(NetworkConstants.CHANNEL_CONFIG_PATH));

            byte[] channelConfigurationSignatures = fabClient.getInstance()
                    .getChannelConfigurationSignature(channelConfiguration, org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(NetworkConstants.CHANNEL_NAME, orderer, channelConfiguration,
                    channelConfigurationSignatures);

            Peer peer0_org1 = fabClient.getInstance().newPeer(NetworkConstants.ORG1_PEER_0, NetworkConstants.ORG1_PEER_0_URL);
            Peer peer1_org1 = fabClient.getInstance().newPeer(NetworkConstants.ORG1_PEER_1, NetworkConstants.ORG1_PEER_1_URL);
            Peer peer0_org2 = fabClient.getInstance().newPeer(NetworkConstants.ORG2_PEER_0, NetworkConstants.ORG2_PEER_0_URL);
            Peer peer1_org2 = fabClient.getInstance().newPeer(NetworkConstants.ORG2_PEER_1, NetworkConstants.ORG2_PEER_1_URL);

            mychannel.joinPeer(peer0_org1);
            mychannel.joinPeer(peer1_org1);

            mychannel.addOrderer(orderer);

            mychannel.initialize();

            fabClient.getInstance().setUserContext(org2Admin);
            mychannel = fabClient.getInstance().getChannel("mychannel");
            mychannel.joinPeer(peer0_org2);
            mychannel.joinPeer(peer1_org2);

            Logger.getLogger(ChannelService.class.getName()).log(Level.INFO, "Channel created "+mychannel.getName());
            Collection peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext())
            {
                Peer pr = (Peer) peerIter.next();
                Logger.getLogger(ChannelService.class.getName()).log(Level.INFO,pr.getName()+ " at " + pr.getUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create enrollment from key and certificate files.
     *
     * @param folderPath
     * @param keyFileName
     * @param certFileName
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws CryptoException
     */
    public static CAEnrollment getEnrollment(String keyFolderPath,  String keyFileName,  String certFolderPath, String certFileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
        PrivateKey key = null;
        String certificate = null;
        InputStream isKey = null;
        BufferedReader brKey = null;

        try {

            isKey = new FileInputStream(keyFolderPath + File.separator + keyFileName);
            brKey = new BufferedReader(new InputStreamReader(isKey));
            StringBuilder keyBuilder = new StringBuilder();

            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
                if (line.indexOf("PRIVATE") == -1) {
                    keyBuilder.append(line);
                }
            }

            certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));

            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            key = kf.generatePrivate(keySpec);
        } finally {
            isKey.close();
            brKey.close();
        }

        CAEnrollment enrollment = new CAEnrollment(key, certificate);
        return enrollment;
    }

    public static void cleanUp() {
        String directoryPath = "users";
        File directory = new File(directoryPath);
        deleteDirectory(directory);
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        // either file or an empty directory
        Logger.getLogger(ChannelService.class.getName()).log(Level.INFO, "Deleting - " + dir.getName());
        return dir.delete();
    }

}
