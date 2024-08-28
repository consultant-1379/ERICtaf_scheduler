package com.ericsson.cifwk.taf.scheduler.common;

import org.hamcrest.Matcher;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertThat;

public class AssertionHelper {
    public static void validateHasProperEntities(Collection<? extends AbstractPersistable<Long>> entites, Matcher<Iterable<? extends Long>> containsIdMatcher) {
        List<Long> ids = entites.stream().map(AbstractPersistable::getId).collect(toList());
        assertThat(ids, containsIdMatcher);
    }
}
