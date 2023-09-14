package com.opendev.odata.util;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public final class Constants {

	private Constants() {
        throw new IllegalStateException("Utility class");
    }
	
	// Service Namespace
	public static final String NAMESPACE = "OData.POC";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_PRODUCT_NAME = "Product";
	public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

	// Entity Set Names
	public static final String ES_PRODUCTS_NAME = "Products";

    public static final String ET_CATEGORY_NAME = "Category";
    public static final FullQualifiedName ET_CATEGORY_FQN = new FullQualifiedName(NAMESPACE, ET_CATEGORY_NAME);

    public static final String ES_CATEGORIES_NAME = "Categories";

    public static final String ET_RATE_NAME = "Rate";
    public static final FullQualifiedName ET_RATES_FQN = new FullQualifiedName(NAMESPACE, ET_RATE_NAME);
    public static final String ES_RATES_NAME = "Rates";

    // Function
    public static final String FUNCTION_COUNT_CATEGORIES = "CountCategories";
    public static final FullQualifiedName FUNCTION_COUNT_CATEGORIES_FQN
            = new FullQualifiedName(NAMESPACE, FUNCTION_COUNT_CATEGORIES);
    public static final String PARAMETER_AMOUNT = "Amount";
    // Function
    public static final String FUNCTION_GET_RATES_BY_YEAR = "GetRatesByYear";
    public static final FullQualifiedName FUNCTION_GET_RATES_BY_YEAR_FQN
            = new FullQualifiedName(NAMESPACE, FUNCTION_GET_RATES_BY_YEAR);
    public static final String PARAMETER_YEAR= "Year";


}
