package com.mozu.base.handlers;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mozu.api.ApiContext;
import com.mozu.api.ApiException;
import com.mozu.api.MozuApiContext;
import com.mozu.api.resources.platform.entitylists.EntityResource;
import com.mozu.api.utils.JsonUtils;
import com.mozu.base.models.AppInfo;
import com.mozu.base.models.EntityCollection;
import com.mozu.base.utils.ApplicationUtils;

public class EntityHandler<TObj> {
	private static final Logger logger = LoggerFactory.getLogger(EntityHandler.class);
	private ObjectMapper mapper = JsonUtils.initObjectMapper();
	private AppInfo appInfo = null;
	
	private Class<TObj> targetClass;
	
	public EntityHandler(Class<TObj> c) {
		targetClass = c;
		appInfo = ApplicationUtils.getAppInfo();
	}
	
	
	public TObj upsertEntity(Integer tenantId, String entityName, String id,TObj obj) throws Exception {
	    return upsertEntity(new MozuApiContext(tenantId), entityName, id, obj);
	}
    public TObj upsertEntity(ApiContext apiContext, String entityName, String id,TObj obj) throws Exception {
	    
		ObjectNode node = mapper.valueToTree(obj);
		String entityNameFQN = getEntityFQN(entityName);
		
		// Add the mapping entry
		EntityResource entityResource = new EntityResource(apiContext);
		try {

			TObj existing = (TObj) getEntity(apiContext, entityNameFQN, id);
			
			if (existing == null) {
				entityResource.insertEntity(node, entityNameFQN);
			} else {
				entityResource.updateEntity(node, entityNameFQN, id);
			}
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + id);
			throw e;
		}
		
		return obj;
	}
	
    public void deleteEntity(Integer tenantId, String entityName, String id) throws Exception {
        deleteEntity(new MozuApiContext(tenantId), entityName, id);
    }

	public void deleteEntity(ApiContext apiContext, String entityName, String id) throws Exception {
		String entityNameFQN = getEntityFQN(entityName);
		// Add the mapping entry
		EntityResource entityResource = new EntityResource(apiContext);
		try {
			entityResource.deleteEntity(entityNameFQN, id);
		} catch (Exception e) {
			logger.error("Error saving or updating  entity : " + id);
			throw e;
		}
	}

	public TObj getEntity(Integer tenantId, String entityName, String id) throws Exception {
	    return getEntity(new MozuApiContext(tenantId), entityName, id);
	}
	
	public TObj getEntity(ApiContext apiContext, String entityName, String id) throws Exception {
		EntityResource entityResource = new EntityResource(apiContext);
		JsonNode entity = null;
		TObj returnValue = null;
		String entityNameFQN = getEntityFQN(entityName);
		try {
			JavaType type = mapper.getTypeFactory().constructType(targetClass);
			entity = entityResource.getEntity(entityNameFQN, id);
			if (entity!=null) {
			    returnValue = mapper.readValue(entity.toString(), type);
			}
		} catch (ApiException e) {
			if (e.getApiError() == null || !StringUtils.equals(e.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND")) {
				logger.error("Error retrieving entity for email id: " + entity);
				throw e;
			}
		}
		return returnValue;
	}

	public EntityCollection<TObj> getEntityCollection(Integer tenantId,String entityName) throws Exception {
		return getEntityCollection(tenantId, entityName, null, null, null, null);
	}

	
    public EntityCollection<TObj> getEntityCollection(Integer tenantId,String entityName, 
			String filterCriteria, String sortBy,Integer startIndex, Integer pageSize) throws Exception {
	    return getEntityCollection(new MozuApiContext(tenantId), entityName, filterCriteria, sortBy, startIndex, pageSize);
	}
	
    @SuppressWarnings("unchecked")
    public EntityCollection<TObj> getEntityCollection(ApiContext apiContext ,String entityName, 
	            String filterCriteria, String sortBy,Integer startIndex, Integer pageSize) throws Exception {
	    
		EntityResource entityResource = new EntityResource(apiContext);
		EntityCollection<TObj> collection = null;
		String entityNameFQN = getEntityFQN(entityName);
		try {
			if(startIndex == null) {
				startIndex = 0;
			}
			
			com.mozu.api.contracts.mzdb.EntityCollection jNodeCollection = 
					entityResource.getEntities(entityNameFQN, pageSize, startIndex,
												filterCriteria, sortBy, null);
			
			collection = new EntityCollection<TObj>();
			if (jNodeCollection != null) {
    			collection.setPageCount(jNodeCollection.getPageCount());
    			collection.setPageSize(jNodeCollection.getPageSize());
    			collection.setStartIndex(jNodeCollection.getStartIndex());
    			collection.setTotalCount(jNodeCollection.getTotalCount());
    			JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, targetClass);
    			ArrayList<TObj> items = (ArrayList<TObj>)mapper.readValue(jNodeCollection.getItems().toString(), type); 
    			collection.setItems(items);
			} else {
                collection.setPageCount(0);
                collection.setPageSize(pageSize);
                collection.setStartIndex(0);
                collection.setTotalCount(0);
                collection.setItems(new ArrayList<TObj>());
			}
		} catch (ApiException e) {
			if (e.getApiError() == null || !StringUtils.equals(e.getApiError().getErrorCode(),
					"ITEM_NOT_FOUND")) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}

		return collection;
	}
	
	private String getEntityFQN(String entityName) {
		if (entityName.indexOf("@") > -1) return entityName;
		else return entityName+"@"+appInfo.getNameSpace();
	}
}
