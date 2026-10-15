package com.indra.retail.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido. La tabla se llama "orders" porque ORDER es palabra reservada en SQL.
 *
 * MODELO INICIAL CON DEFECTOS A PROPÓSITO (ver enunciado.md):
 * TODO(participante):
 *  - customer: pasar a LAZY y quitar el cascade (un pedido no gestiona al cliente).
 *  - items: cascade = {PERSIST, MERGE} en vez de ALL, y orphanRemoval = true.
 *  - status: reemplazar el String por un enum OrderStatus (PENDING, CONFIRMED, SHIPPED)
 *    mapeado con @Enumerated(EnumType.STRING).
 *  - status y createdAt: obligatorios.
 *  - customer_id obligatorio (agregar @JoinColumn(name = "customer_id", nullable = false)).
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
      
    }

    public Order(Customer customer) {
        this.customer = customer;
    }

    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
