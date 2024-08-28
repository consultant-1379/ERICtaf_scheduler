package com.ericsson.cifwk.taf.scheduler.application.schedules;

import com.ericsson.cifwk.taf.scheduler.application.constant.KgbConstant;
import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.application.services.DropService;
import com.ericsson.cifwk.taf.scheduler.application.users.UserService;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.constant.BuildType;
import com.ericsson.cifwk.taf.scheduler.api.dto.CommentInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.SimpleScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.UserInfo;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.CommentMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.ScheduleMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.mapping.UserMapper;
import com.ericsson.cifwk.taf.scheduler.infrastructure.template.TemplateService;
import com.ericsson.cifwk.taf.scheduler.integration.email.EmailService;
import com.ericsson.cifwk.taf.scheduler.model.Comment;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import com.ericsson.cifwk.taf.scheduler.model.User;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class ScheduleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    @Value("${application.hostname:localhost}")
    String hostName;
    @Value("${application.port:0}")
    int port;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    TemplateService templateService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DropService dropService;

    @Transactional
    public ScheduleInfo create(ScheduleInfo schedule) {
        Drop dropEntity = resolveDrop(schedule);
        if (dropEntity == null) {
            LOGGER.warn("Drop " + schedule.getDrop() + " not found in the database");
            return null;
        }
        Schedule scheduleEntity = scheduleMapper.map(schedule, dropEntity);
        scheduleEntity = scheduleRepository.save(scheduleEntity);
        addReviewers(scheduleEntity, schedule.getReviewers());

        return scheduleMapper.map(scheduleEntity);
    }

    private Drop resolveDrop(ScheduleInfo schedule) {
        DropInfo dropDto = schedule.getDrop();
        String productName = dropDto == null ? KgbConstant.PRODUCT_NAME.value() : dropDto.getProductName();
        String dropName = dropDto == null ? KgbConstant.DROP_NAME.value() : dropDto.getName();
        return dropRepository.findByProductAndDropNames(productName, dropName);
    }

    private Drop getKgbDrop() {
        return dropRepository.findByProductAndDropNames(KgbConstant.PRODUCT_NAME.value(), KgbConstant.DROP_NAME.value());
    }

    @Transactional
    public ScheduleInfo update(ScheduleInfo schedule, Long scheduleId) {
        Schedule originalSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(scheduleId);
        if (originalSchedule == null) {
            return null;
        }

        //TODO: difference with updateApproval only with newVersion create.
        Schedule updated = originalSchedule.createNewVersion();
        scheduleMapper.map(schedule, updated);
        updated = scheduleRepository.save(updated);
        addReviewers(updated, schedule.getReviewers());

        // save original as well
        scheduleRepository.save(originalSchedule);

        return scheduleMapper.map(updated);
    }

    @Transactional
    public ScheduleInfo updateApprovalStatus(ScheduleInfo schedule, Long scheduleId) {
        Schedule originalSchedule = scheduleRepository.findLastVersionByOriginalScheduleId(scheduleId);
        if (originalSchedule == null) {
            return null;
        }

        scheduleMapper.map(schedule, originalSchedule);
        scheduleRepository.save(originalSchedule);
        addReviewers(originalSchedule, schedule.getReviewers());

        return scheduleMapper.map(originalSchedule);
    }

    public Optional<ScheduleInfo> getSchedule(Long scheduleId) {
        Schedule scheduleEntity = scheduleRepository.findLastVersionByOriginalScheduleId(scheduleId);
        return getDto(scheduleEntity);
    }

    public Optional<ScheduleInfo> updateScheduleContent(Long scheduleId, ScheduleInfo scheduleInfo) {
        Schedule scheduleEntity = scheduleRepository.findLastVersionByOriginalScheduleId(scheduleId);
        if (scheduleEntity == null) {
            return Optional.empty();
        }

        boolean equals = new EqualsBuilder()
                .append(scheduleInfo.getXmlContent(), scheduleEntity.getXml())
                .append(scheduleInfo.getName(), scheduleEntity.getName())
                .append(scheduleInfo.getType().getId(), scheduleEntity.getType())
                .append(scheduleInfo.getTeam(), scheduleEntity.getTeam())
                .append(scheduleInfo.isValid(), scheduleEntity.isValid())
                .isEquals();

        boolean newReviewersAdded = checkIfNewReviewersAdded(scheduleInfo.getReviewers(), scheduleEntity.getReviewers());

        if (equals && !newReviewersAdded) {
            return Optional.of(scheduleMapper.map(scheduleEntity));
        } else if (equals) {
            //TODO: refactor update approval status
            ScheduleInfo updated = updateApprovalStatus(scheduleInfo, scheduleId);
            return Optional.of(updated);
        } else {
            //TODO: refactor update
            ScheduleInfo updated = update(scheduleInfo, scheduleId);
            return Optional.of(updated);
        }
    }

    public List<String> getAllTeams() {
        return scheduleRepository.findAllTeams();
    }

    @Transactional
    public void deleteAllVersions(Long originalScheduleId) {
        List<Schedule> allVersions = scheduleRepository.findAllVersions(originalScheduleId);
        allVersions.forEach(Schedule::delete);
        scheduleRepository.save(allVersions);
    }

    public Optional<ScheduleInfo> getScheduleVersion(Long scheduleId, Integer version) {
        Schedule entity = scheduleRepository.findVersion(scheduleId, version);
        return getDto(entity);
    }

    public List<SimpleScheduleInfo> getScheduleSummariesByIds(List<Long> ids) {
        List<Schedule> schedules = scheduleRepository.findApprovedSchedulesByIds(ids);
        return scheduleMapper.mapSummary(schedules);
    }

    public List<ScheduleInfo> getScheduleVersions(Long scheduleId) {
        List<Schedule> schedules = scheduleRepository.findAllVersions(scheduleId);
        return scheduleMapper.map(schedules);
    }

    public List<ScheduleInfo> getLatestSchedulesByDrop(Long dropId) {
        Drop drop = dropRepository.findOne(dropId);
        List<Schedule> schedules = scheduleRepository.findLatestVersionsByDrop(drop);
        return scheduleMapper.map(schedules);
    }

    public List<ScheduleInfo> getLatestKgbSchedules() {
        Drop kgbDrop = getKgbDrop();
        List<Schedule> kgbSchedules = scheduleRepository.findLatestVersionsByDrop(kgbDrop);
        return scheduleMapper.map(kgbSchedules);
    }

    public List<SimpleScheduleInfo> getApprovedSchedulesByDrop(String productName, String dropName) {
        Drop drop = dropRepository.findByProductAndDropNames(productName, dropName);

        if (drop == null) {
            drop = searchForDropInCiPortal(productName, dropName);
        }
        List<Schedule> schedules = findApprovedSchedulesByDrop(drop);
        return scheduleMapper.mapSummary(schedules);
    }

    public List<SimpleScheduleInfo> getApprovedSchedulesByDropAndType(String productName, String dropName, String type) {
        Drop drop = dropRepository.findByProductAndDropNames(productName, dropName);

        if (drop == null) {
            drop = searchForDropInCiPortal(productName, dropName);
        }
        Integer buildType = BuildType.getIdByType(type);
        List<Schedule> schedules = findApprovedSchedulesByDropAndType(drop, buildType);
        return scheduleMapper.mapSummary(schedules);
    }

    public List<SimpleScheduleInfo> getApprovedKgbSummaries(Optional<String> maybeTeam) {
        List<Schedule> schedules;
        Drop kgbDrop = getKgbDrop();
        if (maybeTeam.isPresent()) {
            schedules = scheduleRepository.findApprovedSchedulesByDropAndTeam(kgbDrop, maybeTeam.get());
        } else {
            schedules = scheduleRepository.findApprovedSchedulesByDrop(kgbDrop);
        }
        return scheduleMapper.mapSummary(schedules);
    }

    public String getScheduleXml(String dropName, String scheduleName, String scheduleVersion) {
        if (scheduleVersion == null) {
            return scheduleRepository.findLatestScheduleByName(dropName, scheduleName).getXml();
        }
        return scheduleRepository.findScheduleByNameAndVersion(
                dropName, scheduleName, Integer.parseInt(scheduleVersion)).getXml();
    }

    @Transactional
    public ScheduleInfo approveOrReject(Long scheduleId, ScheduleInfo schedule) {
        Schedule entity = scheduleRepository.findVersion(scheduleId, schedule.getVersion());
        if (ApprovalStatus.APPROVED.name().equalsIgnoreCase(schedule.getApprovalStatus())) {
            entity.approve(schedule.getApprovalMsg());
        } else if (ApprovalStatus.REJECTED.name().equalsIgnoreCase(schedule.getApprovalStatus())) {
            entity.reject(schedule.getApprovalMsg());
        }
        if (!Strings.isNullOrEmpty(schedule.getApprovalMsg())) {
            entity.addComment(schedule.getApprovalMsg());
        }
        scheduleRepository.save(entity);
        return scheduleMapper.map(entity);
    }

    @Transactional
    public ScheduleInfo revokeApproval(Long scheduleId, Integer version) {
        Schedule entity = scheduleRepository.findVersion(scheduleId, version);
        entity.revokeApproval();
        scheduleRepository.save(entity);
        return scheduleMapper.map(entity);
    }

    public boolean checkIfNewReviewersAdded(Set<UserInfo> newReviewers, Set<User> originalUsers) {
        if (newReviewers.isEmpty()) {
            return false;
        }
        Set<UserInfo> originalUsersDto = userMapper.map(Lists.newArrayList(originalUsers));
        return !originalUsersDto.containsAll(newReviewers);
    }

    private void addReviewers(Schedule schedule, Set<UserInfo> reviewers) {
        if (reviewers != null) {
            for (UserInfo reviewer : reviewers) {
                UserInfo newReviewer = checkIfReviewerExists(reviewer, schedule.getReviewers());
                if (newReviewer != null) {
                    addReviewer(schedule.getId(), schedule.getVersion(), reviewer);
                }
            }
        }
    }

    private static UserInfo checkIfReviewerExists(UserInfo reviewer, Set<User> existingReviewers) {
        String externalId = StringUtils.defaultIfEmpty(reviewer.getUserId(), reviewer.getEmail());
        for (User user : existingReviewers) {
            if ((user.getExternalId().equals(externalId)) || (user.getEmail().equals(externalId))) {
                return null;
            }
        }
        return reviewer;
    }

    private List<Schedule> findApprovedSchedulesByDrop(Drop drop) {
        return scheduleRepository.findApprovedSchedulesByDrop(drop, ApprovalStatus.APPROVED);
    }

    private List<Schedule> findApprovedSchedulesByDropAndType(Drop drop, Integer buildType) {
        return scheduleRepository.findApprovedSchedulesByDropAndType(drop, buildType, ApprovalStatus.APPROVED);
    }

    private Optional<ScheduleInfo> getDto(Schedule entity) {
        if (entity != null) {
            ScheduleInfo scheduleDto = scheduleMapper.map(entity);
            return Optional.of(scheduleDto);
        } else {
            return Optional.empty();
        }
    }

    public Optional<UserInfo> addReviewer(Long scheduleId, Integer version, UserInfo userInfo) {
        Schedule schedule = scheduleRepository.findVersion(scheduleId, version);
        if (schedule != null) {
            User user = userService.findOrCreateUser(userInfo);
            if (user != null) {
                schedule.getReviewers().add(user);
                scheduleRepository.save(schedule);
                notifyReviewers(Lists.newArrayList(user), schedule, TemplateService.NEW_ASSIGNMENT);
                return Optional.of(userMapper.map(user));
            }
        }
        return Optional.empty();
    }

    public Optional<UserInfo> deleteReviewer(Long scheduleId, Integer version, String userId) {
        Schedule schedule = scheduleRepository.findVersion(scheduleId, version);
        if (schedule != null) {
            Optional<User> user = userService.findByExternalIdOrEmail(userId);
            if (user.isPresent()) {
                schedule.getReviewers().remove(user.get());
                scheduleRepository.save(schedule);
                return Optional.of(userMapper.map(user.get()));
            }
        }
        return Optional.empty();
    }

    public Set<UserInfo> getReviewers(Long scheduleId, Integer version) {
        Schedule schedule = scheduleRepository.findVersion(scheduleId, version);
        if (schedule != null) {
            return scheduleMapper.map(schedule).getReviewers();
        }
        return Collections.emptySet();
    }

    private void notifyReviewers(List<User> reviewers, Schedule schedule, String templateName) {
        String host = hostName;
        if (port != 0) {
            host = host + ":" + port;
        }
        String url = format("http://%s/#/schedules/%s/review?version=%s",
                host, schedule.getOriginalId(), schedule.getVersion());
        String createdBy = findUser(schedule.getCreatedBy());
        String updatedBy = findUser(schedule.getUpdatedBy());
        String owner = updatedBy != null ? updatedBy : createdBy;
        String scheduleType = BuildType.getTypeById(schedule.getType());
        Drop drop = schedule.getDrop();
        List<String> recipients = reviewers.stream().map(User::getEmail).collect(toList());

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("url", url);
        variables.put("owner", owner);
        variables.put("createdBy", createdBy);
        variables.put("updatedBy", updatedBy);
        variables.put("recipient", reviewers.get(0));
        variables.put("schedule", schedule);
        variables.put("type", scheduleType);
        variables.put("drop", drop);

        String text = templateService.generateFromTemplate(templateName, variables);
        String subject = format("Change in %s / %s / %s",
                drop.getProduct().getName(),
                drop.getName(),
                schedule.getName());

        emailService.sendEmail(recipients, subject, text);
    }

    private String findUser(String externalId) {
        Optional<User> user = userService.findByExternalIdOrEmail(externalId);
        if (user.isPresent()) {
            return user.get().toString();
        } else {
            return externalId;
        }
    }

    private Drop searchForDropInCiPortal(String productName, String dropName) {
        dropService.getDrops(productName);
        return dropRepository.findByProductAndDropNames(productName, dropName);
    }

    public List<CommentInfo> getComments(long scheduleId) {
        Optional<List<Schedule>> maybeSchedules = Optional.of(scheduleRepository.findAllVersions(scheduleId));
        if (maybeSchedules.isPresent()) {
            return maybeSchedules.get().stream()
                    .map(Schedule::getComments)
                    .flatMap(Collection::stream)
                    .sorted((c1, c2) -> c2.getId().compareTo(c1.getId()))
                    .map(commentMapper::map)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<CommentInfo> addComment(long scheduleId, int version, String message) {
        Schedule schedule = scheduleRepository.findVersion(scheduleId, version);
        if (schedule != null) {
            schedule.addComment(message);
            schedule = scheduleRepository.save(schedule);
            List<Comment> comments = schedule.getComments();
            if (!comments.isEmpty()) {
                return Optional.of(commentMapper.map(comments.get(comments.size() - 1)));
            }
        }
        return Optional.empty();
    }
}
