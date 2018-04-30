Small metadata service which saves/retrieves ApplicationData from a PostgreSQL database.
In order to function correctly you need to create you own .env-file below src/main/resources.
After creation you also need to provide the following parameters:

* dbdriver (org.postgresql.Driver)
* dbname
* user
* password
* hostname
* port
* initialData
* jwk_kid
* jwk_url
* jwk_alg

When started the service provides following paths:

- on port 8080:

|Methd           |Path                           |Description                  |
|----------------|-------------------------------|-----------------------------|
|GET             |`/projects`                    |get all projects             |
|GET             |`/projects/:id`                |get project by Id            |
|POST            |`/projects `                   |create new project           |
|PUT             |`/projects/:id`                |update existing project      |


- on port 8081:

|Methd           |Path                           |Description                  |
|----------------|-------------------------------|-----------------------------|
|GET             |`/health`                      |get all projects             |
|GET             |`/metrics`                     |get project by Id            |
|GET             |`/metrics/dummyParameter`      |create new project           |

