package mx.nic.jool.pktgen.type;

import mx.nic.jool.pktgen.BitArrayOutputStream;

public class ByteArrayField implements Field {

	private String name;
	private byte[] value;

	public ByteArrayField(String name, byte[] defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}

	@Override
	public String toString() {
		if (value == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (byte bait : value)
			sb.append(bait).append(",");
		return sb.toString();
	}

	@Override
	public void parse(String value) {
		String[] strings = value.split(",");
		this.value = new byte[strings.length];
		for (int i = 0; i < strings.length; i++)
			this.value[i] = Byte.valueOf(strings[i]);
	}

	@Override
	public void write(BitArrayOutputStream out) {
		if (value != null)
			out.write(value);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLength() {
		return (value != null) ? (8 * value.length) : 0;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

}
