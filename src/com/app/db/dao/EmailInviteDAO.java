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
import com.app.db.model.EmailInvite;
import com.app.db.queryaggregate.PipelineBuilder;
import com.app.db.queryresult.EmailInviteQueryResult;

public class EmailInviteDAO
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- DATABASE COLLECTION                        --- **/
	/** -------------------------------------------------- **/
    
	private static final MongoCollection<EmailInvite> DB_COLLECTION = DatabaseCollection.EMAIL_INVITE;
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- INDEXES                                    --- **/
	/** -------------------------------------------------- **/
    
	public static void setupIndexes()
	{
		DB_COLLECTION.createIndex(new Document("token", 1), new IndexOptions().unique(true));
		DB_COLLECTION.createIndex(new Document("ref_userId", 1), new IndexOptions().unique(true));
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CREATE                                     --- **/
	/** -------------------------------------------------- **/
    
	//Insert a new email invite document to the database collection and return the id.
	public static ObjectId createOne(EmailInvite object)
	{
		DB_COLLECTION.insertOne(object);
		return object.getId();
	}
	
	//Insert a list of new email invite documents to the database collection and return a list of ids.
	public static List<ObjectId> createMany(List<EmailInvite> objectList)
	{
		DB_COLLECTION.insertMany(objectList);
		List<ObjectId> objectIdList = new ArrayList<ObjectId>();
		for (EmailInvite object : objectList)
		{
			objectIdList.add(object.getId());
		}
		return objectIdList;
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- READ                                       --- **/
	/** -------------------------------------------------- **/
	
	//Return one email invite document based on the provided query.
	public static EmailInvite getOne(Bson query)
	{
		List<EmailInvite> objectList = getMany(query);
		if (!objectList.isEmpty())
		{
			return getMany(query).get(0);
		}
		return null;
	}
	
	//Return a list of email invite documents based on the provided query.
	public static List<EmailInvite> getMany(Bson query)
	{
		return getManyAsQueryResult(query, null, null, null, null, null).getDocuments();
	}
	
	//Return a list of email invite documents (with additional information to be presented in a results table) based on the provided query.
	public static EmailInviteQueryResult getManyAsQueryResult(Bson query, String search, String sortByField, Boolean isDescending, Integer resultsPerPage, Integer currentPage)
	{
		//Construct the aggregation pipeline.
		List<Bson> aggregationPipeline = new ArrayList<Bson>();
		
		
		
		//Create a list of all fields that are valid for the search query.
		List<String> fields = new ArrayList<String>();
		fields.add("user.firstName");
		fields.add("user.lastName");
		fields.add("user.email");
		fields.add("userSentBy.firstName");
		fields.add("userSentBy.lastName");
		
		
		
		//Check when the lookups/unwinds will be required in the pipeline.
		if (search != null || "user.lastName".equals(sortByField) || "user.email".equals(sortByField) || "userSentBy.lastName".equals(sortByField))
		{
			//User & UserSentBy lookup/unwind before search/sort.
			
			//Step 1: Match - Query
			//Step 2: *Lookup (User)
			//Step 3: *Unwind (User)
			//Step 4: *Lookup (User.Company)
			//Step 5: *Unwind (User.Company)
			//Step 6: *Lookup (UserSentBy)
			//Step 7: *Unwind (UserSentBy)
			//Step 8: *Lookup (UserSentBy.Company)
			//Step 9: *Unwind (UserSentBy.Company)
			//Step 10: Match - Search
			//Step 11: Facets
			//	Step 11a: Count
			//	Step 11b: Documents - Sort > Skip > Limit
			
			
			
			//Add Steps 1-10 to the pipeline.
			PipelineBuilder.addMatchByQuery(aggregationPipeline, query);
			PipelineBuilder.addLookupOnUser(aggregationPipeline, "ref_userId", "user");
			PipelineBuilder.addUnwindOnField(aggregationPipeline, "user");
			PipelineBuilder.addLookupOnCompany(aggregationPipeline, "user.ref_companyId", "user.company");
			PipelineBuilder.addUnwindOnField(aggregationPipeline, "user.company");
			PipelineBuilder.addLookupOnUser(aggregationPipeline, "ref_userSentById", "userSentBy");
			PipelineBuilder.addUnwindOnField(aggregationPipeline, "userSentBy");
			PipelineBuilder.addLookupOnCompany(aggregationPipeline, "userSentBy.ref_companyId", "userSentBy.company");
			PipelineBuilder.addUnwindOnField(aggregationPipeline, "userSentBy.company");
			PipelineBuilder.addMatchBySearch(aggregationPipeline, fields, search);
			
			
			
			//Construct the count facet (Step 11a).
			List<Bson> countFacet = new ArrayList<Bson>();
			countFacet.add(Aggregates.count("qty"));
			
			
			
			//Construct the document facet (Step 11b).
			//Sort > Skip > Limit
			List<Bson> documentsFacet = new ArrayList<Bson>();
			PipelineBuilder.addSort(documentsFacet, sortByField, isDescending);
			PipelineBuilder.addSkip(documentsFacet, resultsPerPage, currentPage);
			PipelineBuilder.addLimit(documentsFacet, resultsPerPage);
			
			
			
			//Add both facets to the facet list (Step 11).
			List<Facet> facetList = new ArrayList<Facet>();
			facetList.add(new Facet("documents", documentsFacet));
			facetList.add(new Facet("count", countFacet));
			
			//Create the facet aggregate (Step 11).
			Bson facet = Aggregates.facet(facetList);
			
			//Add the facet to the pipeline (Step 11).
			aggregationPipeline.add(facet);
		}
		else
		{
			//User & UserSentBy lookup/unwind at end.
			
			//Step 1: Match - Query
			//Step 2: Match - Search
			//Step 3: Facets
			//	Step 3a: Count
			//	Step 3b: Documents - Sort > Skip > Limit > *Lookup (User) > *Unwind (User) > *Lookup (User.Company) > *Unwind (User.Company) > *Lookup (UserSentBy) > *Unwind (UserSentBy) > *Lookup (UserSentBy.Company) > *Unwind (UserSentBy.Company)
			
			
			
			//Add Steps 1-2 to the pipeline.
			PipelineBuilder.addMatchByQuery(aggregationPipeline, query);
			PipelineBuilder.addMatchBySearch(aggregationPipeline, fields, search);
			
			
			
			//Construct the count facet (Step 3a).
			List<Bson> countFacet = new ArrayList<Bson>();
			countFacet.add(Aggregates.count("qty"));
			
			
			
			//Construct the document facet (Step 3b).
			//Sort > Skip > Limit > *Lookup (User) > *Unwind (User) > *Lookup (User.Company) > *Unwind (User.Company) > *Lookup (UserSentBy) > *Unwind (UserSentBy) > *Lookup (UserSentBy.Company) > *Unwind (UserSentBy.Company)
			List<Bson> documentsFacet = new ArrayList<Bson>();
			PipelineBuilder.addEmptyMatch(documentsFacet);
			PipelineBuilder.addSort(documentsFacet, sortByField, isDescending);
			PipelineBuilder.addSkip(documentsFacet, resultsPerPage, currentPage);
			PipelineBuilder.addLimit(documentsFacet, resultsPerPage);
			PipelineBuilder.addLookupOnUser(documentsFacet, "ref_userId", "user");
			PipelineBuilder.addUnwindOnField(documentsFacet, "user");
			PipelineBuilder.addLookupOnCompany(documentsFacet, "user.ref_companyId", "user.company");
			PipelineBuilder.addUnwindOnField(documentsFacet, "user.company");
			PipelineBuilder.addLookupOnUser(documentsFacet, "ref_userSentById", "userSentBy");
			PipelineBuilder.addUnwindOnField(documentsFacet, "userSentBy");
			PipelineBuilder.addLookupOnCompany(documentsFacet, "userSentBy.ref_companyId", "userSentBy.company");
			PipelineBuilder.addUnwindOnField(documentsFacet, "userSentBy.company");
			
			
			
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
		return DB_COLLECTION.aggregate(aggregationPipeline, EmailInviteQueryResult.class).first();
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
}