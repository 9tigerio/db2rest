package com.homihq.db2rest.rest.read.v2.processor;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Order(4)
public class JoinProcessor implements ReadPreProcessor{
    @Override
    public void process(ReadContextV2 readContextV2) {
        if(Objects.nonNull(readContextV2.getJoins()) && !readContextV2.getJoins().isEmpty()) {
            for(String join : readContextV2.getJoins()) {
                log.info("Join - {}", join);

            }
        }
    }
}
