Analyses a secondary stream of information against a known point-of-truth and reports inconsistencies.

The Why
=======

When you have a Lucene-based index of substantial size, say many hundreds of millions of records, what you want is confidence
that your index is correct. In the many cases, people use Solr/ElasticSearch/Compass to index their central database, mongodb,
hbase etc so the index is a secondary storage of data.

How do you know if your index is accurate? Can you just reindex 500 million documents anytime you like? (That's the Aliens: "Nuke the site from
Orbit... It's the only way to be sure" approach). No, if there _ARE_ inconsistencies in your index, then you want to:

* find the items that are incorrect (and only them)
* do it fast

Scrutineer has been designed with this in mind, it can find any inconsistencies in your index fast.


How does this work?
===================

Scrutineer relies on your data having 2 core properties:

* an ID - a unique identifier for your object
* a Version - something stored in your primary datastore for that object that represents the temporal state of that object

The Version property is commonly used in an Optimistic Locking pattern. If you store the ID & Version information in your
secondary store (say, Solr/ElasticSearch) then you can always compare for any given item whether the version in secondary store is up
to date.

Scrutineer takes a stream from your primary, and a stream from your secondary store, presumes they are sorted identically (more
on that later) and walks the streams doing a merge comparison. It detects 4 states:

1. Both items are identical (yay!)
2. An ID is missing from the secondary stream (A missed add?  maybe that index message you sent to Solr/ElasticSearch never made it, anyway, it's not there)
3. An ID was detected in the secondary, but wasn't in the primary stream (A missed delete?  something was deleted on the primary, but the secondary never got the memo)
4. The ID exists in both streams, but the Version values are inconsistent (A missed update?  similar to the missed add, this time perhaps an update to a row in your DB never made it to Solr/ElasticSearch)

Example
=======
Here's an example, 2 streams in sorted order, one from the Database (your point-of-truth), 
and one from ElasticSearch (the one you're checking):

<table border="1">
  <tr><th>Database</th><th>ElasticSearch</th></tr>
  <tr><td>1:12345</td><td>3:84757</td></tr>
  <tr><td>2:23455</td><td>1:12345</td></tr>
  <tr><td>3:84757</td><td>4:98765</td></tr>
  <tr><td>4:98765</td><td>5:38475</td></tr>
  <tr><td>6:34556</td><td>6:34666</td></tr>
</table>

Scrutineer picks up that:

* ID '2' is missing from ElasticSearch
* ID '5' was deleted from the database at some point, but ElasticSearch still has it in there
* ID '6' is visible in ElasticSearch but appears to have the wrong version

Running Scrutineer
==================


    bin/scrutineer \
                --jdbcURL=jdbc:jtds:sqlserver://mydbhost/mydb  \
                --jdbcDriverClass=net.sourceforge.jtds.jdbc.Driver \
                --jdbcUser=itasecret \
                --jdbcPassword=itsasecret   \
                ---sql="select id,version from myobjecttype order by cast(id as varchar(100))" \
                --clusterName=mycluster \
                --indexName=myindex \
                --query="_type:myobjecttype"

*Note:* if you're weirded out about that '...cast(...)' then don't worry, we'll explain that shortly.

Output
======
Scrutineer writes any inconsistencies direct to Standard Error, in a well-defined, tab-separated format for easy parsing to feed into a
system to reindex/cleanup.  Example:


    NOTINSECONDARY    2    20
    MISMATCH    3    30    secondaryVersion=42
    NOTINPRIMARY    4    40

The general format is:

   **FailureType**\t**ID**\t**VERSION**\t**Optional:Additional Info**

### NOTINSECONDARY
This means you are missing this item in your secondary and you should reindex/readd to your secondary stream

### MISMATCH
This means the version of the object stored in the secondary is not the same information as the primary, and you should reindex

### NOTINPRIMARY
The object was removed from the Primary store, but the secondary still has it.  You should remove this item from your secondary.

Scrutineer does _not_ report when items match, we'll presume you're just fine with that...

Sorting
=======

Scrutineer relies on both streams to be sorted using an identical mechanism. Additionally, right now in this early version, it
requires the streams to be in lexicographical sort order (String sort, not numerical). This will not be difficult to overcome,
but in this early version because ElasticSearch's API has IDs based on Strings and it is actually fairly trivial to get the DB
to sort the stream lexicographically (all serious DB's should allow this, for MS SQL Server the above example does "... order
by cast(id as varchar(100))" ) and it's fast..

ElasticSearch
=============

Since Aconex uses ElasticSearch, Scrutineer supports ES out of the box, but it would not be difficult for others to integrate
a Solr stream and wire something up. Happy to take Pull Requests!

What are the 'best practices' for using Scrutineer?
===================================================

The authors of Srutineer, Aconex, index content from a JDBC data source and index using ElasticSearch.  We do the following:

* In the database table of the object being indexed we add a Insert/Update trigger to populate a 'lastupdated' timestamp column as our Version property
* When we index into ElasticSearch, we set the Version property of of the item using the VersionType.EXTERNAL setting.  
* We create an SQL Index on this tuple so these 2 fields can be retrieved from the database very fast


Assumptions
===========

* Your Version property is Long compatible.  Timestamps work fine though up to millisecond accuracy
* Aconex is DB->ElasticSearch centric at the moment.  We've tried to keep things loosely coupled, so it should be 
simple to add further integration points for other Primary & Secondary sources (HBase, MongoDB, Solr). 

Building
========
Scrutineer is a Maven project, which really _should_ just build right out of the box if you have Maven installed.  Just type:

    mvn package

And you should have a Tarball in the 'target' sub-directory.

Submitting Pull Requests
========================

First, Please add unit tests!

Second, Please add integration tests!

Third, We have tightened up the quality rule set for CheckStyle, PMD etc pretty hard.  Before you issue a pull request, please run:

    mvn verify

which will run all quality checks.  Sorry to super-anal, but we just like Clean Code.

Roadmap
=======

