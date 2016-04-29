[![Build Status](https://travis-ci.org/joyent/java-triton.svg?branch=master)](https://travis-ci.org/joyent/java-triton)

# Triton Java SDK

[java-triton](https://github.com/joyent/java-triton) is a community-maintained 
Java SDK for interacting with Joyent's Triton cloud provisioning interface - 
[CloudAPI](https://apidocs.joyent.com/cloudapi/).

This project is under active development. We are targeting our first release to
be 1.0.0 and all versions under 1.0.0 are considered beta builds. If you need
functionality that is not provided, please file a github issue and it will be
prioritized.

## Installation

### Requirements
* [Java 1.7](http://openjdk.java.net/install/) or higher.
* [Maven 3.3.x](https://maven.apache.org/)

### Using Maven
Add the latest java-manta dependency to your Maven `pom.xml`.

```xml
<dependency>
    <groupId>com.joyent.triton</groupId>
    <artifactId>java-triton-client</artifactId>
    <version>LATEST</version>
</dependency>
```

## Intention

The intent of this project is to provide an easy to use interface to Triton via
its REST API (CloudAPI). We intend to do this with the minimal amount of 
dependencies and maintain compatibility with Java 7 for the time being.

Ideally, the project is in a state where we can now start to add swaths of
functionality using the patterns that have been established in the existing 
code.

For the current status of what is implemented from the CloudAPI, refer to the
[ROADMAP.md](ROADMAP.md) document.

## Configuration
 
The entry point to the SDK is via the class ```com.joyent.triton.CloudApi```.
This class takes an instance of ```com.joyent.triton.config.ConfigContext``` as 
a single parameter to its constructor. Any implementation of the 
```ConfigContext``` interface will allow you to configure the SDK's parameters.
For ease of use, we provide a number of different chainable configuration classes.

| Class                       | Description                                                             |
|-----------------------------|-------------------------------------------------------|
| ChainedConfigContext        | allows you to chain together multiple config contexts |
| DefaultsConfigContext       | default values for settings                           |
| EnvVarConfigContext         | configuration from environment variables              |
| MapConfigContext            | configuration from a Map instance                     |
| StandardConfigContext       | configuration via a fluent interface                  |
| SystemSettingsConfigContext | configuration from Java system properties             |


### Defaults, Environment Variables and System Properties 

Configuration parameters take precedence from left to right - values on the
left are overridden by values on the right.

| Default                              | System Property            | Environment Variable               |
|--------------------------------------|----------------------------|------------------------------------|
| https://us-east-1.api.joyent.com:443 | triton.url                 | TRITON_URL, SDC_URL                |
|                                      | triton.user                | TRITON_USER, SDC_USER, SDC_ACCOUNT |                |
| $HOME/.ssh/id_rsa                    | triton.key_id              | TRITON_KEY_ID, SDC_KEY_ID          |
|                                      | triton.key_path            | TRITON_KEY_PATH, SDC_KEY_PATH      |
|                                      | triton.key_content         | TRITON_KEY_CONTENT                 |
|                                      | triton.password            | TRITON_PASSWORD                    |
| 20000                                | triton.timeout             | TRITON_TIMEOUT                     |
| 3                                    | triton.retries             | TRITON_HTTP_RETRIES                |
| 24                                   | triton.max_connections     | TRITON_MAX_CONNS                   |
| TLSv1.2                              | https.protocols            | TRITON_HTTPS_PROTOCOLS             |
| <value too big - see code>           | https.cipherSuites         | TRITON_HTTPS_CIPHERS               |
| false                                | triton.no_auth             | TRITON_NO_AUTH                     |
| false                                | triton.disable_native_sigs | TRITON_NO_NATIVE_SIGS              |

### Logging

The SDK utilizes [slf4j](http://www.slf4j.org/), and logging
can be configured using a SLF4J implementation.

## Usage

You'll need a Triton login, an associated cryptographic key, and its corresponding key
fingerprint. Note that this SDK currently only supports rsa ssh keys --
enterprising individuals wishing to use other keys can contribute to this repo by
consulting the [node-http-signing spec](https://github.com/joyent/node-http-signature/blob/master/http_signing.md).

For detailed usage instructions, consult the provided javadoc.

## Examples

 * [List instances example](src/examples/java/org/example/ListInstances.java)


## Contributions

Contributions welcome! Please read the [CONTRIBUTING.md](CONTRIBUTING.md) document for details
on getting started.

### Releasing

Please refer to the [release documentation](RELEASING.md).

### Bugs

See <https://github.com/joyent/java-triton/issues>.

## License
Triton Java is licensed under the MPLv2. Please see the `LICENSE.txt` file for more details.
