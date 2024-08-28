package com.ericsson.cifwk.taf.scheduler.presentation.controllers;

import com.ericsson.cifwk.taf.scheduler.application.services.DropService;
import com.ericsson.cifwk.taf.scheduler.api.dto.DropInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private DropService dropService;

    @RequestMapping(value = "/{productName}/drops", method = RequestMethod.GET)
    public ResponseEntity<List<DropInfo>> drops(@PathVariable String productName) {
        List<DropInfo> drops = dropService.getDrops(productName);
        return new ResponseEntity<>(drops, HttpStatus.OK);
    }

}
