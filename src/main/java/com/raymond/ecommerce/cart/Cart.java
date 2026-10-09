package com.raymond.ecommerce.cart;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Cart() {
    }
    public Long getId() {
        return id;
    }

    @OneToMany(mappedBy = "cart")
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }
}