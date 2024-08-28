package com.ericsson.oss.axis.common;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public class RestServiceUtils {

    private RestServiceUtils() {}

    public static String cleanEndpointAddress(String endpointAddress) {
        Preconditions.checkArgument(StringUtils.isNotBlank(endpointAddress), "Endpoint address cannot be blank");

        String trimmed = endpointAddress.trim();

        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

}
