package com.app.db.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.IndexOptions;

import com.app.db.constant.DatabaseCollection;
import com.app.db.model.User;
import com.app.db.queryaggregate.PipelineBuilder;
import com.app.db.queryresult.UserQueryResult;

public class UserDAO
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- DATABASE COLLECTION                        --- **/
	/** -------------------------------------------------- **/
    
	private static final MongoCollection<User> DB_COLLECTION = DatabaseCollection.USER;
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- INDEXES                                    --- **/
	/** -------------------------------------------------- **/
    
	public static void setupIndexes()
	{
		DB_COLLECTION.createIndex(new Document("email", 1), new IndexOptions().unique(true));
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CREATE                                     --- **/
	/** -------------------------------------------------- **/
    
	//Insert a new user document to the database collection and return the id.
	public static ObjectId createOne(User object)
	{
		DB_COLLECTION.insertOne(object);
		return object.getId();
	}
	
	//Insert a list of new user documents to the database collection and return a list of ids.
	public static List<ObjectId> createMany(List<User> objectList)
	{
		DB_COLLECTION.insertMany(objectList);
		List<ObjectId> objectIdList = new ArrayList<ObjectId>();
		for (User object : objectList)
		{
			objectIdList.add(object.getId());
		}
		return objectIdList;
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- READ                                       --- **/
	/** -------------------------------------------------- **/
	
	//Return one user document based on the provided query.
	public static User getOne(Bson query)
	{
		List<User> objectList = getMany(query);
		if (!objectList.isEmpty())
		{
			return getMany(query).get(0);
		}
		return null;
	}
	
	//Return a list of user documents based on the provided query.
	public static List<User> getMany(Bson query)
	{
		return getManyAsQueryResult(query, null, null, null, null, null).getDocuments();
	}
	
	//Return a list of user documents (with additional information to be presented in a results table) based on the provided query.
	public static UserQueryResult getManyAsQueryResult(Bson query, String search, String sortByField, Boolean isDescending, Integer resultsPerPage, Integer currentPage)
	{
		//Construct the aggregation pipeline.
		List<Bson> aggregationPipeline = new ArrayList<Bson>();
		
		
		
		//Create a list of all fields that are valid for the search query.
		List<String> fields = new ArrayList<String>();
		fields.add("firstName");
		fields.add("lastName");
		fields.add("email");
		fields.add("company.companyName");
		
		
		
		//Check when the lookups/unwinds will be required in the pipeline.
		if (search != null || "company.companyName".equals(sortByField))
		{
			//Company lookup/unwind before search/sort.
			
			//Step 1: Match - Query
			//Step 2: *Lookup (Company)
			//Step 3: *Unwind (Company)
			//Step 4: Match - Search
			//Step 5: Facets
			//	Step 5a: Count
			//	Step 5b: Documents - Sort > Skip > Limit
			
			
			
			//Add Steps 1-4 to the pipeline.
			PipelineBuilder.addMatchByQuery(aggregationPipeline, query);
			PipelineBuilder.addLookupOnCompany(aggregationPipeline, "ref_companyId", "company");
			PipelineBuilder.addUnwindOnField(aggregationPipeline, "company");
			PipelineBuilder.addMatchBySearch(aggregationPipeline, fields, search);
			
			
			
			//Construct the count facet (Step 5a).
			List<Bson> countFacet = new ArrayList<Bson>();
			countFacet.add(Aggregates.count("qty"));
			
			
			
			//Construct the document facet (Step 5b).
			//Sort > Skip > Limit
			List<Bson> documentsFacet = new ArrayList<Bson>();
			PipelineBuilder.addSort(documentsFacet, sortByField, isDescending);
			PipelineBuilder.addSkip(documentsFacet, resultsPerPage, currentPage);
			PipelineBuilder.addLimit(documentsFacet, resultsPerPage);
			
			
			
			//Add both facets to the facet list (Step 5).
			List<Facet> facetList = new ArrayList<Facet>();
			facetList.add(new Facet("documents", documentsFacet));
			facetList.add(new Facet("count", countFacet));
			
			//Create the facet aggregate (Step 5).
			Bson facet = Aggregates.facet(facetList);
			
			//Add the facet to the pipeline (Step 5).
			aggregationPipeline.add(facet);
		}
		else
		{
			//Company lookup/unwind at end.
			
			//Step 1: Match - Query
			//Step 2: Match - Search
			//Step 3: Facets
			//	Step 3a: Count
			//	Step 3b: Documents - Sort > Skip > Limit > *Lookup (Company) > *Unwind (Company)
			
			
			
			//Add Steps 1-2 to the pipeline.
			PipelineBuilder.addMatchByQuery(aggregationPipeline, query);
			PipelineBuilder.addMatchBySearch(aggregationPipeline, fields, search);
			
			
			
			//Construct the count facet (Step 3a).
			List<Bson> countFacet = new ArrayList<Bson>();
			countFacet.add(Aggregates.count("qty"));
			
			
			
			//Construct the document facet (Step 3b).
			//Sort > Skip > Limit > *Lookup (Company) > *Unwind (Company)
			List<Bson> documentsFacet = new ArrayList<Bson>();
			PipelineBuilder.addEmptyMatch(documentsFacet);
			PipelineBuilder.addSort(documentsFacet, sortByField, isDescending);
			PipelineBuilder.addSkip(documentsFacet, resultsPerPage, currentPage);
			PipelineBuilder.addLimit(documentsFacet, resultsPerPage);
			PipelineBuilder.addLookupOnCompany(documentsFacet, "ref_companyId", "company");
			PipelineBuilder.addUnwindOnField(documentsFacet, "company");
			
			
			
			//Add both facets to the facet list (Step 3).
			List<Facet> facetList = new ArrayList<Facet>();
			facetList.add(new Facet("documents", documentsFacet));
			facetList.add(new Facet("count", countFacet));
			
			//Create the facet aggregate (Step 3).
			Bson facet = Aggregates.facet(facetList);
			
			//Add the facet to the pipeline (Step 3).
			aggregationPipeline.add(facet);
		}
		
		
		
		//Return the result.
		return DB_COLLECTION.aggregate(aggregationPipeline, UserQueryResult.class).first();
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- UPDATE                                     --- **/
	/** -------------------------------------------------- **/
    
	public static void updateFromQuery(Bson query, Bson update)
	{
		DB_COLLECTION.updateMany(query, update);
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- DELETE                                     --- **/
	/** -------------------------------------------------- **/
    
	public static void deleteFromQuery(Bson query)
	{
		DB_COLLECTION.deleteMany(query);
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- COUNT                                      --- **/
	/** -------------------------------------------------- **/
    
	public static long getNumberOfDocumentsFromQuery()
	{
		return DB_COLLECTION.countDocuments();
	}
	
	public static long getNumberOfDocumentsFromQuery(Bson query)
	{
		return DB_COLLECTION.countDocuments(query);
	}
}