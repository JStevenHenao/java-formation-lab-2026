package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import co.com.inventory.inventoryservice.usecases.IInventoryPatchUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.IllegalPathStateException;
import java.util.List;

@Service
public class InventoryPatchUseCase implements IInventoryPatchUseCase {

    private final IInventoryService service;

    @Autowired
    public InventoryPatchUseCase(IInventoryService service) {
        this.service = service;
    }

    @Override
    public void update(String id, ProductDto productDto) throws IllegalStateException, IllegalArgumentException {
        if (id == null || id.isBlank() || productDto == null) {
            throw new IllegalArgumentException("El registro no esta especificado para actualizar!");
        }

        List<ProductDto> producto = service.getById(id);
        if (producto.isEmpty()) {
            throw new IllegalStateException("El registro no existe!");
        }

        ProductDto current = producto.get(0);

        if (productDto.getDescription() != null) {
            current.setDescription(productDto.getDescription());
        }

        if (productDto.getName() != null) {
            current.setName(productDto.getName());
        }

        if (productDto.getUnits() != null) {
            current.setUnits(productDto.getUnits());
        }

        if (productDto.getQuantity() != null) {
            current.setQuantity(productDto.getQuantity());
        }

        service.update(current);
    }
}
