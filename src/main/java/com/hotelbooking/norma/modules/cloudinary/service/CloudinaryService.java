package com.hotelbooking.norma.modules.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            // The `upload` method takes the file content and an options map
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Cloudinary returns a map of data about the uploaded image, including the URL
            return (String) uploadResult.get("url");

        } catch (IOException e) {
            // Handle exceptions (e.g., file not found, network issues)
            throw new IOException("Failed to upload image to Cloudinary", e);
        }
    }

    public void deletePhotoByUrl(String photoUrl) throws IOException {
        try {
            // Extract the public ID from the Cloudinary URL
            String publicId = extractPublicId(photoUrl);
            
            // Delete the image from Cloudinary
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            
        } catch (IOException e) {
            throw new IOException("Could not delete file from Cloudinary.", e);
        }
    }
    
    /**
     * A helper method to extract the public ID from a Cloudinary URL.
     * Example URL: https://res.cloudinary.com/demo/image/upload/v1574581452/hotel-images/my_hotel_photo.jpg
     * Public ID is 'hotel-images/my_hotel_photo'
     */
    private String extractPublicId(String photoUrl) {
        int folderIndex = photoUrl.indexOf("/hotel-images/");
        if (folderIndex == -1) {
             // Handle cases where the URL format might not match
            throw new IllegalArgumentException("Invalid Cloudinary URL format.");
        }
        String publicIdWithExtension = photoUrl.substring(folderIndex + 1);
        int dotIndex = publicIdWithExtension.lastIndexOf('.');
        if (dotIndex != -1) {
            return publicIdWithExtension.substring(0, dotIndex);
        }
        return publicIdWithExtension;
    }
}