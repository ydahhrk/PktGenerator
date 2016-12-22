package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;

public class Icmpv4ErrorHeader extends Icmpv4Header {

	public Icmpv4ErrorHeader() {
		type = 3;
		code = 3;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv4ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "i4err";
	}

	@Override
	protected PacketContent getNextContent() {
		return new Ipv4Header();
	}

	@Override
	public void randomize() {
		super.randomize();

		Integer[][] availableTypes = { //
				/* Destination Unreachable */
				{ 3, 0 }, //
				{ 3, 1 }, //
				{ 3, 2 }, //
				{ 3, 3 }, //
				{ 3, 4 }, //
				{ 3, 5 }, //
				{ 3, 6 }, //
				{ 3, 7 }, //
				{ 3, 8 }, //
				{ 3, 9 }, //
				{ 3, 10 }, //
				{ 3, 11 }, //
				{ 3, 12 }, //
				{ 3, 13 }, //
				{ 3, 14 }, //
				{ 3, 15 }, //
				/* Time Exceeded */
				{ 11, null }, //
				/* Parameter Problem */
				{ 12, 0 }, //
				{ 12, 1 }, //
				{ 12, 2 }, //
		};

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int chosen = random.nextInt(availableTypes.length);
		this.type = availableTypes[chosen][0];
		if (availableTypes[chosen][1] != null)
			this.code = availableTypes[chosen][1];
	}
}
