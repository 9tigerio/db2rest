db = db.getSiblingDB('SampleCollections');

db.createCollection('Sakila_actors');

db.createUser(
  {
    user: "admin",
    pwd: "admin",
    roles: [
      { role: "readWrite", db: "SampleCollections" }
    ]
  }
)

db.Sakila_actors.insertOne({
    "FirstName" : "PENELOPE",
    "LastName" : "GUINESS",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName" : "NICK",
    "LastName" : "WAHLBERG",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName" : "ED",
    "LastName" : "CHASE",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName" : "JENNIFER",
    "LastName" : "DAVIS",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName" : "JOHNNY",
    "LastName" : "LOLLOBRIGIDA",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName" : "BETTE",
    "LastName" : "NICHOLSON",
    "phone" : "XXX-XXXX-XXX",
    "address" : "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName": "JULIA",
    "LastName": "ZELLWEGER",
    "phone": "XXX-XXXX-XXX",
    "address": "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

db.Sakila_actors.insertOne({
    "FirstName": "MINNIE",
    "LastName": "ZELLWEGER",
    "phone": "XXX-XXXX-XXX",
    "address": "XXXXXXXXXXXXXXXXXXXXXXXXXXXX"
});

