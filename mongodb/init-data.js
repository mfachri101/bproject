db = db.getSiblingDB('mydatabase');

// init data e.g. airports and airlines
db.airport.insertMany(require('/docker-entrypoint-initdb.d/airports.json'));
db.airlines.insertMany(require('/docker-entrypoint-initdb.d/airlines.json'));

// init indexes
db.flightItineraries.createIndex({ routeKey: 1 });