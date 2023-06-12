package com.flab.fkreambatch.Dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class DealDto {

    private Long id;
    private Long itemId;
    private int price;
    private String size;
    private LocalDate tradingDay;
}
