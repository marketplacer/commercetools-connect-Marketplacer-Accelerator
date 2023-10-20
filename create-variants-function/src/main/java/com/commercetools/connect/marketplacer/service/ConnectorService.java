package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.models.common.Image;
import com.commercetools.api.models.common.PriceDraft;
import com.commercetools.api.models.common.Reference;
import com.commercetools.api.models.common.ReferenceBuilder;
import com.commercetools.api.models.product.*;
import com.commercetools.connect.marketplacer.model.Edge;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import com.commercetools.connect.marketplacer.model.Option;
import com.commercetools.connect.marketplacer.utils.ConfigReader;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConnectorService {
    private static final Logger logger = Logger.getLogger(ConnectorService.class.getName());

    private static final ConfigReader configReader = new ConfigReader();

    private final ClientService clientService;

    @Autowired
    ConnectorService(ClientService clientService) {
        this.clientService = clientService;
    }

    public String createVariants(MarketplacerRequest marketplacerRequest) throws Exception {
        JsonObject jsonResponse = new JsonObject();
        try {
            Optional<Product> product = clientService.getProductByKey(marketplacerRequest.getPayload().getData().getNode().getLegacyId());
            if (!product.isPresent()) {
                String productId = createProduct(marketplacerRequest);
                jsonResponse.addProperty("productId", productId);
                logger.info("Product created: " + productId);
            } else {
                updateProduct(product.get(), marketplacerRequest);
                jsonResponse.addProperty("updatedProduct", product.get().getId());
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            jsonResponse = new JsonObject();
            jsonResponse.addProperty("stackTrace" , stacktrace);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new Exception(jsonResponse.toString());
        }
        return jsonResponse.toString();
    }

    private String createProduct(MarketplacerRequest marketplacerRequest) {
        List<ProductVariantDraft> variants = createProductVariantDraft(marketplacerRequest);
        ProductVariantDraft master = variants.get(0);
        variants.remove(0);
        final Reference categoryReference = ReferenceBuilder.of().categoryBuilder().id(configReader.getRootCategory()).build();
        final Reference childCat = ReferenceBuilder.of().categoryBuilder().id(configReader.getChildCategory()).build();
        List categories = new ArrayList();
        categories.add(categoryReference);
        categories.add(childCat);
        ProductDraft newProductDetails = ProductDraft
                .builder()
                .name(stringBuilder ->
                        stringBuilder
                                .addValue("en-US", marketplacerRequest.getPayload().getData().getNode().getTitle())

                )
                .productType(typeBuilder -> typeBuilder.key(configReader.getMainProductType()))
                .key(marketplacerRequest.getPayload().getData().getNode().getLegacyId())
                .slug(stringBuilder ->
                        stringBuilder
                                .addValue("en-US", "prod" + marketplacerRequest.getPayload().getData().getNode().getTitle().toLowerCase().replace(" ", "-").replace("'",""))
                )
                .description(stringBuilder ->
                        stringBuilder
                                .addValue("en-US", marketplacerRequest.getPayload().getData().getNode().getDescription())
                )
                .categories(categories)
                .masterVariant(master)
                .variants(variants)
                .build();

        return clientService.executeCreateProduct(newProductDetails);
    }

    private static List<ProductVariantDraft> createProductVariantDraft(MarketplacerRequest marketplacerRequest) {
        List<ProductVariantDraft> variantsDraft = new ArrayList<>();
        for (Edge variant : marketplacerRequest.getPayload().getData().getNode().getVariants().getEdges()) {
            List<Option> options = variant.getNode().getOptionValues().getNodes();
            Optional<Option> size = options.stream().filter(option -> option.getOptionType().getName().contains("Size")).findFirst() ;
            String sizeValue = size.isPresent() ? size.get().getName() :"1";
            ProductVariantDraft newProductVariantDetails = ProductVariantDraft
                    .builder()
                    .sku(variant.getNode().getSku())
                    .key(variant.getNode().getSku())
                    .prices(createPriceDraft(variant))
                    .images(createImages(marketplacerRequest))
                    .plusAttributes(attributeBuilder -> attributeBuilder.name("size").value(sizeValue))
                    .build();
            variantsDraft.add(newProductVariantDetails);
        }
        return variantsDraft;
    }

    private static PriceDraft createPriceDraft(Edge variant) {
        PriceDraft priceDraft = PriceDraft.builder()
                .value(moneyBuilder -> moneyBuilder.currencyCode("USD").centAmount((long)Double.parseDouble(variant.getNode().getLowestPrice())*100))
                .build();
        return priceDraft;
    }

    private static List<Image> createImages(MarketplacerRequest marketplacerRequest) {
        List<Image> images = new ArrayList<>();
        Image image = Image.builder().dimensions(imageDimensionsBuilder -> imageDimensionsBuilder.h(500).w(500)).url(marketplacerRequest.getPayload().getData().getNode().getDisplayImage().getUrl())
                .build();
        images.add(image);
        return images;
    }

    private Product updateProduct(Product productToUpdate, MarketplacerRequest marketplacerRequest) {
        Product updatedProduct = null;
        for (int i = 0; i < productToUpdate.getMasterData().getCurrent().getVariants().size(); i++) {
            ProductVariant variant = productToUpdate.getMasterData().getCurrent().getVariants().get(i);
            final int index = i;
            ProductUpdate productUpdate = ProductUpdate
                    .builder()
                    .version(productToUpdate.getVersion())
                    .plusActions(actionBuilder ->
                            actionBuilder.changeNameBuilder()
                                    .name(stringBuilder ->
                                            stringBuilder
                                                    .addValue("en-US", marketplacerRequest.getPayload().getData().getNode().getTitle())))
                    .plusActions(actionBuilder -> actionBuilder.changePriceBuilder()
                            .priceId(variant.getPrices().get(0).getId())
                            .price(PriceDraft.builder()
                                    .value(moneyBuilder -> moneyBuilder.currencyCode("USD").centAmount((long) Double.parseDouble(marketplacerRequest.getPayload().getData().getNode().getVariants().getEdges().get(index + 1).getNode().getLowestPrice()) * 100))
                                    .build()))
                    .build();

            updatedProduct = clientService.executeUpdateProduct(productToUpdate, productUpdate);

            String updatedProductKey = updatedProduct.getKey();
            logger.log(Level.INFO, updatedProductKey);
        }

        return updateMasterVariant(productToUpdate, marketplacerRequest);
    }

    private Product updateMasterVariant(Product productToUpdate, MarketplacerRequest marketplacerRequest) {
        ProductUpdate productUpdate = ProductUpdate
                .builder()
                .version(productToUpdate.getVersion())
                .plusActions(actionBuilder ->
                        actionBuilder.changeNameBuilder()
                                .name(stringBuilder ->
                                        stringBuilder
                                                .addValue("en-US", marketplacerRequest.getPayload().getData().getNode().getTitle())))
                .plusActions(actionBuilder -> actionBuilder.changePriceBuilder()
                        .priceId(productToUpdate.getMasterData().getCurrent().getMasterVariant().getPrices().get(0).getId())
                        .price(PriceDraft.builder()
                                .value(moneyBuilder -> moneyBuilder.currencyCode("USD").centAmount((long) Double.parseDouble(marketplacerRequest.getPayload().getData().getNode().getVariants().getEdges().get(0).getNode().getLowestPrice())*100))
                                .build()))
                .build();

        Product updatedProduct = clientService.executeUpdateProduct(productToUpdate, productUpdate);

        String updatedProductKey = updatedProduct.getKey();
        logger.log(Level.INFO, updatedProductKey);

        return updatedProduct;
    }

}
