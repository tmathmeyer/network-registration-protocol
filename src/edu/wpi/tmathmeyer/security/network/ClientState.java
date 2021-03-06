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
package edu.wpi.tmathmeyer.security.network;

import edu.wpi.tmathmeyer.protocol.Packet;
import edu.wpi.tmathmeyer.security.network.packets.DistinctOptionPacket;
import edu.wpi.tmathmeyer.security.network.packets.DynamicNumberOptionsPacket;
import edu.wpi.tmathmeyer.security.network.packets.DynamicSizeOptionPacket;
import edu.wpi.tmathmeyer.security.network.packets.HashPacket;

public class ClientState {
	private String stateName;
	private Packet[] allowedPacketTypes;
	
	/**
	 * 
	 * @param s the statename
	 * @param p the list of valid packets
	 */
	public ClientState(String s, Packet[] p){
		this.setStateName(s);
		this.setAllowedPacketTypes(p);
	}
	
	/**
	 * 
	 * @param test the packet to be tested for validity
	 * @return whether this a a valid packet accoring to those supplied by the creation of this unit
	 */
	public boolean isAllowablePacketType(Packet test){
		for(Packet p : this.allowedPacketTypes)
			if (p.getClass().equals(test.getClass())) return true;
		return false;
	}

	/**
	 * @return the allowedPacketTypes
	 */
	public Packet[] getAllowedPacketTypes() {
		return allowedPacketTypes;
	}

	/**
	 * @param allowedPacketTypes the allowedPacketTypes to set
	 */
	public void setAllowedPacketTypes(Packet[] allowedPacketTypes) {
		this.allowedPacketTypes = allowedPacketTypes;
	}

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	public boolean equals(Object other){
		if (! (other instanceof ClientState))return false;
		return ((ClientState)other).stateName.equals(this.stateName);
	}
	
	
	public boolean isServerSide(){
		return this.stateName.startsWith("Server");
	}
	
	
	
	public static final Packet[] allPackets = {new DistinctOptionPacket((byte) 0), new DynamicSizeOptionPacket(null, 1), new HashPacket(null,0), new DynamicNumberOptionsPacket(null, 0)};
	
	public static final Packet[] S1 = {new DistinctOptionPacket((byte) 0), new DynamicSizeOptionPacket(null, 1)};
	public static final Packet[] S2 = {new DistinctOptionPacket((byte) 0), new HashPacket(null,0)};
	public static final Packet[] S3 = {new DynamicNumberOptionsPacket(null, 0)};
	public static final Packet[] S4 = {new DistinctOptionPacket((byte) 0)};
	public static final Packet[] S5 = {};
	
	public static final ClientState Server1 = new ClientState("ServerStateOne", S1);
	public static final ClientState Server2 = new ClientState("ServerStateTwo", S2);
	public static final ClientState Server3 = new ClientState("ServerStateThree", S3);
	public static final ClientState Server4 = new ClientState("ServerStateFour", S4);
	public static final ClientState Server5 = new ClientState("ServerStateFive", S5);
	
	
	
	public static final Packet[] C1 = {new HashPacket(null,0), new DistinctOptionPacket((byte) 0)};
	public static final Packet[] C2 = {new DistinctOptionPacket((byte) 0)};
	public static final Packet[] C3 = {new DistinctOptionPacket((byte) 0), new DynamicNumberOptionsPacket(null, 0)};
	
	public static final ClientState Client1 = new ClientState("ClientStatusOne", C1);
	public static final ClientState Client2 = new ClientState("ClientStatusTwo", C2);
	public static final ClientState Client3 = new ClientState("ClientStatusThree", C3);
	public static final ClientState ClientEmpty = new ClientState("ClientStateEmpty", S5);
}
