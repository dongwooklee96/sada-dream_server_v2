package com.sadadream.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

    private Product product;

    @BeforeEach
    public void setUp() {
        product = Product.builder()
            .id(1L)
            .brand("나이키")
            .name("에어맥스")
            .price("5000")
            .currency("KRW")
            .imageLink(List.of("https://abc.jpg", "https://def.jpg"))
            .description("나이키 신발입니다.")
            .category("신발")
            .build();
    }

    @DisplayName("상품을 생성하였을 때, 정상적으로 생성된다.")
    @Test
    void creationWithBuilder() {
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getBrand()).isEqualTo("나이키");
        assertThat(product.getName()).isEqualTo("에어맥스");
        assertThat(product.getPrice()).isEqualTo("5000");
        assertThat(product.getImageLink()).contains("https://abc.jpg", "https://def.jpg");
        assertThat(product.getDescription()).isEqualTo("나이키 신발입니다.");
        assertThat(product.getCategory()).isEqualTo("신발");
    }

    @DisplayName("상품을 수정하였을 때, 변경사항이 정상적으로 적용된다.")
    @Test
    void changeWith() {
        product.changeWith(Product.builder()
                .brand("아디다스")
                .name("슈팅스타")
                .price("109000")
                .currency("KRW")
                .imageLink(List.of("https://123.jpg", "https://456.jpg"))
                .description("아디다스 신발입니다.")
                .category("신발")
                .build());

        assertThat(product.getBrand()).isEqualTo("아디다스");
        assertThat(product.getName()).isEqualTo("슈팅스타");
        assertThat(product.getPrice()).isEqualTo("109000");
        assertThat(product.getImageLink()).isEqualTo(List.of("https://123.jpg", "https://456.jpg"));
        assertThat(product.getDescription()).isEqualTo("아디다스 신발입니다.");
        assertThat(product.getCategory()).isEqualTo("신발");
    }
}
