/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package Tienda.Web.service;

import jakarta.mail.MessagingException; //Import que ayuda a que si hay un error al enviar el correo tire un exception 

public interface CorreoService {
    
    //mmetodos para enviar un correo en los que se implementa la logica de los mismos en correoServiceImpl
    public void enviarCorreoHtml(
            String para, 
            String asunto, 
            String contenidoHtml) 
            throws MessagingException;
}

