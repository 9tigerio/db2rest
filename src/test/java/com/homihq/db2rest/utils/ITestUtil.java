package com.homihq.db2rest.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ITestUtil {

    /* CREATE film */

    public final String CREATE_FILM_REQUEST = """ 
                   {
                   "title" : "Dunki",
                    "description" : "Film about illegal immigration" ,
                    "release_year" : 2023,
                    "language_id" : 1,
                    "original_language_id" : null,
                    "rental_duration" : 6,
                    "rental_rate" : 0.99 ,
                    "length" : 150,
                    "replacement_cost" : 20.99 ,
                    "rating" : "PG-13" ,
                    "special_features" : "Commentaries"
            	
            }
            """;

    public final String BULK_CREATE_FILM_REQUEST = """ 
             [
                   {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration" ,
                        "release_year" : 2023,
                        "language_id" : 6,
                        "original_language_id" : 6,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
            	
                    },
                    
                   {
                       "title" : "Jawan",
                        "description" : "Socio-econmic problems and corruption" ,
                        "release_year" : 2023,
                        "language_id" : 6,
                        "original_language_id" : 6,
                        "rental_duration" : 5,
                        "rental_rate" : 0.99 ,
                        "length" : 160,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
            	
                    }
                ]
            """;

    public final String BULK_CREATE_FILM_BAD_REQUEST = """ 
             [
                   {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration" ,
                        "release_year" : 2023,
                        "language_id" : 6,
                        "original_language_id" : 6,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries",
                        "country" : "INDIA"
            	
                    },
                    
                   {
                       "title" : "Jawan",
                        "description" : "Socio-econmic problems and corruption" ,
                        "release_year" : 2023,
                        "language_id" : 6,
                        "original_language_id" : 6,
                        "rental_duration" : 5,
                        "rental_rate" : 0.99 ,
                        "length" : 160,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
            	
                    }
                ]
            """;

    public final String CREATE_FILM_REQUEST_CSV = """
            title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features
            Dunki2,Film about illegal immigration,2023,6,6,6,0.99,150,20.99,PG-13,Commentaries
            Jawan2,Socio-econmic problems and corruption,2023,6,6,6,0.99,160,20.99,PG-13,Commentaries      
                     """;

    public final String CREATE_FILM_BAD_REQUEST_CSV = """
            title,description,release_year,language_id,original_language_id,rental_duration,rental_rate,length,replacement_cost,rating,special_features,country
            Dunki2,Film about illegal immigration,2023,6,6,6,0.99,150,20.99,PG-13,Commentaries,India
            Jawan2,Socio-econmic problems and corruption,2023,6,6,6,0.99,160,20.99,PG-13,Commentaries,India      
                     """;


    /* Update film */

    public final String UPDATE_FILM_REQUEST = """
            {
                "rating": "NC-17"
            }
            """;

    public final String UPDATE_NON_EXISTING_FILM_REQUEST = """
            {
                "special_features": "CGI,Fantasy,Action"
            }
            """;

    public final String UPDATE_NON_EXISTING_TABLE = """
            {
                "sample_col": "sample value"
            }
            """;

    public final String UPDATE_FILMS_REQUEST = """
            {
                "rating": "PG"
            }
            """;

    /* CREATE director */

    public final String CREATE_DIRECTOR_REQUEST = """
            {
                "first_name": "Rohit",
                "last_name": "Shetty"
            }
            """;

    public final String BULK_CREATE_DIRECTOR_REQUEST = """
            [
               {
                  "first_name": "Anurag",
                  "last_name": "Kashyap"
               },
               {
                  "first_name": "Rajkumar",
                  "last_name": "Hirani"
               } 
            ]
            """;

    public final String BULK_CREATE_DIRECTOR_BAD_REQUEST = """
            [
               {
                  "first_name": "Anurag",
                  "last_name": "Kashyap"
               },
               {
                  "first_name": "Rajkumar",
                  "last_name": "Hirani"
               } 
            ]
            """;


    /* CREATE review */

    public final String BULK_CREATE_REVIEW_REQUEST = """
            [
               {
                  "message": "Very long film.",
                  "rating": 3,
                  "film_id" : 1
               },
               {
                  "message": "Very exciting film.",
                  "rating": 4,
                  "film_id" : 2
               } 
            ]
            """;


    /* Query Actor */

    public final String SINGLE_RESULT_ACTOR_QUERY = """ 
                   {
                         "sql": "SELECT FIRST_NAME,LAST_NAME FROM ACTOR WHERE ACTOR_ID = :id",
                         "params" : {
                             "id" : 1
                         },
                         "single" : true
                     }
            """;

    public final String BULK_RESULT_ACTOR_QUERY = """ 
                   {
                       "sql": "SELECT FIRST_NAME,LAST_NAME FROM ACTOR WHERE ACTOR_ID IN (:id1, :id2)",
                       "params" : {
                           "id1" : 1,
                           "id2" : 2
                       },
                       "single" : false
                   }
            """;

    public final String EMPTY_ACTOR_QUERY = """ 
                   {
                       "sql": "",
                       "params" : {
                           "id1" : 1
                       },
                       "single" : false
                   }
            """;

}
