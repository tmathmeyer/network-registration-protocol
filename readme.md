This is an extension of the Synchronous-Network-Protocol found at https://github.com/tmathmeyer/Synchronous-Network-Protocol

to use this, there are a few simple steps, as shown in the example classes. however, there are a few things I didnt do in them that I should have done, had I to build an actual system out of them. (that's on the way)

1. Make a new class that extends Client:
	you should override the following methods:
		processPacket(Packet p);
			the first line in this method should be:
				p = super.processPacket(p);
			this will return null if the system has not been authenticated. youshould have no reason to access it. If it has been activated, you should have full access to all the packets.
		print(Object o);
			should probably use some sort of custom output writer, if you would like, or system.out
		generateSalt();
			try to do random?
2. I havent thought of any more yet, ill post them later :D