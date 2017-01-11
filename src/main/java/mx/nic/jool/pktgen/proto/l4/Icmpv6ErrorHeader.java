package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;

/**
 * A header representing the meat of an IPv6 error report.
 */
public class Icmpv6ErrorHeader extends Icmpv6Header {

	public Icmpv6ErrorHeader() {
		type = 1;
		code = 4;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv6ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "i6err";
	}

	@Override
	protected Header getNextHeader() {
		return new Ipv6Header();
	}

	@Override
	public void randomize() {
		super.randomize();

		Integer[][] availableTypes = { //
				/* Destination Unreachable */
				{ 1, 0 }, //
				{ 1, 1 }, //
				{ 1, 2 }, //
				{ 1, 3 }, //
				{ 1, 4 }, //
				/* Packet Too Big */
				{ 2, null }, //
				/* Time Exceeded */
				{ 3, null }, //
				/* Parameter problem */
				{ 4, 0 }, //
				{ 4, 1 }, //
				{ 4, 2 }, //
		};

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int chosen = random.nextInt(availableTypes.length);
		this.type = availableTypes[chosen][0];
		if (availableTypes[chosen][1] != null)
			this.code = availableTypes[chosen][1];
	}
}
