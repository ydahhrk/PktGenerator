# PktGenerator

Raw packet generator, intended to be used with [Jool](https://github.com/NICMx/NAT64)'s graybox testing framework.

## Build

```sh
mvn package
```

## Run

Needs an adjacent file named `address.properties` that contains the default IPv4 and IPv6 header addresses. (You can find a sample in this directory.)
 
```sh
java -jar target/PktGenerator-<version>.jar [auto | edit <packet file> | random]
```

Arguments:

### `auto`

Build a packet from scratch, creating headers from a list of templates and updating them.

This is the default option.

### `edit <packet file>`

Load a previously-generated packet, edit it as if `auto` mode had been chosen.

### `random`

Generate a likely valid random packet. No user input is involved.

The resulting file is `random.pkt`.
