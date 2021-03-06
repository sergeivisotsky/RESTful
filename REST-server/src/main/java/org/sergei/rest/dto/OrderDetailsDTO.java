/*
 * Copyright 2018-2019 the original author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sergei.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Sergei Visotsky
 */
@ApiModel(value = "OrderDetails", description = "All oder details data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("orderDetails")
public class OrderDetailsDTO {

    @JsonIgnore
    private Long orderId;

    @ApiModelProperty("Product code which was orders")
    private String productCode;

    @ApiModelProperty("Quantity of the product ordered")
    private Integer quantityOrdered;

    @ApiModelProperty("Order price")
    private BigDecimal price;
}
