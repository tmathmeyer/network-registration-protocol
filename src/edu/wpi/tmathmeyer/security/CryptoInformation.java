package edu.wpi.tmathmeyer.security;

import edu.wpi.tmathmeyer.protocol.Packet;

public class CryptoInformation {
	public static Packet[] pkts = {new AuthPacket(new byte[0], "", (byte) 0),  new InformationPacket("")};
}
