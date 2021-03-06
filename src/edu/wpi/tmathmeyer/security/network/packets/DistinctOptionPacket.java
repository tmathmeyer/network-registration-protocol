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
package edu.wpi.tmathmeyer.security.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.wpi.tmathmeyer.protocol.Packet;
/**
 * 
 * @author Ted
 * 
 * This packet class supports up to TEN different options (0-9), to be arbitrarily
 * determined at reception by the other end of the connection
 * 
 * I would like these to be alog the lines of:
 *  0-no
 *  1-yes
 *  2-maybe
 *  ...
 *  8-???
 *  9-profit
 * 
 */
public class DistinctOptionPacket implements Packet{
	
	public byte option;
	
	public DistinctOptionPacket(byte b){
		this.option = b;
	}
	
	public DistinctOptionPacket(DataInputStream dis) throws IOException{
		this.option = dis.readByte();
	}
	
	@Override
	public byte getPacketID() {
		return (byte) 0x301;
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.write(this.getPacketID());
		dos.write(this.option);
		dos.flush();
	}

}
