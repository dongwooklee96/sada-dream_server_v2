package com.sadadream.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sadadream.application.AuthenticationService;
import com.sadadream.application.ProductService;
import com.sadadream.domain.Product;
import com.sadadream.domain.Role;
import com.sadadream.dto.ProductData;
import com.sadadream.errors.InvalidTokenException;
import com.sadadream.errors.ProductNotFoundException;

@WebMvcTest(ProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {

        Product product = Product.builder()
                .id(1L)
                .brand("아디다스")
                .category("신발")
                .description("신발입니다.")
                .currency("KRW")
                .name("슈팅스타")
                .price("50000")
                .build();

        given(productService.getProducts()).willReturn(List.of(product));

        given(productService.getProduct(1L)).willReturn(product);

        given(productService.getProduct(1000L))
                .willThrow(new ProductNotFoundException(1000L));

        given(productService.createProduct(any(ProductData.class), any(Long.class)))
                .willReturn(product);

        given(productService.updateProduct(eq(1L), any(ProductData.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                    ProductData productData = invocation.getArgument(1);
                    return Product.builder()
                            .id(id)
                            .name(productData.getName())
                            .brand(productData.getBrand())
                            .price(productData.getPrice())
                            .currency(productData.getCurrency())
                            .imageLink(productData.getImageLink())
                            .description(productData.getDescription())
                            .category(productData.getCategory())
                            .build();
                });

        given(productService.updateProduct(eq(1000L), any(ProductData.class)))
                .willThrow(new ProductNotFoundException(1000L));

        given(productService.deleteProduct(1000L))
                .willThrow(new ProductNotFoundException(1000L));

        given(authenticationService.parseToken(VALID_TOKEN)).willReturn(1L);

        given(authenticationService.parseToken(INVALID_TOKEN))
                .willThrow(new InvalidTokenException(INVALID_TOKEN));

        given(authenticationService.roles(1L))
            .willReturn(Arrays.asList(new Role("USER")));
    }

    @DisplayName("상품 리스트를 조회하였을 때 정상적으로 조회가 이루어진다.")
    @Test
    void list() throws Exception {
        mockMvc.perform(
            get("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().string(
                containsString("\"brand\":\"아디다스\"")
            ))
            .andExpect(content().string(
                containsString("\"name\":\"슈팅스타\"")
            ))
            .andExpect(content().string(
                containsString("\"currency\":\"KRW\"")
            ));
        verify(productService).getProducts();

    }

    @DisplayName("존재하는 상품의 상세조회를 하면 정상적으로 조회가 이루어진다.")
    @Test
    void detailWithExistedProduct() throws Exception {
        mockMvc.perform(
            get("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().string(
                containsString("\"brand\":\"아디다스\"")
            ))
            .andExpect(content().string(
                containsString("\"name\":\"슈팅스타\"")
            ))
            .andExpect(content().string(
                containsString("\"currency\":\"KRW\"")
            ));

        verify(productService).getProduct(any(Long.class));
    }

    @DisplayName("존재하지 않은 상품을 조회하면 존재하지 않음을 반환한다.")
    @Test
    void detailWithNotExistedProduct() throws Exception {
        mockMvc.perform(get("/products/1000"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("유효한 형식 및 토큰으로 상품 생성을 요청하면, 정상적으로 생성된다.")
    @Test
    void createWithValidAttributes() throws Exception {
        mockMvc.perform(
            post("/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isCreated())
            .andExpect(content().string(
                containsString("\"brand\":\"아디다스\"")
            ))
            .andExpect(content().string(
                containsString("\"name\":\"슈팅스타\"")
            ))
            .andExpect(content().string(
                containsString("\"currency\":\"KRW\"")
            ));

        verify(productService).createProduct(any(ProductData.class), any(Long.class));
    }

    @DisplayName("유효하지 않은 형식으로 상품 생성을 요청하면, 잘못된 요청을 반환한다.")
    @Test
    void createWithInvalidAttributes() throws Exception {
        mockMvc.perform(
            post("/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"\",\n"
                    + "  \"category\": \"\",\n"
                    + "  \"currency\": \"\",\n"
                    + "  \"description\": \"\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"\"\n"
                    + "  ],\n"
                    + "  \"name\": \"\",\n"
                    + "  \"price\": \"\"\n"
                    + "}")
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isBadRequest());
    }

    @DisplayName("액세스 토큰 없이 상품 생성을 요청하면, 생성되지 않는다.")
    @Test
    void createWithoutAccessToken() throws Exception {
        mockMvc.perform(
            post("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
        )
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("유효하지 않은 액세스 토큰으로, 상품 생성 요청을 하면 거부된다.")
    @Test
    void createWithWrongAccessToken() throws Exception {
        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
                .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("존재하는 상품에 대해서 업데이트 요청을 하면 정상적으로 수행된다.")
    @Test
    void updateWithExistedProduct() throws Exception {
        mockMvc.perform(
            patch("/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isOk())
            .andExpect(content().string(
                containsString("\"brand\":\"아디다스\"")
            ));

        verify(productService).updateProduct(eq(1L), any(ProductData.class));
    }

    @DisplayName("존재하지 않는 유저에게 수정요청을 하면 존재하지 않음을 반환한다.")
    @Test
    void updateWithNotExistedProduct() throws Exception {
        mockMvc.perform(
            patch("/products/1000")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(1000L), any(ProductData.class));
    }

    @DisplayName("유효하지 않은 형식으로 상품 수정을 요청하면 잘못된 요청을 반환한다.")
    @Test
    void updateWithInvalidAttributes() throws Exception {
        mockMvc.perform(
            patch("/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"\",\n"
                    + "  \"category\": \"\",\n"
                    + "  \"currency\": \"\",\n"
                    + "  \"description\": \"\",\n"
                    + "  \"name\": \"\",\n"
                    + "  \"price\": \"\"\n"
                    + "}")
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isBadRequest());
    }

    @DisplayName("액세스 토큰 없이 상품 수정을 요청하면 허가되지 않는다. (401)")
    @Test
    void updateWithoutAccessToken() throws Exception {
        mockMvc.perform(
            patch("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
        )
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("유효하지 않은 액세스 토큰으로 상품 수정을 요청하면 허가되지 않는다. (401)")
    @Test
    void updateWithInvalidAccessToken() throws Exception {
        mockMvc.perform(
            patch("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\n"
                    + "  \"brand\": \"아디다스\",\n"
                    + "  \"category\": \"신발\",\n"
                    + "  \"currency\": \"KRW\",\n"
                    + "  \"description\": \"신발입니다.\",\n"
                    + "  \"image_link\": [\n"
                    + "    \"https://aws.s3.abc.jpg\"\n"
                    + "  ],\n"
                    + "  \"name\": \"슈팅스타\",\n"
                    + "  \"price\": \"50000\"\n"
                    + "}")
                .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
            .andExpect(status().isUnauthorized());
    }

    @DisplayName("존재하는 상품을 삭제하였을 때 정상적으로 수행된다.")
    @Test
    void destroyWithExistedProduct() throws Exception {
        mockMvc.perform(
            delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @DisplayName("존재 하지 않는 상품을 삭제하면 존재 하지 않는다. (404)")
    @Test
    void destroyWithNotExistedProduct() throws Exception {
        mockMvc.perform(
            delete("/products/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + VALID_TOKEN)
        )
            .andExpect(status().isNotFound());

        verify(productService).deleteProduct(1000L);
    }

    @DisplayName("유효하지 않은 토큰으로 삭제 요청을 하면, 권한 없음(401) 을 반환한다.")
    @Test
    void destroyWithInvalidAccessToken() throws Exception {
        mockMvc.perform(
            delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
            .andExpect(status().isUnauthorized());
    }
}
