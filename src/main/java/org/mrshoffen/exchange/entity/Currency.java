package org.mrshoffen.exchange.entity;

import lombok.*;

@Data
@Builder
public class Currency {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;

}
