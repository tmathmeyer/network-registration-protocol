package edu.wpi.tmathmeyer.security.network;

public interface ClientManager {
	public String getAvailName();
	public void addNewClient(Client c);
	public String getUserDatabaseName();
	public String getDatabaseLocation();
}
