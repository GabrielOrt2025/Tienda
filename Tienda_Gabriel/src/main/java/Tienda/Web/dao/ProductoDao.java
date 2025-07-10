/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.Web.dao;

import Tienda.Web.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoDao extends JpaRepository <Producto, Long> {
    
    public List<Producto> findByPrecioBetweenOrderByDescripcion(double precioInf, double precioSup);
    
    @Query (value= "SELECT a FROM Producto a where a.precio BETWEEN :precioInfo AND :precioSup ORDER BY a.descripcion ASC")
    public List <Producto> metodoJPQL (@Param ("precioInf") double precioInf, @Param("precioSup") double precioSup);
    
    @Query(nativeQuery=true,
            value="SELECT * FROM producto where producto.precio BETWEEN :precioInf AND :precioSup ORDER BY producto.descripcion ASC")
    public List<Producto> metodoNativo (@Param("precioInf") double precioInf, @Param("precioSup") double precioSup);
    
}
