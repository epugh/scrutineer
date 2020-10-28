Scrutineer Changelog
====================

Outlines key changes across versions (not exhaustive)

Scrutineer 6.8.x
-----------------
*BREAKING* 

* no longer ships with transitive dependencies including JDBC drivers (e.g. JTDS or Microsof SQL Server)
  Consumers are expected to explicitly include the JDBC driver they want (See README.md#JDBC Drivers)

*Other*

* explicitly uses upgraded transitive dependencies from Elasticsearch to remove CVSS score issue (see OWASP plugin results)   

Scrutineer 6.5.x
----------------

* aligned to Elasticsearch 6.5
* required introduction of the `--esHosts` option to specify the seed ES hosts to connect to (original Multicast Zen discovery is gone)
* Default Batch size to retrieve items from ES is downgraded from 100k => 10k to align with default ES maximum that has been introduced.

Scrutineer 1.7.6
----------------
* Alignment of versioning to align with version of Elasticsearch it is compatible with
* Upgrade to ES 1.7.6
* Replacement of JTDS MSSQL JDBC driver with Microsoft Driver
* general upgrade of dependency library components to latest version including testing (specifically Mockito 2)
* Upgrade of minimum Java version to 1.8
