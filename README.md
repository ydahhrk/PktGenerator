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

Build a packet from scratch, creating headers from a list of templates and updating them.

This is the default option.

## `edit <packet file>`

Load a previously-generated packet, edit it as if `auto` mode had been chosen.

## `random`

Generate a likely valid random packet. No user input is involved.
