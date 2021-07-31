package com.sadadream.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.github.dozermapper.core.Mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductData {
    @NotBlank
    @Mapping("brand")
    private String brand;

    @NotBlank
    @Mapping("name")
    private String name;

    @NotNull
    @Mapping("price")
    private String price;

    @NotNull
    @Mapping("currency")
    private String currency;

    @Mapping("imageLink")
    private List<String> imageLink;

    @Mapping("description")
    private String description;

    @Mapping("category")
    private String category;
}
