package org.sergei.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author Sergei Visotsky
 */
@Data
@AllArgsConstructor
public class ErrorDetailsDTO {
    private Date timestamp;
    private String message;
    private String details;
}
