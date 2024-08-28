package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.schedules.ScheduleService;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.model.Comment;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;

@Service
public class SchedulePromotionService {

    @Autowired
    DropRepository dropRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    UserMapper userMapper;

    public void promoteSchedulesByDrop(String productName, List<Drop> newDrops) {
        List<Drop> allDrops = dropRepository.findByProductName(productName);

        List<Drop> existingDrops = allDrops.stream()
                                            .filter(d -> !newDrops.contains(d))
                                            .collect(toList());

        if (!existingDrops.isEmpty()) {
            String latestExistingDropName = getLatestDropName(existingDrops);
            Drop latestExistingDrop = existingDrops.stream()
                    .filter(d -> latestExistingDropName.equals(d.getName()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            copySchedules(latestExistingDrop, newDrops);
        }
    }

    private void copySchedules(Drop latestExistingDrop, List<Drop> newDrops) {
        List<Schedule> existingSchedules = scheduleRepository.findLatestVersionsByDrop(latestExistingDrop);

        newDrops.forEach(d ->
            existingSchedules.forEach(s -> {
                Schedule copiedSchedule = s.copyForPromotionTo(d);

                s.getComments().forEach(c -> {
                    Comment copiedComment = c.copy(copiedSchedule);
                    copiedSchedule.addComment(copiedComment);
                });
                Schedule savedSchedule = scheduleRepository.save(copiedSchedule);

                s.getReviewers().forEach(r ->
                    scheduleService.addReviewer(savedSchedule.getId(), savedSchedule.getVersion(), userMapper.map(r))
                );
            })
        );
    }

    public String getLatestDropName(List<Drop> drops) {
        List<DefaultArtifactVersion> versions = drops.stream()
                .map(d -> new DefaultArtifactVersion(d.getName()))
                .collect(toList());
        return versions.stream()
                .sorted((v1, v2) -> v2.compareTo(v1))
                .collect(toList())
                .get(0)
                .toString();
    }
}
