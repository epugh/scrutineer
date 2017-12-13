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
