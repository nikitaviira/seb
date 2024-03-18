# SEB Test assignment

## Stack
#### Backend: ```Spring Boot (JPA, Web, Validation), H2 DB, Feign client, Liquibase, Lombok```
#### Frontend: ```Angular, TypeScript, SCSS```

## How to run

1. Install and run [Docker Desktop](https://docs.docker.com/get-docker/)
2. Run "run-app.sh" script
3. Open "localhost:4200"

## Comments
- History data is "lazily" migrated for each currency when first request to the currency is made
- Latest rates are loaded each night for all currencies
- Feign client with Jackson XML mapper used for API fetching
- API does not have data on most of the currencies. Supported currencies are listed in the 
  [Currency](backend/src/main/java/com/task/seb/util/Currency.java) enum