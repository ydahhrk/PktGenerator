package mx.nic.jool.pktgen.type;

import java.net.InetAddress;
import java.net.UnknownHostException;

import mx.nic.jool.pktgen.BitArrayOutputStream;

public class IpAddrField implements Field {

	private String name;
	private InetAddress value;
	
	public IpAddrField(String name, InetAddress defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}

	@Override
	public String toString() {
		return (value != null) ? value.toString() : null;
	}

	@Override
	public void parse(String value) {
		try {
			this.value = InetAddress.getByName(value);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Apparently not an IP address: " + value, e);
		}
	}

	@Override
	public void write(BitArrayOutputStream out) {
		if (value == null)
			throw new NullPointerException("value");
		out.write(value.getAddress());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLength() {
		return 8 * value.getAddress().length;
	}

	public InetAddress getValue() {
		return value;
	}

	public void setValue(InetAddress value) {
		this.value = value;
	}

}
