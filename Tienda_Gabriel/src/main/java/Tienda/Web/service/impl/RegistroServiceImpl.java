/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.Web.service.impl;

import Tienda.Web.domain.Usuario;
import Tienda.Web.service.CorreoService;
import Tienda.Web.service.RegistroService;
import Tienda.Web.service.UsuarioService;
import jakarta.mail.MessagingException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegistroServiceImpl implements RegistroService {

        //Los autowired se usan para inyectar las dependencias que va a ocupar esta clase, se estan inyectando los metodos definidos en las clases anteriores para poder darles la logica
    @Autowired
    private CorreoService correoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MessageSource messageSource;  //creado en semana 4...
    @Autowired
    private FireBaseStorageServiceImpl firebaseStorageService;

    @Override
    public Model activar(Model model, String username, String clave) {
        Usuario usuario = 
                usuarioService.getUsuarioPorUsernameYPassword(username, 
                        clave); //Se busca el usuario con los metodos de UsuarioService por el username y el password para poder validarlo
        if (usuario != null) {
            model.addAttribute("usuario", usuario); // si se encuentra al usuario se añade al modelo
        } else {
            model.addAttribute(
                    "titulo", 
                    messageSource.getMessage(
                            "registro.activar", 
                            null,  Locale.getDefault()));
            model.addAttribute(
                    "mensaje", 
                    messageSource.getMessage(
                            "registro.activar.error", 
                            null, Locale.getDefault()));
        }
        return model; //Retorna el modelo ya con los datos cargados
    }

    @Override
    public void activar(Usuario usuario, MultipartFile imagenFile) { //metodo void que contiene el objeto usuario y MultipartFile para subir una imagen a firebase
        var codigo = new BCryptPasswordEncoder(); //se encripta la clave de los usuarios
        usuario.setPassword(codigo.encode(usuario.getPassword()));

        if (!imagenFile.isEmpty()) {
            usuarioService.save(usuario, false);
            usuario.setRutaImagen(
                    firebaseStorageService.cargaImagen(
                            imagenFile, 
                            "usuarios", 
                            usuario.getIdUsuario()));
        }
        usuarioService.save(usuario, true); //Se guarda el usuario ya con la imagen subida en firebase
    }

    @Override
    public Model crearUsuario(Model model, Usuario usuario) //metodo crear usuario que contiene el model y el objeto usuario para después enviar la infromación a la vista
            throws MessagingException {
        String mensaje;
        if (!usuarioService.
                existeUsuarioPorUsernameOCorreo( //Se utilizan los metodos de usuarioService para buscar si el usuario existe
                        usuario.getUsername(), 
                        usuario.getCorreo())) {
            String clave = demeClave(); //Se genera una clave para el usuario
            usuario.setPassword(clave); //Se le setea esa clave al usuario
            usuario.setActivo(false); // el usuario se setea como que esta inactivo
            usuarioService.save(usuario, true); //Se guarda el usuario
            enviaCorreoActivar(usuario, clave);
            mensaje = String.format(
                    messageSource.getMessage(
                            "registro.mensaje.activacion.ok", //mensaje de activaión que dice que el usuario se activo de manera correcta
                            null, 
                            Locale.getDefault()),
                    usuario.getCorreo());
        } else {
            mensaje = String.format(
                    messageSource.getMessage(
                            "registro.mensaje.usuario.o.correo", //en caso de que el usuario ya exista, se manda un mensaje de que el usuario ya esta registrado
                            null, 
                            Locale.getDefault()),
                    usuario.getUsername(), usuario.getCorreo());
        }
        model.addAttribute(
                "titulo", 
                messageSource.getMessage(
                        "registro.activar", 
                        null, 
                        Locale.getDefault())); // se guardan los models con la información
        model.addAttribute(
                "mensaje", 
                mensaje);
        return model; //se retorna el model ya con la información
    }

    @Override
    public Model recordarUsuario(Model model, Usuario usuario) //metodo recordar un usuario que contiene el model y el objeto usuario para después enviar la infromación a la vista
            throws MessagingException {
        String mensaje;
        Usuario usuario2 = usuarioService.getUsuarioPorUsernameOCorreo( // se crea un objeto llamado usuario2 el cual pide el username y el correo
                usuario.getUsername(), 
                usuario.getCorreo());
        if (usuario2 != null) { //Compara el username y el correo para ver si existen
            String clave = demeClave(); //genera una clave
            usuario2.setPassword(clave); //le setea esa clave al usuario
            usuario2.setActivo(false); //mara al usuario como inacctivo
            usuarioService.save(usuario2, false); // se guarda al usuario
            enviaCorreoRecordar(usuario2, clave);//se envia el correo con la nueva clave del usuario
            mensaje = String.format(
                    messageSource.getMessage(
                            "registro.mensaje.recordar.ok", //mensaje de exito
                            null, 
                            Locale.getDefault()),
                    usuario.getCorreo());
        } else {
            mensaje = String.format(
                    messageSource.getMessage(
                            "registro.mensaje.usuario.o.correo", //mensaje de que el usuario y el correo no estan registrados
                            null, 
                            Locale.getDefault()),
                    usuario.getUsername(), usuario.getCorreo());
        }
        model.addAttribute(
                "titulo", 
                messageSource.getMessage(
                        "registro.activar", 
                        null, 
                        Locale.getDefault()));
        model.addAttribute(
                "mensaje", 
                mensaje);
        return model; // se retorna el modal con la información
    }

    private String demeClave() { //metodo generar clave
        String tira = "ABCDEFGHIJKLMNOPQRSTUXYZabcdefghijklmnopqrstuvwxyz0123456789_*+-"; //la clave que se genera puede utilizar estos caracteres
        String clave = "";//variable llamada clave la cual es vacia
        for (int i = 0; i < 40; i++) {
            clave += tira.charAt((int) (Math.random() * tira.length())); //se utiliza un for de tamaño 40 el cual tierne la función de generar una clave random de 40 caracteres
        }
        return clave; //retorna la clave ya generada
    }

    //Ojo cómo le lee una informacion del application.properties
    @Value("${servidor.http}") //valor donde se va a enviar el correo electronico
    private String servidor;

    private void enviaCorreoActivar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = messageSource.getMessage(
                "registro.correo.activar", 
                null, Locale.getDefault());
        mensaje = String.format(
                mensaje, usuario.getNombre(), 
                usuario.getApellidos(), servidor, 
                usuario.getUsername(), clave);
        String asunto = messageSource.getMessage(
                "registro.mensaje.activacion", 
                null, Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje); //este metodo se utiliza para obtener el cuerpo del correo
    }

    private void enviaCorreoRecordar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = messageSource.getMessage(""
                + "registro.correo.recordar", 
                null, 
                Locale.getDefault());
        mensaje = String.format(
                mensaje, usuario.getNombre(), 
                usuario.getApellidos(), servidor, 
                usuario.getUsername(), clave);
        String asunto = messageSource.getMessage(
                "registro.mensaje.recordar", 
                null, Locale.getDefault());
        correoService.enviarCorreoHtml(
                usuario.getCorreo(), 
                asunto, mensaje);
    }
}
