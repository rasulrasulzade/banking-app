// run db and migrations <br>
docker-compose up

run spring app <br>
./gradlew bootRun 

run tests <br>
./gradlew test

POST /customers <br>
Example payload: <br> {
"name": "Tyler Durden"
}

POST /{customerId}/bankAccounts <br>
Example payload: <br> {
"currency":"USD",
"amount":10000
} <br>

GET /{customerId}/bankAccounts <br>
GET /bankAccounts/{accountNumber} <br>

POST /transfers <br>
Example payload: <br> {
"receiverAccountNumber": "ABC1692266759779",
"senderAccountNumber":"ABC1692266870116",
"amount": "900"
}

GET /transfers/{accountNumber}