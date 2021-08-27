package com.aconex.scrutineer2;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.elasticsearch.common.transport.TransportAddress;

// CHECKSTYLE:OFF This is the standard JCommander pattern
@Parameters(separators = "=")
public class ScrutineerCommandLineOptions {
    @Parameter(names = {"--help", "-h"}, description = "Print a usage (help) message", help = true)
    public boolean help = false;

    @Parameter(names = "--clusterName", description = "ElasticSearch cluster name identifier", required = true)
    public String clusterName;

    @Parameter(names= "--esHosts", description = "CSV set of seed ElasticSearch host:port pairs to use as part of discovery", required = true, listConverter= JcommanderTransportAddressParser.class)
    public List<TransportAddress> elasticSearchHosts;

    @Parameter(names = "--esUsername", description = "Elasticsearch Username")
    public String esUsername;

    @Parameter(names = "--esPassword", description = "Elasticsearch Password", password = true)
    public String esPassword;

    @Parameter(names = "--esSSLVerificationMode",
            description = "Transport client SSL verification mode, accepted values: [none, certificate, full]", validateValueWith = CmdLineOptionsSSLVerificationModeValidator.class)
    public String esSSLVerificationMode = "certificate";

    @Parameter(names = "--esSSLEnabled", description = "Enable SSL encryption for transport client")
    public boolean esSSLEnabled = false;

    @Parameter(names = "--indexName", description = "ElasticSearch index name to Verify", required = true)
    public String indexName;

    @Parameter(names = "--query", description = "ElasticSearch query to create Secondary stream.  Not required to be ordered", required = false)
    public String query = "*";

    @Parameter(names = "--jdbcDriverClass", description = "FQN of the JDBC Driver class", required = true)
    public String jdbcDriverClass;

    @Parameter(names = "--jdbcURL", description = "JDBC URL of the Connection of the Primary source", required = true)
    public String jdbcURL;

    @Parameter(names = "--jdbcUser", description = "JDBC Username", required = true)
    public String jdbcUser;

    @Parameter(names = "--jdbcPassword", description = "JDBC Password", required = false)
    public String jdbcPassword;

    @Parameter(names = "--sql", description = "SQL used to create Primary stream, which should return results in _lexicographical_ order", required = true)
    public String sql;

    @Parameter(names = "--numeric", description = "JDBC query is sorted numerically")
    public boolean numeric = false;

    @Parameter(names = "--versions-as-timestamps", description = "Assumes Version values are timestamps and are printed out in ISO8601 date/time format for convenience")
    public boolean versionsAsTimestamps = false;

    @Parameter(names = "--ignore-timestamps-during-run", description = "Will suppress any Version Mismatch warnings whose timestamps are after the start of a Scrutineer run (implies use of --versionsAsTimestamps)")
    public boolean ignoreTimestampsDuringRun = false;
}
// CHECKSTYLE:ON