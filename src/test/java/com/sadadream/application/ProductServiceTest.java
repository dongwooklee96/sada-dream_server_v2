package com.sadadream.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sadadream.domain.Product;
import com.sadadream.domain.ProductRepository;
import com.sadadream.domain.User;
import com.sadadream.domain.UserRepository;
import com.sadadream.dto.ProductData;
import com.sadadream.errors.ProductNotFoundException;

class ProductServiceTest {
    private ProductService productService;

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();

        productService = new ProductService(mapper, productRepository, userRepository);

        Product product = Product.builder()
                .id(1L)
                .name("나이키 조던")
                .price("1000")
                .brand("나이키")
                .currency("KRW")
                .category("신발")
                .build();

        given(productRepository.findAll()).willReturn(List.of(product));

        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        given(productRepository.save(any(Product.class))).will(invocation -> {
            Product source = invocation.getArgument(0);
            return Product.builder()
                    .id(2L)
                    .name(source.getName())
                    .price(source.getPrice())
                    .brand("나이키")
                    .currency("KRW")
                    .category("신발")
                    .build();
        });

        given(userRepository.findById(1L)).willReturn(Optional.of(
            User.builder()
                .id(1L)
                .email("tester@example.com")
                .name("tester")
                .password("valid_password")
                .build()));
    }

    @DisplayName("상품이 없는 상태에서 모든 상품 목록을 조회하면, 빈 리스트가 반환된다.")
    @Test
    void getProductsWithNoProduct() {
        given(productRepository.findAll()).willReturn(List.of());

        Assertions.assertThat(productService.getProducts()).isEmpty();
    }

    @DisplayName("특정 상품을 조회하였을 때 상품이 반환된다.")
    @Test
    void getProducts() {
        List<Product> products = productService.getProducts();

        assertThat(products).isNotEmpty();

        Product product = products.get(0);

        assertThat(product.getName()).isEqualTo("나이키 조던");
        assertThat(product.getBrand()).isEqualTo("나이키");
        assertThat(product.getCategory()).isEqualTo("신발");
    }

    @DisplayName("존재하는 상품 아이디로 상품을 조회하면 해당 상품이 반환된다.")
    @Test
    void getProductWithExistedId() {
        Product product = productService.getProduct(1L);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("나이키 조던");
        assertThat(product.getBrand()).isEqualTo("나이키");
        assertThat(product.getCategory()).isEqualTo("신발");
    }

    @DisplayName("존재하지 않는 상품 아이디로 상품을 조회하면 예외가 발생한다.")
    @Test
    void getProductWithNotExistedId() {
        assertThatThrownBy(() -> productService.getProduct(1000L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @DisplayName("상품을 생성하면, 해당 상품이 정상적으로 생성된다.")
    @Test
    void createProduct() {
        ProductData productData = ProductData.builder()
            .brand("나이키")
            .currency("KRW")
            .category("신발")
            .name("나이키 조던")
            .build();

        User user = User.builder()
            .id(1L)
            .build();

        Product product = productService.createProduct(productData, user.getId());

        verify(productRepository).save(any(Product.class));

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getCurrency()).isEqualTo("KRW");
        assertThat(product.getName()).isEqualTo("나이키 조던");
    }

    @DisplayName("존재하는 상품 아이디로, 업데이트를 하면 상품 정보가 갱신된다.")
    @Test
    void updateProductWithExistedId() {
        ProductData productData = ProductData.builder()
            .brand("아디다스")
            .currency("KRW")
            .category("신발")
            .name("슈팅스타")
            .build();

        Product product = productService.updateProduct(1L, productData);

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getCurrency()).isEqualTo("KRW");
        assertThat(product.getCategory()).isEqualTo("신발");
        assertThat(product.getName()).isEqualTo("슈팅스타");
    }

    @DisplayName("존재하지 않는 상품 아이디로, 상품 정보를 갱신하면, 예외가 발생한다.")
    @Test
    void updateProductWithNotExistedId() {
        ProductData productData = ProductData.builder()
            .brand("나이키")
            .currency("KRW")
            .category("신발")
            .name("나이키 조던")
            .build();

        assertThatThrownBy(() -> productService.updateProduct(1000L, productData))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @DisplayName("존재하는 상품을 삭제를 시도하면, 정상적으로 삭제가 된다.")
    @Test
    void deleteProductWithExistedId() {
        productService.deleteProduct(1L);

        verify(productRepository).delete(any(Product.class));
    }

    @DisplayName("존재하지 않는 상품 삭제를 시도하면 예외가 발생한다.")
    @Test
    void deleteProductWithNotExistedId() {
        assertThatThrownBy(() -> productService.deleteProduct(1000L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
