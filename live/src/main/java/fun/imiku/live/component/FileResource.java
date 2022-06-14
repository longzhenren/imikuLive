/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.component;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Configuration
public class FileResource implements WebMvcConfigurer {
    @Value("${site.files}")
    String localFile;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + localFile + "/");
    }

    public void saveAndDeletePrev(MultipartFile file, String path, String name, String prev) throws IOException {
        FileOutputStream fileOutputStream =
                new FileOutputStream(localFile + path + name);
        IOUtils.copy(file.getInputStream(), fileOutputStream);
        fileOutputStream.close();
        if (prev.equals("auto"))
            return;
        new File(localFile + path + prev).delete();
    }
}
