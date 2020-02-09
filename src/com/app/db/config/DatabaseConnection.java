package com.app.db.config;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- DATABASE CONNECTION VARIABLES              --- **/
	/** -------------------------------------------------- **/
    
	private static final String MONGO_DATABASE_NAME =			"appDB";
	private static final CodecRegistry POJO_CODEC_REGISTRY =		fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	private static final MongoClientSettings MONGO_DATABASE_SETTINGS =	MongoClientSettings.builder().codecRegistry(POJO_CODEC_REGISTRY).build();
	private static final MongoClient MONGO_CLIENT =				MongoClients.create(MONGO_DATABASE_SETTINGS);
	
	public static final MongoDatabase MONGO_DATABASE =			MONGO_CLIENT.getDatabase(MONGO_DATABASE_NAME);
}
