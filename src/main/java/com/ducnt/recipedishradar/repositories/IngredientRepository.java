package com.ducnt.recipedishradar.repositories;

import com.ducnt.recipedishradar.models.Ingredient;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngredientRepository extends MongoRepository<Ingredient, ObjectId> {

}
