package com.pruebadev.franquicias.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sucursal")
public class Sucursal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franquicia_id", nullable = false)
    private Franquicia franquicia;

    protected Sucursal() {
    }

    public Sucursal(String nombre, Franquicia franquicia) {
        this.nombre = nombre;
        this.franquicia = franquicia;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Franquicia getFranquicia() {
        return franquicia;
    }
}
