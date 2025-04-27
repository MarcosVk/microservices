package com.example.Product_Service;

import com.example.Product_Service.dto.ProductRequest;
import com.example.Product_Service.dto.ProductResponse;
import com.example.Product_Service.model.Product;
import com.example.Product_Service.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MySQLContainer mySQLContainer=new MySQLContainer("mysql:8.0.33")
			.withDatabaseName("product_db")
			.withUsername("testuser")
			.withPassword("testpassword");

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url",mySQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username",mySQLContainer::getUsername);
		registry.add("spring.datasource.password",mySQLContainer::getPassword);
	}

	@Test
	void shouldCreateProduct() throws Exception {

		ProductRequest productRequest=getProductRequest();

		String productRequestString=objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());

		Assert.assertEquals(1,productRepository.findAll().size());
	}

	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
				.name("IQ mobile")
				.description("Andriod")
				.price(BigDecimal.valueOf(20000)).build();
	}

	@Test
	void getAllProduct() throws Exception {
		Product product=Product.builder()
				.name("IQ mobile")
				.description("Andriod")
				.price(new BigDecimal("20000.00")).build();

		productRepository.save(product);

		MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();

		String responseProduct=mvcResult.getResponse().getContentAsString();

		List<ProductResponse> responses = objectMapper.readValue(
				responseProduct,
				new TypeReference<List<ProductResponse>>() {}
		);

		Assert.assertFalse(responses.isEmpty());
		Assert.assertEquals("IQ mobile",responses.get(0).getName());
		Assert.assertEquals("Andriod",responses.get(0).getDescription());
		Assert.assertEquals(new BigDecimal("20000.00"),responses.get(0).getPrice());


	}

}
