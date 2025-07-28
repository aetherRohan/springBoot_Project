package com.smart.smartcontactmanager.Helper;

import com.smart.smartcontactmanager.Entities.Contact;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Component
public class Upload {

    public void Uploader(MultipartFile file, Contact contact) throws Exception {

        if (file.isEmpty()) {
            if (contact.getImageUrl()==null|| contact.getImageUrl().trim().isEmpty()){
           contact.setImageUrl("image/contact.jpg");
            }
        }
        else {

        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
            System.out.println("Uploaded file must be Image,Jpeg or Png");
            return;
        }

        String time = LocalDate.now().toString().replace("-", "");
        String filename = file.getOriginalFilename();
        contact.setImageUrl(time + "_" + filename);

   //    final Path path = Paths.get(new ClassPathResource("static/image").getFile().getAbsolutePath(), time + "_" + filename);
        final Path path = Paths.get("C:/profile_pic", time + "_" + filename);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    }
}

