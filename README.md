# PktGenerator

Raw packet generator, intended to be used with [Jool](https://github.com/NICMx/NAT64)'s graybox testing framework.

## Build

```sh
mvn package
```

## Run

Needs an adjacent file named `address.properties` that contains the default IPv4 and IPv6 header addresses. (You can find a sample in this directory.)
 
```sh
java -jar target/PktGenerator-<version>.jar
```

Will generate packets based on clipboard source.

Example packet source (Copy it, then run the jar):

	packet helper-jat: helper-jat's natural translation
		20	IPv4		swap ttl--
		8	UDP
		1205	Payload

	packet helper-jae: original packet
		40	IPv6		ttl--
		8	UDP
		1205	Payload

	packet jat: ICMP error to helper-jat, not truncated
		20	IPv4
		8	ICMPv4
		1233	Payload		file:helper-jat

	packet jae: translated jat, inner packet truncated
		40	IPv6		swap ttl--
		8	ICMPv6
		1232	Payload		file:helper-jae
