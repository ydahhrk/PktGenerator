package mx.nic.jool.pktgen.type;

import mx.nic.jool.pktgen.BitArrayOutputStream;

public class BoolField implements Field {

	private String name;
	private Boolean value;

	public BoolField(String name, Boolean defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}

	@Override
	public String toString() {
		return (value != null) ? value.toString() : null;
	}

	@Override
	public void parse(String value) {
		switch (value) {
		case "true":
			this.value = true;
			break;
		case "false":
			this.value = false;
			break;
		default:
			throw new IllegalArgumentException("'" + value + "' is not a valid boolean.");
		}
	}

	@Override
	public void write(BitArrayOutputStream out) {
		if (value == null)
			throw new NullPointerException("value");
		out.write(value ? 1 : 0, 1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLength() {
		return 1;
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

}
