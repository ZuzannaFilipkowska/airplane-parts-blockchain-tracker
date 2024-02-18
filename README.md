# airplane-parts-blockchain-tracker
MDB (Magazyn Danych Blockchain) Project
Welcome to the MDB project repository, a system designed to revolutionize how information about airplane parts is stored and managed using blockchain technology.

I developed this project as a crucial component of my engineering thesis, focusing on the topic of "Data Storage of Aircraft Parts Maintenance Using Blockchain Technology."

## Features:
- **Blockchain-Powered Storage:** Securely store and manage aviation component data using the robust blockchain infrastructure.
- **Web Application Interface:** Intuitive web application for seamless interaction with the MDB system.
- **Smart Contracts:** Implementing smart contracts for transparent and secure transactions, ensuring data integrity.
- **User Authentication:** Multi-layered user authentication for controlled access and enhanced security.
- **Hyperledger Fabric Integration:** Leveraging the power of Hyperledger Fabric for a trusted and efficient blockchain network.

## Architecture
![Architektura systemu](images/projekt_1.jpg)

Detailed architecture:
![Architektura sieci](images/projekt_2.png)

## BPMN Sale process
![BPMN](images/bpmn.png)

## UI

Login page:
![Login page](images/front_logowanie.png)

View after login:
![Main page](images/front_twoje_czesci.png)

Marketplace:
![Marketplace](images/front_market.png)

## Technologies used:
- **Frontend** - Angular framework
- **Backend** - Java + Spring Bott + Hyperledger Fabric Spring Boot Starter
- **Database** - PostgreSQL
- **Blockchain network** - Private Hyperledger Fabric network. Fabric Gateway was used to access network. Each organization has private data storage component. Smart contracts were written using Go.
  
## Getting Started:

Setting up the network is described in [run.sh](run.sh).
