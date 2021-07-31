package com.sadadream.domain;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;

    private String name;

    private String price;

    private String currency;

    @ElementCollection
    private List<String> imageLink;

    private String description;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @Setter
    private User user;
    public void changeWith(Product source) {
        this.brand = source.brand;
        this.name = source.name;
        this.price = source.price;
        this.currency = source.currency;
        this.imageLink = source.imageLink;
        this.description = source.description;
        this.category = source.category;
    }
}
