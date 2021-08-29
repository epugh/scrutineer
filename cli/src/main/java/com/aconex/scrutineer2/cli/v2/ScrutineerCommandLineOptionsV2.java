package com.aconex.scrutineer2.cli.v2;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

// CHECKSTYLE:OFF This is the standard JCommander pattern
@Parameters(separators = "=")
public class ScrutineerCommandLineOptionsV2 {
    @Parameter(names = {"--help", "-h"}, description = "Print a usage (help) message", help = true)
    public boolean help = false;

    @Parameter(names = "--primary-config", description = "configuration property for the primary stream connector, placed under 'config' directory", required = true)
    public String primaryConfig;

    @Parameter(names = "--secondary-config", description = "configuration property for the secondary stream connector, placed under 'config' directory", required = true)
    public String secondaryConfig;

    @Parameter(names = "--numeric", description = "JDBC query is sorted numerically")
    public boolean numeric = false;

    @Parameter(names = "--versions-as-timestamps", description = "Assumes Version values are timestamps and are printed out in ISO8601 date/time format for convenience")
    public boolean versionsAsTimestamps = false;

    @Parameter(names = "--ignore-timestamps-during-run", description = "Will suppress any Version Mismatch warnings whose timestamps are after the start of a Scrutineer run (implies use of --versionsAsTimestamps)")
    public boolean ignoreTimestampsDuringRun = false;
}
// CHECKSTYLE:ON