package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by evlailj on 16/06/2015.
 */
public class ConfiguredGson {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    public String toJson(Object src) {
        return gson.toJson(src);
    }

}
