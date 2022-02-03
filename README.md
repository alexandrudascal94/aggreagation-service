# aggregation-service

## Prerequisites

1. Docker, docker-compose
2. Java 11
3. mvn 

## How to run

```maven
mvn clean install
docker-compose up -d
mvn spring-boot:run 
```
The service will run on default port 8081 
## Request sample

```http request
GET http://localhost:8081/aggregation?pricing=NL,FR,CA,NL,IT&track=109347263,109347261,109347265,109347254,109147261&shipments=109347263,109347261,109347265,109347254,109147261
```
## Response sample:
```http resposne
{
    "pricing": {
        "IT": 89.82910788628968,
        "FR": 31.653950273520792,
        "CA": 73.34027687659636,
        "NL": 23.01410302394469
    },
    "track": {
        "109347263": "DELIVERING",
        "109347261": "IN TRANSIT",
        "109347254": "NEW",
        "109347265": "NEW",
        "109147261": "COLLECTED"
    },
    "shipments": {
        "109347263": [
            "pallet",
            "pallet",
            "pallet"
        ],
        "109347261": [
            "box"
        ],
        "109347254": [
            "box",
            "box",
            "envelope",
            "box"
        ],
        "109347265": [
            "box",
            "pallet",
            "box",
            "envelope",
            "pallet"
        ],
        "109147261": [
            "pallet"
        ]
    }
}
```
