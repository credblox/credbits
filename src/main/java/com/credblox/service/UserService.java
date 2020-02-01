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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.credblox.adapter.client.CAClient;
import com.credblox.constants.NetworkConstants;
import com.credblox.domain.UserContext;
import main.java.com.credblox.util.NetworkUtil;

/**
 * 
 * @author Balaji Kadambi
 *
 */

public class UserService {

	public static void main(String args[]) {
		try {
			cleanUp();
			String caUrl = NetworkConstants.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(NetworkConstants.ADMIN);
			adminUserContext.setAffiliation(NetworkConstants.ORG1);
			adminUserContext.setMspId(NetworkConstants.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(NetworkConstants.ADMIN, NetworkConstants.ADMIN_PASSWORD);

			// Register and Enroll user to Org1MSP
			UserContext userContext = new UserContext();
			String name = "user"+System.currentTimeMillis();
			userContext.setName(name);
			userContext.setAffiliation(NetworkConstants.ORG1);
			userContext.setMspId(NetworkConstants.ORG1_MSP);

			String eSecret = caClient.registerUser(name, NetworkConstants.ORG1);

			userContext = caClient.enrollUser(userContext, eSecret);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "Deleting - " + dir.getName());
		return dir.delete();
	}
	
	public static void registerEnrollUser() {
		try {
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

			// Register and Enroll user to Org1MSP
			UserContext userContext = new UserContext();
			String name = "user"+System.currentTimeMillis();
			userContext.setName(name);
			userContext.setAffiliation(NetworkConstants.ORG1);
			userContext.setMspId(NetworkConstants.ORG1_MSP);

			String eSecret = caClient.registerUser(name, NetworkConstants.ORG1);

			userContext = caClient.enrollUser(userContext, eSecret);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
