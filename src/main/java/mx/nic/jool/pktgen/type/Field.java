package mx.nic.jool.pktgen.type;

import mx.nic.jool.pktgen.BitArrayOutputStream;

public interface Field {

	public static int getLength(Field[] fields) {
		int bits = 0;
		for (Field field : fields)
			bits += field.getLength();

		if ((bits & 7) != 0) {
			System.err.println("Warning: Field group bit length is not divisible by 8.");
			return (bits >> 3) + 1; // Add padding
		}

		return bits >> 3;
	}

	public String getName();

	/* In bits. */
	public int getLength();

	public void parse(String value);

	public void write(BitArrayOutputStream out);

}
