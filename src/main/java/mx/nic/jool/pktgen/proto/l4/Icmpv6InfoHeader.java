package mx.nic.jool.pktgen.proto.l4;

import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

/**
 * A header representing the meat of an IPv6 ping.
 */
public class Icmpv6InfoHeader extends Icmpv6Header {

	public Icmpv6InfoHeader() {
		type = 128;
		code = 0;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv6InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i6info";
	}

	@Override
	protected Header getNextHeader() {
		return new Payload();
	}

	@Override
	public void randomize() {
		super.randomize();

		ThreadLocalRandom random = ThreadLocalRandom.current();
		this.type = random.nextBoolean() ? 128 : 129;
	}
}
