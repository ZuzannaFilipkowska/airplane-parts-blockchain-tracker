Setting up network:
1. Write in terminal:
export PATH=$PATH:/opt/gradle/gradle-8.3/bin

sudo ./network.sh  down
sudo ./network.sh up createChannel -s couchdb -c mychannel
sudo ./network.sh deployCC -ccn secured -ccp ../asset-transfer-private-data/airplane-parts-blockchain-tracker/chaincode/ -ccl go -ccep "OR('Org1MSP.peer','Org2MSP.peer')" 
sudo chmod 777 -R ../*

Setting up SQL database:
docker-compose up --build

Setting up backend REST API:
1. Write in terminal from directory with gradle set up
sudo ./gradlew bootRun

Setting up frontend
1. npm i
2. npm start


################################################################################################################
# Example usage of network from peer terminal 

#Set the environment variables to operate as Org1
export PATH=${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051


#create asset
export ASSET_PROPERTIES=$(echo -n "{\"object_type\":\"asset_properties\",\"partName\":\"czesc 1\",\"partNumber\":\"1\",\"description\":\"czesc\",\"manufacturer\":\"m\",\"length\":\"1\",\"width\":\"1\",\"height\":\"1\",\"status\":\"used\",\"lastInspectionDate\":\"01.01.2001\",\"inspectionPerformedBy\":\"MRO\",\"nextInspectionDate\":\"01.01.2025\",\"lifeLimit\":\"2\",
\"currentUsageTime\":\"1\",\"salt\":\"a94a8fe5ccb19ba61c4c0873d391e987982fbbd3\"}" | base64 | tr -d \\n)

peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c '{"function":"CreateAsset","Args":["A new asset for Org1MSP", "True"]}' --transient "{\"asset_properties\":\"$ASSET_PROPERTIES\"}"


export ASSET_ID=dd7b4d3d0037e893f760609c250508e3d1e6ad79d4ae622c59002f4ba27eb089


# read asset 
# trzeba podstawic id zwrocone przez create
peer chaincode query -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"ReadAsset\",\"Args\":[\"$ASSET_ID\"]}"



# agree to sell
export ASSET_PRICE=$(echo -n "{\"asset_id\":\"$ASSET_ID\",\"trade_id\":\"abcd\",\"price\":100}" | base64 | tr -d \\n)
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"AgreeToSell\",\"Args\":[\"$ASSET_ID\"]}" --transient "{\"asset_price\":\"$ASSET_PRICE\"}"

# get sales price
peer chaincode query -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"GetAssetSalesPrice\",\"Args\":[\"$ASSET_ID\"]}"

# agree to buy

export ASSET_PROPERTIES=$(echo -n "{\"object_type\":\"asset_properties\",\"partName\":\"czesc 1\",\"partNumber\":\"1\",\"description\":\"czesc\",\"manufacturer\":\"m\",\"length\":\"1\",\"width\":\"1\",\"height\":\"1\",\"status\":\"used\",\"lastInspectionDate\":\"01.01.2001\",\"inspectionPerformedBy\":\"MRO\",\"nextInspectionDate\":\"01.01.2025\",\"lifeLimit\":\"2\",
\"currentUsageTime\":\"1\",\"salt\":\"a94a8fe5ccb19ba61c4c0873d391e987982fbbd3\"}" | base64 | tr -d \\n)

peer chaincode query -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"VerifyAssetProperties\",\"Args\":[\"$ASSET_ID\"]}" --transient "{\"asset_properties\":\"$ASSET_PROPERTIES\"}"


peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"AgreeToBuy\",\"Args\":[\"$ASSET_ID\"]}" --transient "{\"asset_price\":\"$ASSET_PRICE\", \"asset_properties\":\"$ASSET_PROPERTIES\"}"

# transfer

peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n secured -c "{\"function\":\"TransferAsset\",\"Args\":[\"$ASSET_ID\",\"Org2MSP\"]}" --transient "{\"asset_price\":\"$ASSET_PRICE\"}" --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"


