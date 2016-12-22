package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;

public class Icmpv6InfoHeader extends Icmpv6Header {

	public Icmpv6InfoHeader() {
		type = 128;
		code = 0;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv6InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i6info";
	}

	@Override
	protected PacketContent getNextContent() {
		return new Payload();
	}

	@Override
	public void randomize() {
		super.randomize();

		ThreadLocalRandom random = ThreadLocalRandom.current();
		this.type = random.nextBoolean() ? 128 : 129;
	}
}
