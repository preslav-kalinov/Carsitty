package com.kalinov.carsitty.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "sales")
public class Sale implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "partId")
    private Part part;

    @Column(name = "soldQuantity")
    private Long soldQuantity;

    @Column(name = "saleProfit")
    private BigDecimal saleProfit;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "saleDate")
    private Date saleDate;

    public Sale() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Long getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Long soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public BigDecimal getSaleProfit() {
        return saleProfit;
    }

    public void setSaleProfit(BigDecimal saleProfit) {
        this.saleProfit = saleProfit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
}