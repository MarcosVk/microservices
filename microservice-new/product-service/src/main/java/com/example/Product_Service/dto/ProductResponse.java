package com.example.Product_Service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;

    private String name;
    private String description;
    private BigDecimal price;
}
