package com.homihq.db2rest.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ITestUtil {




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



}
