package mx.nic.jool.pktgen.pojo;

import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.proto.Protocol;

public interface PacketContent {

	public void readFromStdIn(FieldScanner scanner);
	
	public byte[] toWire() throws IOException;

	public Protocol getProtocol();

	public String getShortName();

	public void postProcess(Packet packet, Fragment fragment)
			throws IOException;
	
	PacketContent createClone();

}
