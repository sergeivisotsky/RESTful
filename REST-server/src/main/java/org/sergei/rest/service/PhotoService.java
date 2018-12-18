package org.sergei.rest.service;

import org.sergei.rest.dto.PhotoDTO;
import org.sergei.rest.exceptions.FileNotFoundException;
import org.sergei.rest.exceptions.FileStorageException;
import org.sergei.rest.exceptions.ResourceNotFoundException;
import org.sergei.rest.model.Customer;
import org.sergei.rest.model.Photo;
import org.sergei.rest.repository.CustomerRepository;
import org.sergei.rest.repository.PhotoRepository;
import org.sergei.rest.util.ObjectMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergei Visotsky, 2018
 */
@Service
public class PhotoService {

    private static final String UPL_DIR = "D:/Program files/servers/apache-tomcat-9.0.10_API/webapps/media";
    private static final String CUSTOMER_NOT_FOUND = "Customer with this ID not found";
    private final Path fileStorageLocation;
    private final PhotoRepository photoRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, CustomerRepository customerRepository) {
        this.photoRepository = photoRepository;
        this.customerRepository = customerRepository;
        this.fileStorageLocation = Paths.get(UPL_DIR).toAbsolutePath().normalize();
    }

    /**
     * Method to find all photos by customer number
     *
     * @param customerId get customer number from the REST controller
     * @return list of the photo DTOs as a response
     */
    public List<PhotoDTO> findAll(Long customerId) {
        List<PhotoDTO> photoDTOList = new LinkedList<>();

        List<Photo> photos = photoRepository.findAllPhotosByCustomerId(customerId);
        if (photos == null) {
            throw new ResourceNotFoundException("Invalid customer ID or photos not found");
        }
        photos.forEach(photo ->
                photoDTOList.add(
                        ObjectMapperUtil.map(photo, PhotoDTO.class)
                )
        );

        return photoDTOList;
    }

    /**
     * Method to upload file on the server
     *
     * @param customerId           get customer number from the REST controller
     * @param fileDownloadUri      get file download uri created in REST controller
     * @param commonsMultipartFile get file uploaded from the REST controller
     * @return photo DTO response
     */
    public PhotoDTO uploadFileByCustomerId(Long customerId, String fileDownloadUri,
                                           CommonsMultipartFile commonsMultipartFile) {

        String fileName = StringUtils.cleanPath(commonsMultipartFile.getOriginalFilename());

        if (fileDownloadUri.length() > 150) {
            throw new FileStorageException("Too long file name");
        }

        // Filename recreation
        try {
            // Check if file contains inappropriate symbols
            if (fileName.contains("..")) {
                throw new FileStorageException("Invalid path sequence");
            }

            // Store files on the server directory replacing existing if it exists
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(commonsMultipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND)
                    );

            Photo photo = new Photo();

            photo.setCustomer(customer);
            photo.setFileName(commonsMultipartFile.getOriginalFilename());
            photo.setFileUrl(fileDownloadUri);
            photo.setFileType(commonsMultipartFile.getContentType());
            photo.setFileSize(commonsMultipartFile.getSize());

            // Save file metadata into a database
            Photo savedPhoto = photoRepository.save(photo);

            return ObjectMapperUtil.map(savedPhoto, PhotoDTO.class);
        } catch (IOException e) {
            throw new FileStorageException("Cannot store file");
        }
    }

    /**
     * Method to download file from the server by file name
     *
     * @param customerId get customer number from the REST controller
     * @param fileName   get file name from the RESt controller
     * @return Resource
     * @throws MalformedURLException throws in case of invalid uri
     */
    public Resource downloadFileAsResourceByName(Long customerId, String fileName) throws MalformedURLException {
        // Get filename by customer number written in database
        Photo photo = photoRepository.findPhotoByCustomerIdAndFileName(customerId, fileName)
                .orElseThrow(
                        () -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND)
                );

        Path filePath = this.fileStorageLocation.resolve(photo.getFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        // Check if file exists
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found");
        }
    }

    /**
     * Method to download file from the server by file ID
     *
     * @param customerId get customer number from the REST controller
     * @param photoId    get photo IR from the REST controller
     * @return Returns resource
     * @throws MalformedURLException throws in case of invalid uri
     */
    public Resource downloadFileAsResourceByFileId(Long customerId, Long photoId) throws MalformedURLException {
        // Get filename by customer id written in database
        Photo photo = photoRepository.findByCustomerIdAndPhotoId(customerId, photoId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND)
                );

        String responseFileName = photo.getFileName();

        Path filePath = this.fileStorageLocation.resolve(responseFileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        // Check if file exists
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found");
        }
    }

    /**
     * Method to perform file deletion by customer number and photo ID
     *
     * @param customerId get customer number from the REST controller
     * @param photoId    get photo IR from the REST controller
     * @return photo DTO as a response
     * @throws IOException Input-output exception
     */
    public PhotoDTO deleteById(Long customerId, Long photoId) throws IOException {
        Photo photo = photoRepository.findByCustomerIdAndPhotoId(customerId, photoId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND)
                );

        PhotoDTO photoDTO = ObjectMapperUtil.map(photo, PhotoDTO.class);

        String responseFileName = photo.getFileName();

        // Delete photo from temp storage
        Path targetLocation = this.fileStorageLocation.resolve(responseFileName);
        Files.deleteIfExists(targetLocation);

        photoRepository.delete(photo); // Delete file metadata from the database

        return photoDTO;
    }
}
