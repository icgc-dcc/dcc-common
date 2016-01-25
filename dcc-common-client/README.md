ICGC DCC - Common Client
===

Provides clients for interaction with ICGC RESTful APIs.

See [Wiki](https://wiki.oicr.on.ca/display/icgcweb/Web+Development+-+ICGC+Home) for more details.

Build
---

In the repository's root execute from the command line:

        mvn -am -pl dcc-common-client package

Usage
---

Configure and create an ICGCClient

```java
ICGCClientConfig config = ICGCClientConfig.builder()
        .cgpServiceUrl(SERVICE_URL)
        .consumerKey(CONSUMER_KEY)
        .consumerSecret(CONSUMER_SECRET)
        .accessToken(ACCESS_TOKEN)
        .accessSecret(ACCESS_SECRET)
        .requestLoggingEnabled(true)
        .build();
ICGCClient client = ICGCClient.create(config);
```

Create a CGP API client and query for some information

```java
CGPClient cgp = client.cgp();
CancerGenomeProject project = cgp.details().getCancerGenomeProject(id);
```

NB
---
- [jul-to-slf4j](http://www.slf4j.org/legacy.html#jul-to-slf4j) should be enabled for logging HTTP requests to the ICGC REST API.
 The requests are logged at `INFO` level by the `com.sun.jersey.api.client.filter.LoggingFilter` logger.
