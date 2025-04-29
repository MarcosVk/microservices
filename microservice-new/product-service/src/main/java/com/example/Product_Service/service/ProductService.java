package com.example.Product_Service.service;

import com.example.Product_Service.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.Product_Service.model.Product;
import com.example.Product_Service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;

	public void createProductService(Product productRequest) {
		Product product=Product.builder().
				name(productRequest.getName()).
				description(productRequest.getDescription()).
				price(productRequest.getPrice()).build();
		
		productRepository.save(product);

		log.info("Product {} is saved",product.getId());
	}

	public List<ProductResponse> getAllProducts(){
		List<Product> products=productRepository.findAll();

		return products.stream().map(this::mapToProductResponse).toList();
	}
	public ProductResponse mapToProductResponse(Product product){
		return ProductResponse.builder().id(product.getId()).name(product.getName())
				.description(product.getDescription()).price(product.getPrice()).build();
	}

}
