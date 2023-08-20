package com.opendev.odata.util;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public final class Constants {

	private Constants() {
        throw new IllegalStateException("Utility class");
    }
	
	// Service Namespace
	public static final String NAMESPACE = "OData.Demo";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_PRODUCT_NAME = "Product";
	public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

	// Entity Set Names
	public static final String ES_PRODUCTS_NAME = "Products";

    // EDM Container
//    public static final String CONTAINER_NAME = "Container";
//    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    // Entity Types Names
//    public static final String ET_PRODUCT_NAME = "Product";
//    public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

    public static final String ET_CATEGORY_NAME = "Category";
    public static final FullQualifiedName ET_CATEGORY_FQN = new FullQualifiedName(NAMESPACE, ET_CATEGORY_NAME);

    // Entity Set Names
//    public static final String ES_PRODUCTS_NAME = "Products";
    public static final String ES_CATEGORIES_NAME = "Categories";

    // Action
    public static final String ACTION_RESET = "Reset";
    public static final FullQualifiedName ACTION_RESET_FQN = new FullQualifiedName(NAMESPACE, ACTION_RESET);

    //Bound Action
    public static final String ACTION_PROVIDE_DISCOUNT = "DiscountProducts";
    public static final FullQualifiedName ACTION_PROVIDE_DISCOUNT_FQN = new FullQualifiedName(NAMESPACE, ACTION_PROVIDE_DISCOUNT);

    //Bound Action
    public static final String ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT = "DiscountProduct";
    public static final FullQualifiedName ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT);

    // Function
    public static final String FUNCTION_COUNT_CATEGORIES = "CountCategories";
    public static final FullQualifiedName FUNCTION_COUNT_CATEGORIES_FQN
            = new FullQualifiedName(NAMESPACE, FUNCTION_COUNT_CATEGORIES);
    //Bound Function
    public static final String FUNCTION_PROVIDE_DISCOUNT = "GetDiscountProducts";
    public static final FullQualifiedName FUNCTION_PROVIDE_DISCOUNT_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_PROVIDE_DISCOUNT);

    public static final String FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT = "GetDiscountProduct";
    public static final FullQualifiedName FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT);

    // Function/Action Parameters
    public static final String PARAMETER_AMOUNT = "Amount";

    //Bound Action Binding Parameter
    public static final String PARAMETER_CATEGORY = "ParamCategory";

    //Bound Function Binding Parameter
    public static final String PARAMETER_BIND = "BindingParameter";

}
