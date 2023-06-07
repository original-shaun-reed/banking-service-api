# banking-service-api

IntelliJ was used while developing this maven project, using Spring/Spring Boot 3.x and Java 17 and H2 DB

When cloning this project and opening in the IDE, please make sure that the maven dependencies are imported, when that is done you may run the project
***
##NB: I made use of the LOMBOK plugin to save myself time from unnecessary creation of getters and setters in my POJOs. Please install that plugin if it doesn't pull through from the **pom.xml** file when importing maven dependencies  
***

When running this project the default host will be  **http://localhost:8087** 
A database will be created on successful startup with two Tables called **Account** as well as **TransactionHistory**

### To access the H2DB
Run **http://localhost:8086/h2-console** in your browser (The login information is default, meaning that username = sa and password is empty)
Once logged in, you can query from the TABLES


### *When the project is in runtime;*
<br />There are 7 (3 for savings account/3 for current account/1 for tranfer) end points that exists in this API that has their unique request and response bodies with a ***Content-Type: application/json*** header when making use of these APIs as well as more than **40 Unit tests with 100% coverage on services**
***
***

### *Example when opening account (Savings Account):*

POST
<br />**http://localhost:8087/api/savings/v1/open**

Request body:
<br />**{
    "accountHolderName": "TEST",
    "accountHolderSurname": "TEST",
    "accountType": "SAVINGS",
    "accountNumber": 1234567890,
    "amountToDeposit": 2000
}**

Response body:
<br />**{
    "accountHolderName": "TEST",
    "accountHolderSurname": "TEST",
    "accountNumber": 1234567890,
    "accountType": "SAVINGS",
    "accountBalance": 2000.0
}**
***

### *Example deposit to an account (Savings Account):*

POST
<br />**http://localhost:8087/api/savings/v1/deposit**

Request body:
<br />**{
    "accountNumber": 1234567890,
    "depositAmount": 2000
}**

Response body:
<br />**{
    "accountNumber": 1234567890,
    "accountType": "SAVINGS",
    "previousAccountBalance": 9000.0,
    "accountBalance": 11000.0
}**
***

### *Example deposit to an account (Savings Account) with mandatory fields missing (Response with validation), handled by the rest controller advice:*

POST
<br />**http://localhost:8087/api/savings/v1/deposit**

Request body:
<br />**{
    "accountNumber": 1234567890,
    "depositAmount": 0
}**

Response body:
<br />**{
    "errors": [
        {
            "fieldName": "depositAmount",
            "message": "Deposit amount can't be 0"
        },
        {
            "fieldName": "depositAmount",
            "message": "Amount can't be a negative number"
        }
    ]
}**
***

### *Example withdrawal to an account (Savings Account):*

POST
<br />**http://localhost:8087/api/savings/v1/withdrawal**

Request body:
<br />**{
    "accountNumber": 1234567890,
    "withdrawalAmount": 500
}**

Response body:
<br />**{
    "accountNumber": 1234567890,
    "accountType": "SAVINGS",
    "previousAccountBalance": 1500.0,
    "accountBalance": 1000.0
}**
***

### *Example transfer from a Current Account to a Savings Account:*

POST
<br />**http://localhost:8087/api/savings/v1/withdrawal**

Request body:
<br />**{
    "fromAccountNumber": 1234567898,
    "toAccountNumber": 1234567890,
    "transferAmount": 46000
}**

Response body:
<br />**{
    "message": "Transfer from 1234567898 to 1234567890 with the amount of R46000.0 was successful"
}**
***

### Note: I've also added a docker file as well as compose configuration to mimic an actual live/external environment when deployed
<br />**To run this api in a docker container, simply access the the project directory though cmd/terminal and type out "docker-compose up --build" (Just make sure that you have docker installed on your machine)**