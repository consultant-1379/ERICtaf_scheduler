package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import org.springframework.stereotype.Service;

@Service
public class DropMapper {

    public DropInfo map(Drop entity) {
        DropInfo dto = new DropInfo();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setProductName(entity.getProduct().getName());
        return dto;
    }
}
