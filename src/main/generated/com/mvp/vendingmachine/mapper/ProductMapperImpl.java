package com.mvp.vendingmachine.mapper;

import com.mvp.vendingmachine.api.model.ProductDto;
import com.mvp.vendingmachine.storage.model.StoredProduct;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-04T12:30:29+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public List<ProductDto> asProductList(List<StoredProduct> src) {
        if ( src == null ) {
            return null;
        }

        List<ProductDto> list = new ArrayList<ProductDto>( src.size() );
        for ( StoredProduct storedProduct : src ) {
            list.add( asProductDto( storedProduct ) );
        }

        return list;
    }

    @Override
    public ProductDto asProductDto(StoredProduct src) {
        if ( src == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setProductName( src.getProductName() );
        productDto.setAmountAvailable( src.getAmountAvailable() );
        productDto.setCost( src.getCost() );
        productDto.setSellerId( src.getSellerId() );

        return productDto;
    }

    @Override
    public StoredProduct asStoredProduct(ProductDto src) {
        if ( src == null ) {
            return null;
        }

        StoredProduct storedProduct = new StoredProduct();

        storedProduct.setProductName( src.getProductName() );
        storedProduct.setAmountAvailable( src.getAmountAvailable() );
        storedProduct.setCost( src.getCost() );
        storedProduct.setSellerId( src.getSellerId() );

        return storedProduct;
    }
}
