package com.example.Inventory_Service.controller;

import com.example.Inventory_Service.service.Inventory_Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class Inventory_Controller {

    private final Inventory_Service inventoryService;

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("sku-Code") String skuCode){
        return inventoryService.isInStock(skuCode);
    }
}
