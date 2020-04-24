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

Will generate a packet based on standard input.
