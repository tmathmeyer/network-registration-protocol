package edu.wpi.tmathmeyer.security.network;

import java.util.List;

import edu.wpi.tmathmeyer.protocol.Packet;

public class ClientState {
	private String stateName;
	private List<Packet> allowedPacketTypes;
	
	public ClientState(String s, List<Packet> p){
		this.setStateName(s);
		this.setAllowedPacketTypes(p);
	}
	
	public boolean isAllowablePacketType(Packet test){
		for(Packet p : this.allowedPacketTypes)
			if (p.getClass().equals(test.getClass())) return true;
		return false;
	}

	/**
	 * @return the allowedPacketTypes
	 */
	public List<Packet> getAllowedPacketTypes() {
		return allowedPacketTypes;
	}

	/**
	 * @param allowedPacketTypes the allowedPacketTypes to set
	 */
	public void setAllowedPacketTypes(List<Packet> allowedPacketTypes) {
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
}
