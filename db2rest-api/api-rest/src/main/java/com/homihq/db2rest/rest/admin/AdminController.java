package com.homihq.db2rest.rest.admin;

import com.homihq.db2rest.jdbc.JdbcManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final JdbcManager jdbcManager;


    @PostMapping("/reloadCache")
    public void reloadCache(){
        log.info("Reload cache request received.");
        this.jdbcManager.reload();
    }

}
