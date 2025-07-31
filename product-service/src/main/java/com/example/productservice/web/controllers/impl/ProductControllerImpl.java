package com.example.productservice.web.controllers.impl;

import com.example.productservice.data.entities.Product;
import com.example.productservice.service.ProductService;
import com.example.productservice.web.controllers.ProductController;
import com.example.productservice.web.dto.ProductBasicDTO;
import com.example.productservice.web.dto.ProductResponseDTO;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;
    private final String maxAge ="300";

    public ProductControllerImpl(ProductService productService) {
        this.productService = productService;
    }
    @Override
    public ResponseEntity<ProductResponseDTO> create(ProductBasicDTO productBasicDTO) {
        System.out.println("CREATE product : " + productBasicDTO);
//        String userId = (String) auth.getPrincipal();
//        System.out.println("userId(from createProduct): " + userId);
        Product product = new Product();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + maxAge)
                .body(new ProductResponseDTO(productService.create(productBasicDTO.toProduct(product.getUserId()))));
    }

    @Override
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        System.out.println("GET(getAll) products");
        List<ProductResponseDTO> allDtos = productService.getAllProducts().stream()
                .map(ProductResponseDTO::new)
                .toList();
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + maxAge)
                .body(allDtos);
    }

    @Override
    public ResponseEntity<ProductResponseDTO> getById(String id) {
        System.out.println("GET(getById) product : " + id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + maxAge)
                .body(new ProductResponseDTO(productService.getById(id)));
    }

    @Override
    public ResponseEntity<ProductResponseDTO> update(String id, ProductBasicDTO productBasicDTO) {
        System.out.println("UPDATE product : " + productBasicDTO);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String userId = (String) auth.getPrincipal();
        Product product = new Product();
        System.out.println("userId(from updateProduct): " + product.getUserId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + maxAge)
                .body(new ProductResponseDTO(
                        productService.update(
                                id, productBasicDTO.toProduct(product.getUserId()))));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        System.out.println("DELETE product : " + id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
