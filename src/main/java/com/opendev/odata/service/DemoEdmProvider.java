package com.opendev.odata.service;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.opendev.odata.util.Constants.*;

/*
 * this class is supposed to declare the metadata of the OData service
 * it is invoked by the Olingo framework e.g. when the metadata document of the service is invoked
 * e.g. http://localhost:8080/ExampleService1/ExampleService1.svc/$metadata
 */
@Component
public class DemoEdmProvider extends CsdlAbstractEdmProvider {
    @Override
    public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) {
        if (functionName.equals(FUNCTION_COUNT_CATEGORIES_FQN)) {
            // It is allowed to overload functions, so we have to provide a list of functions for each function name
            final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

            // Create the parameter for the function
            final CsdlParameter parameterAmount = new CsdlParameter();
            parameterAmount.setName(PARAMETER_AMOUNT);
            parameterAmount.setNullable(false);
            parameterAmount.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

            // Create the return type of the function
            final CsdlReturnType returnType = new CsdlReturnType();
            returnType.setCollection(true);
            returnType.setType(ET_CATEGORY_FQN);

            // Create the function
            final CsdlFunction function = new CsdlFunction();
            function.setName(FUNCTION_COUNT_CATEGORIES_FQN.getName())
                    .setParameters(Arrays.asList(parameterAmount))
                    .setReturnType(returnType);
            functions.add(function);

            return functions;
        }else if (functionName.equals(FUNCTION_GET_RATES_BY_YEAR_FQN)){
            // It is allowed to overload functions, so we have to provide a list of functions for each function name
            final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

            // Create the parameter for the function
            final CsdlParameter parameterYear = new CsdlParameter();
            parameterYear.setName(PARAMETER_YEAR);
            parameterYear.setNullable(false);
            parameterYear.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

            // Create the return type of the function
            final CsdlReturnType returnType = new CsdlReturnType();
            returnType.setCollection(true);
            returnType.setType(ET_RATES_FQN);

            // Create the function
            final CsdlFunction function = new CsdlFunction();
            function.setName(FUNCTION_GET_RATES_BY_YEAR_FQN.getName())
                    .setParameters(Arrays.asList(parameterYear))
                    .setReturnType(returnType);
            functions.add(function);

            return functions;
        }
        return null;
    }

    @Override
    public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName) {
        if (entityContainer.equals(CONTAINER)) {
            if (functionImportName.equals(FUNCTION_COUNT_CATEGORIES_FQN.getName())) {
                return new CsdlFunctionImport()
                        .setName(functionImportName)
                        .setFunction(FUNCTION_COUNT_CATEGORIES_FQN)
                        .setEntitySet(ES_CATEGORIES_NAME)
                        .setIncludeInServiceDocument(true);
            }else if (functionImportName.equals(FUNCTION_GET_RATES_BY_YEAR_FQN.getName())){
                return new CsdlFunctionImport()
                        .setName(functionImportName)
                        .setFunction(FUNCTION_GET_RATES_BY_YEAR_FQN)
                        .setEntitySet(ES_RATES_NAME)
                        .setIncludeInServiceDocument(true);
            }
        }

        return null;
    }

    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

        // this method is called for each EntityType that are configured in the Schema
        CsdlEntityType entityType = null;

        if (entityTypeName.equals(ET_PRODUCT_FQN)) {
            // create EntityType properties
            CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty price = new CsdlProperty().setName("Price").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

            // create PropertyRef for Key element
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("ID");

            // navigation property: many-to-one, null not allowed (product must have a category)
            CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName("Category").setType(ET_CATEGORY_FQN).setNullable(true).setPartner("Products");
            List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
            navPropList.add(navProp);

            // configure EntityType
            entityType = new CsdlEntityType();
            entityType.setName(ET_PRODUCT_NAME);
            entityType.setProperties(Arrays.asList(id, name, description, price));
            entityType.setKey(Arrays.asList(propertyRef));
            entityType.setNavigationProperties(navPropList);

        } else if (entityTypeName.equals(ET_CATEGORY_FQN)) {
            // create EntityType properties
            CsdlProperty id = new CsdlProperty().setName("ID")
                    .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty name = new CsdlProperty().setName("Name")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            // create PropertyRef for Key element
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("ID");

            // navigation property: one-to-many
            CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName("Products")
                    .setType(ET_PRODUCT_FQN).setCollection(true)
                    .setPartner("Category");
            List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
            navPropList.add(navProp);

            // configure EntityType
            entityType = new CsdlEntityType();
            entityType.setName(ET_CATEGORY_NAME);
            entityType.setProperties(Arrays.asList(id, name));
            entityType.setKey(Arrays.asList(propertyRef));
            entityType.setNavigationProperties(navPropList);
        } else if (entityTypeName.equals(ET_RATES_FQN)){
            CsdlProperty dateTime = new CsdlProperty().setName("DateTime")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty mortgage30Year = new CsdlProperty().setName("Mortgage30Year")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            entityType = new CsdlEntityType();
            entityType.setName(ET_RATE_NAME);
            entityType.setProperties(Arrays.asList(dateTime, mortgage30Year));
        }
        return entityType;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

        CsdlEntitySet entitySet = null;

        if (entityContainer.equals(CONTAINER)) {
            if (entitySetName.equals(ES_PRODUCTS_NAME)) {
                entitySet = new CsdlEntitySet();
                entitySet.setName(ES_PRODUCTS_NAME);
                entitySet.setType(ET_PRODUCT_FQN);

            } else if (entitySetName.equals(ES_CATEGORIES_NAME)) {
                entitySet = new CsdlEntitySet();
                entitySet.setName(ES_CATEGORIES_NAME);
                entitySet.setType(ET_CATEGORY_FQN);

            } else if (entitySetName.equals(ES_RATES_NAME)) {
                entitySet = new CsdlEntitySet();
                entitySet.setName(ES_RATES_NAME);
                entitySet.setType(ET_RATES_FQN);
            }
        }

        return entitySet;

    }

    @Override
    public List<CsdlSchema> getSchemas() {

        // create Schema
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // add EntityTypes
        List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
        entityTypes.add(getEntityType(ET_PRODUCT_FQN));
        entityTypes.add(getEntityType(ET_CATEGORY_FQN));
        entityTypes.add(getEntityType(ET_RATES_FQN));
        schema.setEntityTypes(entityTypes);

        // add functions
        List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
        functions.addAll(getFunctions(FUNCTION_COUNT_CATEGORIES_FQN));
        functions.addAll(getFunctions(FUNCTION_GET_RATES_BY_YEAR_FQN));
        schema.setFunctions(functions);

        // add EntityContainer
        schema.setEntityContainer(getEntityContainer());

        // finally
        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        schemas.add(schema);

        return schemas;
    }

    public CsdlEntityContainer getEntityContainer() {
        // create EntitySets
        List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
        entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_CATEGORIES_NAME));
        entitySets.add(getEntitySet(CONTAINER, ES_RATES_NAME));

        // Create function imports
        List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
        functionImports.add(getFunctionImport(CONTAINER, FUNCTION_COUNT_CATEGORIES));
        functionImports.add(getFunctionImport(CONTAINER, FUNCTION_GET_RATES_BY_YEAR));

        // create EntityContainer
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);
        entityContainer.setFunctionImports(functionImports);

        return entityContainer;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

        // This method is invoked when displaying the service document at
        // e.g. http://localhost:8080/DemoService/DemoService.svc
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }
        return null;
    }
}
