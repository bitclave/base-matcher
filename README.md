# Match Making

In BASE platform, offers contain keywords and search requests contain free text search query, matchmaking is the process of matching search requests to offers.

# Implementation
* Runs two jobs to sync offers and search requests from BASE
* In-memory `OfferStore` implementation (skips already stored offers)
* Matching - For every search request match for key-to-key and value-to-value
* If found any matches saved them back to BASE (skips any already saved offers)

# How to Run
* Check `application.properties` for BASE endpoint and for Matcher's public & private key
* If you dont have one generate a public & private keys using [base-tutorial](https://github.com/bitclave/base-tutorial)
* Run using gradle
  ```
  ./gradlew bootRun
  ```
* To run from IntelliJ Idea, make sure you enabled annotation pre-processor (> Settings > Build, Execution, Deployment > Compiler > Annotation Processors)