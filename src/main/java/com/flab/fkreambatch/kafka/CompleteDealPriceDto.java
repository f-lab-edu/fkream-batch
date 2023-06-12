package com.flab.fkreambatch.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteDealPriceDto {

    private Long itemId;
    private int price;

}
