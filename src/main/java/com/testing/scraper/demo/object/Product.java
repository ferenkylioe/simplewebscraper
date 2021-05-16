package com.testing.scraper.demo.object;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product {
    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private String description;

    @CsvBindByPosition(position = 2)
    private String imageLink;

    @CsvBindByPosition(position = 3)
    private String price;

    @CsvBindByPosition(position = 4)
    private String rating;

    @CsvBindByPosition(position = 5)
    private String storeName;
}
