package com.ericsson.cifwk.taf.scheduler.integration.tms.model;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by eniakel on 24/02/2016.
 */
public class TestCampaign {

    private String totalCount;

    private List<Item> items;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        @NotNull
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}