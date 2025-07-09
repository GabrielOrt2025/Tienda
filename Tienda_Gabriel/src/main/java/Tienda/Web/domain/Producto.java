/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.Web.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data​
@Entity​
@Table(name="producto")
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id​
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_producto")
    private Long idProducto;    
    private String descripcion;
    private String detalle;
    private double precio;
    private int existencias;
    private String rutaImagen;
    private boolean activo;

    @ManyToOne​
    @JoinColumn(name="id_categoria")
    Categoria categoria;

    public Producto() {    
    }


    public Producto(String descripcion, String detalle, double precio, int existencias, String imagen, boolean activo) {
        this.descripcion = descripcion;
        this.detalle = detalle;
        this.precio = precio;
        this.existencias = existencias;
        this.rutaImagen = imagen;
        this.activo = activo;
    }
}
