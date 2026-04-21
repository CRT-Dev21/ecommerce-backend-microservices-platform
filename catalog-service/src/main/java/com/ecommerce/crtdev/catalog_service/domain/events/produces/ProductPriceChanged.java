package com.ecommerce.crtdev.catalog_service.domain.events.produces;

import java.math.BigDecimal;

public record ProductPriceChanged(String productId, BigDecimal newPrice) {}
