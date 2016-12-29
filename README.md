# PktGenerator

Raw packet generator, intended to be used with [Jool](https://github.com/NICMx/NAT64)'s graybox testing framework.

## Build

```sh
mvn package
```

## Run

```sh
java -jar target/PktGenerator-<version>.jar [auto | manual | edit <packet file> | random]
```

Arguments:

## `auto`

Start with default headers, the user can override whatever needs to be changed.

Intended for packets where few header fields need to be specified.

This is the default option.

## `manual`

Start with nothing. The user must define every field, one by one.

Intended for the creation of very specific packets.

## `edit <packet file>`

Load a previously-generated packet, edit it as if `auto` mode had been chosen.

## `random`

Generate a likely valid random packet. No user input is involved.

The generator can currently only generate packets that consist of the following header sequence:

	IPv4 header [ + options ] + ICMPv4 error header + IPv4 header [ + options ] + TCP header + payload
