

# ms-reservations
Microservice that manages reservations of the volcano Island

## Run application

### With H2 database
```
./gradlew bootRun (WINDOWS)
```

```
./gradlew.bat bootRun (Linux)
```

### With Sql server database installed locally

Before running the spring application execute on the local database the DDL
of the database model, the script "model.sql" file that is in the root of the
project contains the DDL instructions.

```
./gradlew bootRun --args='--spring.profiles.active=mssql' (WINDOWS)
```

```
./gradlew.bat bootRun --args='--spring.profiles.active=mssql' (Linux)
```

## STACK
Java 11
Spring boot
Groovy + Spock + TestContainers -> Tests
SQL Server database.


## REST API Documentation

http://localhost:8080/swagger-ui.html

