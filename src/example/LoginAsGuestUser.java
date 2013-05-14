/*******************************************************************************
 * Copyright (c) 2013 Ted Meyer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ted Meyer - initial API and implementation
 ******************************************************************************/
package example;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.wpi.tmathmeyer.security.network.Client;
import edu.wpi.tmathmeyer.security.network.ClientManager;
import edu.wpi.tmathmeyer.security.network.ClientState;
import edu.wpi.tmathmeyer.security.network.packets.DistinctOptionPacket;

public class LoginAsGuestUser implements ClientManager{
	
	Client c;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		Socket s = new Socket("localhost", 8338);
		new LoginAsGuestUser(s);
	}
	
	public LoginAsGuestUser(Socket s) throws IOException{
		Client c = new Client(ClientState.ClientEmpty, this, s);
		this.addNewClient(c);
		this.c.setCurrentState(ClientState.Client2);
		this.c.sendPacket(new DistinctOptionPacket((byte) 0));
	}
	
	@Override
	public String getAvailName() {
		return "seeeeeexy";
	}

	@Override
	public void addNewClient(Client c) {
		this.c = c;
	}

}
