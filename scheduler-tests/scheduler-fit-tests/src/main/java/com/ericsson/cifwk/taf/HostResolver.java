package com.ericsson.cifwk.taf;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;

public class HostResolver {

    public static final String TAF_SCHEDULER = "scheduler";

    public static Host resolve() {
        return DataHandler.getHostByName(TAF_SCHEDULER);
    }

}
