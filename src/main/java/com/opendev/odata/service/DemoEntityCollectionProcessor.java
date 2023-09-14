package com.opendev.odata.service;

import com.opendev.odata.data.Storage;
import com.opendev.odata.util.Constants;
import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.opendev.odata.util.Constants.*;

@Component
public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	// our database-mock
	private Storage storage;

	@Autowired
	public DemoEntityCollectionProcessor(Storage storage) {
		this.storage = storage;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	/*
	 * This method is invoked when a collection of entities has to be read.
	 * In our example, this can be either a "normal" read operation, or a navigation:
	 *
	 * Example for "normal" read entity set operation:
	 * http://localhost:8080/DemoService/DemoService.svc/Categories
	 *
	 * Example for navigation
	 * http://localhost:8080/DemoService/DemoService.svc/Categories(3)/Products
	 */
	public void readEntityCollection(ODataRequest request, ODataResponse response,
									 UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, SerializerException {

		final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);

		if (firstResourceSegment instanceof UriResourceEntitySet) {
			readEntityCollectionInternal(request, response, uriInfo, responseFormat);
		} else if (firstResourceSegment instanceof UriResourceFunction) {
			readFunctionImportCollection(request, response, uriInfo, responseFormat);
		} else {
			throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	private void readFunctionImportCollection(final ODataRequest request, final ODataResponse response,
											  final UriInfo uriInfo, final ContentType responseFormat) throws ODataApplicationException, SerializerException {

		// 1st step: Analyze the URI and fetch the entity collection returned by the function import
		// Function Imports are always the first segment of the resource path
		final UriResource firstSegment = uriInfo.getUriResourceParts().get(0);

		if (!(firstSegment instanceof UriResourceFunction)) {
			throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}

		final UriResourceFunction uriResourceFunction = (UriResourceFunction) firstSegment;
		EntityCollection entityCol = null;
//		final EntityCollection entityCol = storage.readFunctionImportCollection(uriResourceFunction, serviceMetadata);
		if (firstSegment.getSegmentValue().equals(FUNCTION_COUNT_CATEGORIES)) {
			entityCol = storage.readFunctionImportCollection(uriResourceFunction, serviceMetadata);
		} else if (firstSegment.getSegmentValue().equals(FUNCTION_GET_RATES_BY_YEAR)) {
			entityCol = readGetRatesByYearImportCollection(uriResourceFunction, serviceMetadata);
		}
		// 2nd step: Serialize the response entity
		final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
		final ContextURL contextURL = ContextURL.with().asCollection().type(edmEntityType).build();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextURL).build();
		final ODataSerializer serializer = odata.createSerializer(responseFormat);
		final SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCol,
				opts);

		// 3rd configure the response object
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	private void readEntityCollectionInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo,
											  ContentType responseFormat) throws ODataApplicationException, SerializerException {

		EdmEntitySet responseEdmEntitySet = null; // we'll need this to build the ContextURL
		EntityCollection responseEntityCollection = null; // we'll need this to set the response body

		// 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
		List<UriResource> resourceParts = uriInfo.getUriResourceParts();
		int segmentCount = resourceParts.size();

		UriResource uriResource = resourceParts.get(0); // in our example, the first segment is the EntitySet
		if (!(uriResource instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Only EntitySet is supported",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
		}

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
		EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();
		responseEdmEntitySet = startEdmEntitySet;
		if (startEdmEntitySet.getName().equals(ES_PRODUCTS_NAME)) {
			responseEntityCollection = storage.readEntitySetData(startEdmEntitySet);
		} else if (startEdmEntitySet.getName().equals(ES_CATEGORIES_NAME)) {
			responseEntityCollection = storage.readEntitySetData(startEdmEntitySet);
		} else if (startEdmEntitySet.getName().equals(ES_RATES_NAME)) {
			responseEntityCollection = getData(startEdmEntitySet.getName());
		}

		// 3rd: create and configure a serializer
		ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).build();
		final String id = request.getRawBaseUri() + "/" + responseEdmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
				.contextURL(contextUrl).id(id).build();
		EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();

		ODataSerializer serializer = odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType,
				responseEntityCollection, opts);

		// 4th: configure the response object: set the body, headers and status code
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	private List<String[]> readCSVFile(String csvFile) {
		String line;
		String csvDelimiter = ",";
		List<String[]> csvData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(csvDelimiter);
				csvData.add(values);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return csvData;
	}

	private EntityCollection getData(String edmEntitySetName) {
		List<String[]> csvData = readCSVFile("/Users/mingzhecong/Documents/GitHub/odata_server-java/src/main/java/com/opendev/odata/data/MORTGAGE30US.csv");
		EntityCollection ratesCollection = new EntityCollection();
		// check for which EdmEntitySet the data is requested
		if (Constants.ES_RATES_NAME.equals(edmEntitySetName)) {
			List<Entity> ratesList = ratesCollection.getEntities();
			String dateTime = "DateTime";
			String mortgage30Year = "Mortgage30Year";
			for (String[] values : csvData) {
				final Entity e1 = new Entity()
						.addProperty(new Property("String", dateTime, ValueType.PRIMITIVE, values[0]))
						.addProperty(new Property("String", mortgage30Year, ValueType.PRIMITIVE, values[1]));
				ratesList.add(e1);
			}
		}
		return ratesCollection;
	}

	public EntityCollection readGetRatesByYearImportCollection(final UriResourceFunction uriResourceFunction,
															   final ServiceMetadata serviceMetadata) throws ODataApplicationException {

		if (FUNCTION_GET_RATES_BY_YEAR.equals(uriResourceFunction.getFunctionImport().getName())) {
			// Get the parameter of the function
			final UriParameter parameterYear = uriResourceFunction.getParameters().get(0);
			// Try to convert the parameter to an Integer.
			// We have to take care, that the type of parameter fits to its EDM declaration
			int year;
			try {
				year = Integer.parseInt(parameterYear.getText());
			} catch (NumberFormatException e) {
				throw new ODataApplicationException("Type of parameter Year must be Edm.Int32", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
			}

			final List<Entity> resultEntityList = new ArrayList<Entity>();

			// Loop over all categories and check how many products are linked
			final EntityCollection rates = getData(ES_RATES_NAME);
			boolean firstRow=true;
			for (final Entity each_rate : rates) {
				if (firstRow==true){
					firstRow=false;
					continue;
				}
				String datetime=each_rate.getProperty("DateTime").getValue().toString();
				if (Integer.parseInt(datetime.split(("-"))[0])==year) {
					resultEntityList.add(each_rate);
				}
			}

			final EntityCollection resultCollection = new EntityCollection();
			resultCollection.getEntities().addAll(resultEntityList);
			return resultCollection;
		} else {
			throw new ODataApplicationException("Function not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ROOT);
		}
	}
}