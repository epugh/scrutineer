# Contributing to Scrutineer

Thanks for taking the time to contribute! :heart:

## Building

Scrutineer is a Maven project, which really _should_ just build right out of the box if you have Maven installed.  Just type:

```
mvn package
```

And you should have a Tarball in the 'target' sub-directory.

## Submitting Pull Requests

First, Please add unit tests!

Second, Please add integration tests!

Third, We have tightened up the quality rule set for CheckStyle, PMD etc pretty hard.  Before you issue a pull request, please run:

```
mvn verify
```

which will run all quality checks.  Sorry to be super-anal, but we just like Clean Code.

## High level design
Scrutineer provides both a standalone application and a library for integration. 
The application is to compare two data source according to the specified config and output the differences to stdout.
The library `com.aconex.scrutineer2:verifier` is for integration with other service to handle the differences.

### Modules
* the standalone application entry point: [cli](cli) 
* shared internal model and utility class: [core](core)
* data source connectors: [jdbc](jdbc), [elasticsearch](elasticsearch), [http](http)
* data comparing logic: [verifier](verifier)

### Layers
1. `CLI`: handles command line input config and create data source connector accordingly
2. `IdAndVersionStreamConnector`: fetch data from the data source and convert to `Iterator<IdAndVersion>`
3. `IdAndVersionStreamVerifier`: compare two given data source by getting iterator from the data source connectors