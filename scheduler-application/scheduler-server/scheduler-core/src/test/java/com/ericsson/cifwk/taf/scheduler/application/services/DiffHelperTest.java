package com.ericsson.cifwk.taf.scheduler.application.services;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class DiffHelperTest {

    @Test
    public void shouldReturnRecordsOnlyA() {
        DiffHelper<Integer> differenceManager = new DiffHelper<>(
                Sets.newHashSet(1, 2, 3),
                Sets.newHashSet(3, 4, 5));

        assertThat(differenceManager.getOnlyInA(), containsInAnyOrder(1, 2));
        assertThat(differenceManager.getOnlyInA(), hasSize(2));
    }

    @Test
    public void shouldReturnRecordsOnlyB() {
        DiffHelper<Integer> differenceManager = new DiffHelper<>(
                Sets.newHashSet(1, 2, 3),
                Sets.newHashSet(3, 4, 5));

        assertThat(differenceManager.getOnlyInB(), containsInAnyOrder(4, 5));
        assertThat(differenceManager.getOnlyInB(), hasSize(2));
    }

    @Test
    public void shouldReturnIntersection() {
        DiffHelper<Integer> differenceManager = new DiffHelper<>(
                Sets.newHashSet(1, 2, 3),
                Sets.newHashSet(3, 4, 5));

        assertThat(differenceManager.getInBoth(), contains(3));
        assertThat(differenceManager.getInBoth(), hasSize(1));
    }
}
