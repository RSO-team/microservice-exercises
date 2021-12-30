# RSO: Microservice Exercise

Microservice which manages exercise in our service

## Prerequisites

```bash
docker run -d --name pg-exercise -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=exercise -p 5432:5432 postgres:13
```