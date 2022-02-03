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
GET http://localhost:8081/aggregation?pricing=NL,FR,CA,RU,IT&track=109347263,109347261,109347265,109347254,109147261&shipments=109347263,109347261,109347265,109347254,109147261

```
## Response sample:
```http resposne
{
    "pricing": {
        "RU": 44.456929691798784,
        "IT": 7.84373746982453,
        "FR": 71.6310942152352,
        "CA": 50.26114196996825,
        "NL": 68.54581134776295
    },
    "track": {
        "109347263": "NEW",
        "109347261": "DELIVERING",
        "109347254": "DELIVERED",
        "109347265": "DELIVERING",
        "109147261": "COLLECTING"
    },
    "shipments": {
        "109347263": [
            "pallet",
            "envelope",
            "box"
        ],
        "109347261": [
            "pallet"
        ],
        "109347254": [
            "box",
            "box",
            "pallet",
            "box"
        ],
        "109347265": [
            "box",
            "envelope",
            "pallet",
            "envelope",
            "box"
        ],
        "109147261": [
            "envelope"
        ]
    }
}
```
