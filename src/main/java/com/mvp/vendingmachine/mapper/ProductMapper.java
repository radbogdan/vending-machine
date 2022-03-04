package com.mvp.vendingmachine.mapper;

import com.mvp.vendingmachine.api.model.ProductDto;
import com.mvp.vendingmachine.storage.model.StoredProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<ProductDto> asProductList(final List<StoredProduct> src);

    ProductDto asProductDto(final StoredProduct src);

    StoredProduct asStoredProduct(final ProductDto src);
}
