/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package Tienda.Web.service;

import Tienda.Web.domain.Usuario;
import jakarta.mail.MessagingException;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public interface RegistroService {
    
    //Definición de metodos sin logica, la logica va en el ServiceImpl

    public Model activar(Model model, String usuario, String clave); //Contiene el objeto model y guarda los strings de usuario y clave para mostrarlos en thymeleaf

    public Model crearUsuario(Model model, Usuario usuario) throws MessagingException; //Model que crea un usuario, guarda los datos del objeto usuario para la vista thymeleaf
    
    public void activar(Usuario usuario, MultipartFile imagenFile); //metodo que activa un usuario el cual tiene los datos del objeto usuario y sube una imagen con firebase
    
    public Model recordarUsuario(Model model, Usuario usuario) throws MessagingException; //Model para recordar al usuaruo que utiliza el objeto usuario con los datos del mismo y mostrarlos en thymeleaf
}
