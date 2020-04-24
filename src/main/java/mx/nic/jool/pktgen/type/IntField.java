package mx.nic.jool.pktgen.type;

import mx.nic.jool.pktgen.BitArrayOutputStream;

public class IntField implements Field {

	public static final int FLAG_HEX = 1;
	
	private String name;
	private int bits;
	private Integer value;
	private int flags;

	public IntField(String name, int bits, Integer defaultValue) {
		this(name, bits, defaultValue, 0);
	}

	public IntField(String name, int bits, Integer defaultValue, int flags) {
		this.name = name;
		this.bits = bits;
		this.value = defaultValue;
		this.flags = flags;
	}

	@Override
	public String toString() {
		if (value == null)
			return null;

		if ((flags & FLAG_HEX) != 0)
			return String.format("0x%X", value);

		return value.toString();
	}

	@Override
	public void parse(String value) {
		this.value = Integer.valueOf(value);
	}

	@Override
	public void write(BitArrayOutputStream out) {
		if (value == null)
			throw new NullPointerException(name);
		long value = this.value;
		for (int i = 0; i < bits; i++)
			out.write((byte) ((value >> (bits - i - 1)) & 1), 1);
	}

	public String getName() {
		return name;
	}

	@Override
	public int getLength() {
		return bits;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
