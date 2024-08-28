package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.application.repository.ScheduleRepository;
import com.ericsson.cifwk.taf.scheduler.constant.BuildType;
import com.ericsson.cifwk.taf.scheduler.constant.ApprovalStatus;
import com.ericsson.cifwk.taf.scheduler.api.dto.ScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.SimpleScheduleInfo;
import com.ericsson.cifwk.taf.scheduler.api.dto.TypeInfo;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ScheduleMapper {

    private static String urlToSchedule = "{0}/api/schedules/{1}/versions/{2}";

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    IsoMapper isoMapper;

    @Autowired
    DropMapper dropMapper;

    @Autowired
    UserMapper userMapper;

    @Value("${application.hostname:localhost}")
    String hostName;

    @Value("${application.port:0}")
    int port;

    public Schedule map(ScheduleInfo dto, Drop drop) {
        return new Schedule(dto.getName(), dto.getType().getId(), dto.getXmlContent(), drop, dto.getApprovalStatus(), dto.getTeam(), dto.isValid());
    }

    public Schedule map(ScheduleInfo dto, Schedule schedule) {
        schedule.setName(dto.getName());
        schedule.setType(dto.getType().getId());
        schedule.setXml(dto.getXmlContent());
        schedule.setApprovalStatus(ApprovalStatus.valueOf(dto.getApprovalStatus()));
        schedule.setTeam(dto.getTeam());
        schedule.setValidStatus(dto.isValid());
        return schedule;
    }

    public ScheduleInfo map(Schedule entity) {
        ScheduleInfo dto = new ScheduleInfo();
        dto.setId(entity.getOriginalId());
        dto.setName(entity.getName());
        dto.setType(mapType(entity.getType()));
        dto.setVersion(entity.getVersion());
        dto.setIsLastVersion(entity.isLastVersion());
        setVersionList(dto);
        dto.setXmlContent(entity.getXml());
        dto.setCreated(entity.getCreated());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdated(entity.getUpdated());
        dto.setReviewers(userMapper.map(new ArrayList<>(entity.getReviewers())));
        dto.setDrop(dropMapper.map(entity.getDrop()));
        dto.setTeam(entity.getTeam());
        dto.setValid(entity.isValid());
        mapApprovalData(dto, entity);
        return dto;
    }

    private static ScheduleInfo mapApprovalData(ScheduleInfo dto, Schedule entity) {
        ApprovalStatus approvalStatus = entity.getApprovalStatus();
        dto.setApprovalStatus(entity.getApprovalStatus().name());

        if (approvalStatus.name().equalsIgnoreCase(ApprovalStatus.APPROVED.name()) ||
                approvalStatus.name().equalsIgnoreCase(ApprovalStatus.REJECTED.name())) {
            dto.setApprovedBy(entity.getApprovedBy());
            dto.setApprovalMsg(entity.getApprovalMsg());
        }
        return dto;
    }

    public List<ScheduleInfo> map(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::map)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public SimpleScheduleInfo mapSummary(Schedule entity) {
        SimpleScheduleInfo summary = new SimpleScheduleInfo();
        summary.setId(entity.getId());
        summary.setName(entity.getName());
        summary.setVersion(entity.getVersion());
        summary.setType(mapType(entity.getType()));
        summary.setCreatedBy(entity.getCreatedBy());
        summary.setXml(entity.getXml());
        summary.setUrl(getScheduleUrl(entity.getOriginalId(), entity.getVersion()));
        summary.setLastVersion(entity.isLastVersion());
        return summary;
    }

    public List<SimpleScheduleInfo> mapSummary(List<Schedule> schedules) {
        return schedules.stream()
                .map(this::mapSummary)
                .collect(toList());
    }

    protected String getScheduleUrl(Long id, Integer version) {
        String host;
        if (port != 0) {
            host = String.format("%s:%d", hostName, port);
        } else {
            host = hostName;
        }
        return MessageFormat.format(urlToSchedule, host, id, version);
    }

    public TypeInfo mapType(int entityId) {
        return new TypeInfo(entityId, BuildType.getTypeById(entityId));
    }

    private void setVersionList(ScheduleInfo dto) {
        List<Integer> allVersions = extractVersionNumbers(scheduleRepository.findAllVersions(dto.getId()));
        dto.setVersionList(allVersions);
    }

    private static List<Integer> extractVersionNumbers(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> s.getVersion())
                .collect(toList());
    }

}
