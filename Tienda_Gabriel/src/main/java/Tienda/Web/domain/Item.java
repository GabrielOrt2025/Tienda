/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.Web.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Item extends Producto {
   private int cantidad; //Almacenar la cantidad de items de un producto
   
   public Item() {
   }
   
   public Item(Producto producto) {
       super.setIdProducto(idProducto:producto.getIdProducto());
       super.setCategoria(categoria:producto.getCategoria());
       super.setDescripcion(descripcion:producto.getDescripcion());
       super.setDetalle(detalle:producto.getDetalle());
       super.setPrecio(precio:producto.getPrecio());
       super.setExistencias(existencias:producto.getExistencias());
       super.setActivo(activo:producto.isActivo());
       super.setRutaImagen(rutaImagen:producto.getRutaImagen());
       this.cantidad = 0;
   }
}
