package pl.wut.airplane.parts.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.*;
import org.springframework.stereotype.Service;
import pl.wut.airplane.parts.storage.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NetworkService {
    // dane kanalu i sieci
    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "secured");

    // dane org 1
    private static final String MSP_ID_Org1 = System.getenv().getOrDefault("MSP_ID", "Org1MSP");
    // Path to crypto materials.
    private static final Path CRYPTO_PATH_Org1 = Paths.get("../../test-network/organizations/peerOrganizations/org1.example.com");
    // Path to user certificate.
    private static final Path CERT_PATH_Org1 = CRYPTO_PATH_Org1.resolve(Paths.get("users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem"));
    // Path to user private key directory.
    private static final Path KEY_DIR_PATH_Org1 = CRYPTO_PATH_Org1.resolve(Paths.get("users/User1@org1.example.com/msp/keystore"));
    // Path to peer tls certificate.
    private static final Path TLS_CERT_PATH_Org1 = CRYPTO_PATH_Org1.resolve(Paths.get("peers/peer0.org1.example.com/tls/ca.crt"));

    // dane org 2
    private static final String MSP_ID_Org2 = System.getenv().getOrDefault("MSP_ID", "Org2MSP");

    // Path to crypto materials.
    private static final Path CRYPTO_PATH_Org2  = Paths.get("../../test-network/organizations/peerOrganizations/org2.example.com");
    // Path to user certificate.
    private static final Path CERT_PATH_Org2 = CRYPTO_PATH_Org2.resolve(Paths.get("users/User1@org2.example.com/msp/signcerts/User1@org2.example.com-cert.pem"));
    // Path to user private key directory.
    private static final Path KEY_DIR_PATH_Org2  = CRYPTO_PATH_Org2.resolve(Paths.get("users/User1@org2.example.com/msp/keystore"));
    // Path to peer tls certificate.
    private static final Path TLS_CERT_PATH_Org2  = CRYPTO_PATH_Org2.resolve(Paths.get("peers/peer0.org2.example.com/tls/ca.crt")); // czy jest peer0?

    // Gateway peer end point.
    private static final String PEER_ENDPOINT_Org1 = "localhost:7051";
    private static final String PEER_ENDPOINT_Org2 = "localhost:9051";

    private static final String OVERRIDE_AUTH_Org1 = "peer0.org1.example.com";
    private static final String OVERRIDE_AUTH_Org2 = "peer0.org2.example.com";

    private final Contract contractOrg1;
    private final Contract contractOrg2;

    private final String assetId = "asset" + Instant.now().toEpochMilli(); // @TODO REFACTOR
    
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public NetworkService() throws CertificateException, IOException, InvalidKeyException, EndorseException, CommitException, SubmitException, CommitStatusException {
        // The gRPC client connection should be shared by all Gateway connections
        var channelOrg1 = newGrpcConnection(TLS_CERT_PATH_Org1, PEER_ENDPOINT_Org1, OVERRIDE_AUTH_Org1);

        var builderOrg1 = Gateway.newInstance().identity(newIdentity(CERT_PATH_Org1, MSP_ID_Org1)).signer(newSigner(String.valueOf(KEY_DIR_PATH_Org1))).connection(channelOrg1)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(10, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(10, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(5, TimeUnit.MINUTES));

        try (var gatewayOrg1 = builderOrg1.connect()) {
            // Get a network instance representing the channel where the smart contract is deployed.
            var networkOrg1 = gatewayOrg1.getNetwork(CHANNEL_NAME);

            // Get the smart contract from the network.
            contractOrg1 = networkOrg1.getContract(CHAINCODE_NAME);

            // Initialize a set of asset data on the ledger using the chaincode 'InitLedger' function.
            initLedger();
        } finally {
            // kiedy powinnam posprzatac????
            //channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }

        var channelOrg2 = newGrpcConnection(TLS_CERT_PATH_Org2, PEER_ENDPOINT_Org2, OVERRIDE_AUTH_Org2);

        var builderOrg2 = Gateway.newInstance().identity(newIdentity(CERT_PATH_Org2, MSP_ID_Org2)).signer(newSigner(String.valueOf(KEY_DIR_PATH_Org2))).connection(channelOrg2)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        try (var gatewayOrg2 = builderOrg2.connect()) {
            // Get a network instance representing the channel where the smart contract is deployed.
            var networkOrg2 = gatewayOrg2.getNetwork(CHANNEL_NAME);

            // Get the smart contract from the network.
            contractOrg2 = networkOrg2.getContract(CHAINCODE_NAME);

            // Initialize a set of asset data on the ledger using the chaincode 'InitLedger' function.
            //initLedger();
        } finally {
            // kiedy powinnam posprzatac????
            //channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Submit a transaction synchronously, blocking until it has been committed to
     * the ledger.
     */
    String createAsset(String publicDescription, Boolean isForSale, AssetPrivateData privateData) throws GatewayException, CommitException {
        System.out.println("\n--> Submit Transaction: CreateAsset, creates new asset");

        AssetPropertiesJSON assetPropertiesJSON = new AssetPropertiesJSON("asset_properties", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", privateData.getPartName(),
                privateData.getPartNumber(), privateData.getDescription(), privateData.getManufacturer(), privateData.getLength(), privateData.getWidth(), privateData.getHeight(), privateData.getStatus(),
                privateData.getLastInspectionDate(), privateData.getInspectionPerformedBy(), privateData.getNextInspectionDate(), privateData.getLifeLimit(), privateData.getCurrentUsageTimes());

        String endorsingOrg = MSP_ID_Org1;

        byte[] resultBytes = contractOrg1.newProposal("CreateAsset")
                .addArguments(publicDescription.getBytes(), isForSale.toString().getBytes())
                .putTransient("asset_properties",  toJson(assetPropertiesJSON))
                .setEndorsingOrganizations(endorsingOrg)
                .build()
                .endorse()
                .submit();

        String assetID = prettyJson(resultBytes); // decode to utf

        System.out.println(String.format("id: %s", assetID));

        return assetID;
    }


    byte[] readAssetById(String assetId) throws GatewayException {
        System.out.println(String.format("\n--> Evaluate Transaction: ReadAsset, function returns asset with id: %s attributes", assetId));
        var evaluateResult = contractOrg1.evaluateTransaction("ReadAsset", assetId);

        System.out.println("*** Result:" + prettyJson(evaluateResult));

        return evaluateResult;
    }

    byte[] readAssetDetailsById(String assetId) throws GatewayException {
        System.out.println(String.format("\n--> Evaluate Transaction: GetAssetPrivateProperties, function returns private asset with id: %s attributes", assetId));

        var evaluateResult = contractOrg1.evaluateTransaction("GetAssetPrivateProperties", assetId);

       // System.out.println("*** Result:" + prettyJson(evaluateResult));

        return evaluateResult;
    }


    List<Asset> getAllAssetsForSale() throws GatewayException {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Asset>>(){}.getType();
        List<Asset> objects = gson.fromJson(prettyJson(getAllAssets()), listType);
        List<Asset> result = objects.stream().filter(asset -> asset.getIsForSale()).collect(Collectors.toList());
        return result;
    }


    void setAssetForSale(String id) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        System.out.println("\n--> Submit Transaction: setForSale");

        String endorsingOrg = MSP_ID_Org1;

        contractOrg1.newProposal("SetAssetForSale")
                .addArguments(id, String.valueOf(true))
                .setEndorsingOrganizations(endorsingOrg)
                .build()
                .endorse()
                .submit();

        System.out.println("*** Transaction committed successfully");
    }


    void agreeToSell(String id, Long price, String tradeId) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        System.out.println("\n--> Submit Transaction: AgreeToSell");

        AssetPriceJSON assetPriceJSON = new AssetPriceJSON(id, tradeId, price);

        String endorsingOrg = MSP_ID_Org1;

        contractOrg1.newProposal("AgreeToSell")
                .addArguments(id)
                .setEndorsingOrganizations(endorsingOrg)
                .putTransient("asset_price",  toJson(assetPriceJSON))
                .build()
                .endorse()
                .submit();

        System.out.println("*** Transaction committed successfully");
    }

    void verifyAssetProperties(String assetId, AssetProperties assetData) throws GatewayException {

//        int leftLimit = 48; // numeral '0'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = 10;
//        Random random = new Random();
//
//        String salt = random.ints(leftLimit, rightLimit + 1)
//                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
//                .limit(targetStringLength)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();

        AssetPropertiesJSON assetPropertiesJSON = new AssetPropertiesJSON(
            "asset_properties",
                "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3",
             assetData.getPartName(),
              assetData.getPartNumber(),
               assetData.getDescription(),
             assetData.getManufacturer(),
              assetData.getLength(),
               assetData.getWidth(),
                assetData.getHeight(),
                 assetData.getStatus(),
             assetData.getLastInspectionDate(),
              assetData.getInspectionPerformedBy(),
               assetData.getNextInspectionDate(),
                assetData.getLifeLimit(),
                 assetData.getCurrentUsageTimes());


        contractOrg2.newProposal("VerifyAssetProperties")
                .addArguments(assetId)
                .setEndorsingOrganizations(MSP_ID_Org1, MSP_ID_Org2)
                .putTransient("asset_properties", toJson(assetPropertiesJSON))
                .build()
                .endorse()
                .submitAsync();

        System.out.println("*** Transaction committed successfully");
    }

    void agreeToBuy(String tradeId, AgreeToBuyAssetRequest agreeToBuyAssetRequest) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        System.out.println("\n--> Submit Transaction: AgreeToBuy");

        AssetPriceJSON assetPriceJSON = new AssetPriceJSON(agreeToBuyAssetRequest.getAssetId(), tradeId, agreeToBuyAssetRequest.getPrice());

        AssetPropertiesJSON assetPropertiesJSON = new AssetPropertiesJSON("asset_properties", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", agreeToBuyAssetRequest.getPrivateData().getPartName(),
                agreeToBuyAssetRequest.getPrivateData().getPartNumber(), agreeToBuyAssetRequest.getPrivateData().getDescription(), agreeToBuyAssetRequest.getPrivateData().getManufacturer(), agreeToBuyAssetRequest.getPrivateData().getLength(), agreeToBuyAssetRequest.getPrivateData().getWidth(), agreeToBuyAssetRequest.getPrivateData().getHeight(), agreeToBuyAssetRequest.getPrivateData().getStatus(),
                agreeToBuyAssetRequest.getPrivateData().getLastInspectionDate(), agreeToBuyAssetRequest.getPrivateData().getInspectionPerformedBy(), agreeToBuyAssetRequest.getPrivateData().getNextInspectionDate(), agreeToBuyAssetRequest.getPrivateData().getLifeLimit(), agreeToBuyAssetRequest.getPrivateData().getCurrentUsageTimes()); // ?

        String endorsingOrg = MSP_ID_Org2;

        contractOrg2.newProposal("AgreeToBuy")
                .addArguments(assetPriceJSON.getAsset_id())
                .setEndorsingOrganizations(endorsingOrg)
                .putTransient("asset_price",  toJson(assetPriceJSON))
                .putTransient("asset_properties", toJson(assetPropertiesJSON))
                .build()
                .endorse()
                .submit();

        System.out.println("*** Transaction committed successfully");
    }

    /**
     * Evaluate a transaction to query ledger state.
     */
    byte[] getAllAssets() throws GatewayException {
        System.out.println("\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger");

        var result = contractOrg1.evaluateTransaction("GetAllAssets", String.valueOf(new ArrayList<>()));

        System.out.println("*** Result: " + prettyJson(result));

        return result;
    }

    void transferAsset(String assetId, Long price, String tradeId) throws EndorseException, CommitException, SubmitException, CommitStatusException {
        System.out.println("\n--> Submit Transaction: TransferAsset");

        AssetPriceJSON assetPriceJSON = new AssetPriceJSON(assetId, tradeId, price);

        String buyerOrgID = MSP_ID_Org2;

        contractOrg1.newProposal("TransferAsset")
                .addArguments(assetPriceJSON.getAsset_id(), buyerOrgID)
                .setEndorsingOrganizations(MSP_ID_Org1, MSP_ID_Org2)
                .putTransient("asset_price",  toJson(assetPriceJSON))
                .build()
                .endorse()
                .submit();

        System.out.println("*** Transaction committed successfully");
    }

    private void setOrg1Data() {
        // Ogarnac slowniki na zasadzie 1 - , 2
        // zamienic w funkcjach na uzycie na podstawie current i wywolywanie setterow - wysraczt chyba dla samego mspID
//        MSP_ID = MS //MSP_IDS[1]
//
//        private Path CRYPTO_PATH;
//        private Path CERT_PATH;
//        // Path to user private key directory.
//        private Path KEY_DIR_PATH;
//        // Path to peer tls certificate.
//        private Path TLS_CERT_PATH;
    }

    private void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
        System.out.println("\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger");

        //contract.submitTransaction("InitLedger");

        System.out.println("*** Transaction committed successfully");
    }

    private String prettyJson(final byte[] json) {
        return prettyJson(new String(json, StandardCharsets.UTF_8));
    }

    private String prettyJson(final String json) {
        var parsedJson = JsonParser.parseString(json);
        return gson.toJson(parsedJson);
    }

    private String toJson(Object o) {
        return gson.toJson(o);
    }

    private static ManagedChannel newGrpcConnection(Path tlsCertPath, String host, String peerName) throws IOException, CertificateException {
        var tlsCertReader = Files.newBufferedReader(tlsCertPath);
        var tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget(host)
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(peerName)
                .build();
    }

    private static Identity newIdentity(Path certPath, String mspId) throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(certPath);
        var certificate = Identities.readX509Certificate(certReader);

        return new X509Identity(mspId, certificate);
    }

    private static Signer newSigner(String privateKeyDirPath) throws IOException, InvalidKeyException {
        var keyReader = Files.newBufferedReader(getPrivateKeyPath(privateKeyDirPath));
        var privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

    private static Path getPrivateKeyPath(String keyDirPath) throws IOException {
        try (var keyFiles = Files.list(Path.of(keyDirPath))) {
            return keyFiles.findFirst().orElseThrow();
        }
    }
}
