package io.security.corespringsecurity.security.excpetion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SimpleResponse {

    private LocalDateTime successTime;

    private String message;
}
