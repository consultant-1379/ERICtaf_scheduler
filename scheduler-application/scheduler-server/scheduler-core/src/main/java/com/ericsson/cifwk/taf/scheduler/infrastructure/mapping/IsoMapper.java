package com.ericsson.cifwk.taf.scheduler.infrastructure.mapping;

import com.ericsson.cifwk.taf.scheduler.api.dto.IsoInfo;
import com.ericsson.cifwk.taf.scheduler.model.ISO;
import org.springframework.stereotype.Service;

/**
 * Created by evlailj on 15/06/2015.
 */
@Service
public class IsoMapper {

    public IsoInfo map(ISO entity) {
        IsoInfo dto = new IsoInfo();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVersion(entity.getVersion());
        return dto;
    }
}
