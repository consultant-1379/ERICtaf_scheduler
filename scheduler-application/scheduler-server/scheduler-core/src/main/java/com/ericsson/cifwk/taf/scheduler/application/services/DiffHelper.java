package com.ericsson.cifwk.taf.scheduler.application.services;

import com.google.common.collect.Sets;

import java.util.Set;

public class DiffHelper<K> {

    private final Sets.SetView<K> onlyInA;
    private final Sets.SetView<K> onlyInB;
    private final Sets.SetView<K> inBoth;

    public DiffHelper(Set<K> a, Set<K> b) {
        onlyInA = Sets.difference(a, b);
        onlyInB = Sets.difference(b, a);
        inBoth = Sets.intersection(a, b);
    }

    public Sets.SetView<K> getInBoth() {
        return inBoth;
    }

    public Sets.SetView<K> getOnlyInB() {
        return onlyInB;
    }

    public Sets.SetView<K> getOnlyInA() {
        return onlyInA;
    }
}
