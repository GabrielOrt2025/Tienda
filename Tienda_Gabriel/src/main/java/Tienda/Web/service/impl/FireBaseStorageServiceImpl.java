/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package Tienda.Web.service.impl;

import com.google.auth.Credentials;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.cloud.storage.StorageOptions;
import Tienda.Web.service.FireBaseStorageService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FireBaseStorageServiceImpl implements FireBaseStorageService {
    @Override
    
    public String cargaImagen (MultipartFile archivoProductoCliente, String carpeta, Long id) {
        try {
            // Se hace original del archivo local del Client
            String extension = archivoProductoCliente.getOriginalFilename();

            // Se convierte name el archivo y se codifica para la BD
            String filename = "img" + sacaNumero(id) + extension;

            // Se convierte/sube el archivo a un archivo temporal
            File file = this.convertToFile(archivoProductoCliente);

            // Se copia a Firebase y se obtiene el url salido de la imagen (por 10 años)
            String URL = this.uploadFile(file, carpeta, filename);

            // Se elimina el archivo temporal cargado desde el cliente
            file.delete();

            return URL;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

   private String uploadFile(File file,
                          String carpeta,
                          String fileName) throws IOException {
// Se define el lugar y acceso al archivo .jasper
    ClassPathResource json = new ClassPathResource(rutaJsonFile + File.separator + archivoJsonFile);
    BlobId blobId = BlobId.of(bucketName,
            rutaSuperiorStorage + "/" + carpeta + "/" + fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType("media").build();

    Credentials credentials = GoogleCredentials
            .fromStream(json.getInputStream());
    Storage storage = StorageOptions.newBuilder()
            .setCredentials(credentials).build().getService();
    storage.create(blobInfo, Files.readAllBytes(file.toPath()));
    String url = storage.signUrl(blobInfo,
            3650,
            TimeUnit.DAYS,
            Storage.SignUrlOption.signWith((ServiceAccountSigner) credentials))
            .toString();
    return url;
}


    //Método utilitario que convierte el archivo desde el equipo local
    // del usuario a un archivo temporal en el servidor
    private File convertToFile(MultipartFile archivoLocalCliente) throws IOException {
        File tempFile = File.createTempFile("img", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(archivoLocalCliente.getBytes());
        }
        return tempFile;
    }

    //Método utilitario para obtener un string con ceros....
    private String sacaNumero(Long id) {
        return String.format("%019d", id);
    }
}
