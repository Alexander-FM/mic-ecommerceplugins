package com.codecorecix.ecommerce.order.info.api.dto.request;

import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    private Integer newStatusId;
}