package com.ericsson.cifwk.taf.scheduler.application.services;

import com.ericsson.cifwk.taf.scheduler.application.repository.DropRepository;
import com.ericsson.cifwk.taf.scheduler.application.repository.ProductRepository;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import com.ericsson.cifwk.taf.scheduler.integration.ciportal.CiPortalClient;
import com.ericsson.cifwk.taf.scheduler.model.Drop;
import com.ericsson.cifwk.taf.scheduler.model.Product;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class DropService {

    @Autowired
    private CiPortalClient ciPortalClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DropRepository dropRepository;

    @Autowired
    private SchedulePromotionService schedulePromotionService;

    @Transactional
    public List<DropInfo> getDrops(String productName) {
        List<DropInfo> remoteDrops = ciPortalClient.getDrops(productName);

        Product product = productRepository.findByName(productName);

        Set<DropInfo> dropsInProduct = product.getDrops()
                .stream()
                .map(d -> new DropInfo(d.getId(), product.getName(), d.getName()))
                .collect(toSet());

        DiffHelper<DropInfo> diffHelper = new DiffHelper<>(new HashSet<>(remoteDrops), dropsInProduct);
        List<Drop> newDrops = diffHelper.getOnlyInA().stream()
                .map(d -> new Drop(product, d.getName()))
                .collect(toList());

        dropRepository.save(newDrops);

        if (!newDrops.isEmpty()) {
            schedulePromotionService.promoteSchedulesByDrop(product.getName(), newDrops);
        }

        List<DropInfo> newDropDtosWithId = map(newDrops);
        dropsInProduct.addAll(newDropDtosWithId);

        List<DropInfo> dropInfos = Lists.newArrayList(dropsInProduct);
        dropInfos.sort(Comparator.comparing(DropInfo::getName).reversed());
        return dropInfos;
    }

    private List<DropInfo> map(List<Drop> drops) {
        return drops.stream()
                .map(d -> new DropInfo(d.getId(), d.getProduct().getName(), d.getName())).collect(toList());
    }
}
