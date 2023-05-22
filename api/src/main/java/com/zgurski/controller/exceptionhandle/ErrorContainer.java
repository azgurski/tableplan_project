package com.zgurski.controller.exceptionhandle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ErrorContainer {

    private String errorId;

    private Integer errorCode;

    private String errorMessage;

    private String errorClass;
}
