package pl.wut.airplane.parts.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.wut.airplane.parts.storage.model.AssetJSON;
import pl.wut.airplane.parts.storage.model.AssetPrivateData;
import pl.wut.airplane.parts.storage.model.AssetPropertiesJSON;
import pl.wut.airplane.parts.storage.model.CreateAssetRequest;

@RestController
public class NetworkController {
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
  private static final Path CERT_PATH_Org2 = CRYPTO_PATH_Org2.resolve(Paths.get("users/User1@org2.example.com/msp/signcerts/cert.pem"));
  // Path to user private key directory.
  private static final Path KEY_DIR_PATH_Org2  = CRYPTO_PATH_Org1.resolve(Paths.get("users/User1@org2.example.com/msp/keystore"));
  // Path to peer tls certificate.
  private static final Path TLS_CERT_PATH_Org2  = CRYPTO_PATH_Org1.resolve(Paths.get("peers/peer0.org2.example.com/tls/ca.crt")); // czy jest peer0?

  // Gateway peer end point.
  private static final String PEER_ENDPOINT_Org1 = "localhost:7051"; // ??
  private static final String PEER_ENDPOINT_Org2 = "localhost:9051"; // ??

  private static final String OVERRIDE_AUTH_Org1 = "peer0.org1.example.com";
  private static final String OVERRIDE_AUTH_Org2 = "peer0.org2.example.com";


  // nie wiem co to ;)
  private final Contract contract;
  private final String assetId = "asset" + Instant.now().toEpochMilli();
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

  // ta funkcja pobiera dane organizacji
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

  public NetworkController()
      throws CertificateException, IOException, InvalidKeyException, InterruptedException, EndorseException, CommitException, SubmitException, CommitStatusException {
    // The gRPC client connection should be shared by all Gateway connections to
    // this endpoint.
    var channel = newGrpcConnection(TLS_CERT_PATH_Org1, PEER_ENDPOINT_Org1, OVERRIDE_AUTH_Org1);

    var builder = Gateway.newInstance().identity(newIdentity(CERT_PATH_Org1, MSP_ID_Org1)).signer(newSigner(String.valueOf(KEY_DIR_PATH_Org1))).connection(channel)
        // Default timeouts for different gRPC calls
        .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
        .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
        .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
        .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

    // @TODO FLOW: Chaincode mam w Go -> mam gateway tylko w ts -> musze napisac na bazie tego odpowiednie funkcje w springu, to co mam w springu to jest basic asset transfer
    // 1. zmienic nazwy na gateway 1 itd
    // 2.
    // 4. sprobowac wywolad istniejace read one
    // w tym momencie by byla opcja pobierania wszystkich i pojedynczych to juz mozna z tym dzialac
    // 6. zmienic model na lotniczy ;)
    // 7. dalej by byly potrzebne endpointy do spedzay lub edycji
    // 10. posprzatac
    // 11. dane init, readme
    // 12. uzyc serwisu
    // 13. usunac niuzywane klasy

    try (var gateway = builder.connect()) {
      // Get a network instance representing the channel where the smart contract is
      // deployed.
      var network = gateway.getNetwork(CHANNEL_NAME);

      // Get the smart contract from the network.
      contract = network.getContract(CHAINCODE_NAME);

      // Initialize a set of asset data on the ledger using the chaincode 'InitLedger' function.
      initLedger();
    } finally {
        // kiedy powinnam posprzatac????
      //channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  private void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
    System.out.println("\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger");

    //contract.submitTransaction("InitLedger");

    System.out.println("*** Transaction committed successfully");
  }

  @GetMapping("/parts")
  public ResponseEntity<byte[]> getAllParts() throws GatewayException {
    return ResponseEntity.ok(getAllAssets());
  }
  @PostMapping("/parts")
  public ResponseEntity<String> addPart(@RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
    //  AssetPrivateData data = new AssetPrivateData("asset_properties", "color", 10L); przyklad <= to wlasnie ta klase trzeba zedytowac i jej pochodne
    return ResponseEntity.created(URI.create("")).body(createAsset(request.getPublicDescription(), request.getIsForSale(), request.getPrivateData()));
  }

  @GetMapping("/parts/{id}")
  public ResponseEntity<byte[]> getAssetById(@PathVariable String id) {
    try {
      return ResponseEntity.ok(readAssetById(id));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
  }

  @GetMapping("/parts/{id}/details")
  public ResponseEntity<byte[]> getPrivateAssetDetailsById(@PathVariable String id) {
    try {
      return ResponseEntity.ok(readAssetById(id));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
  }
//
//
//  @PutMapping("/parts/{id}")
//  public ResponseEntity<String> changePublicDescription(@PathVariable String id, @RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
//    // @TODO
//    return ResponseEntity.created(URI.create("")).body(createAsset(request.getPublicDescription(), request.getPrivateData()));
//  }
//
//  @PutMapping("/market/{id}/owner")
//  public ResponseEntity<String> agreeToSell(@PathVariable String id, @RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
//    // @TODO
//    return ;
//  }

//  @GetMapping("/market/{id}")
//  public ResponseEntity<String> verifyAssetProperties(@PathVariable String id, @RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
//    // @TODO
//    return ResponseEntity.created(URI.create("")).body(createAsset(request.getPublicDescription(), request.getPrivateData()));
//  }

//  @PutMapping("/market/{id}/owner")
//  public ResponseEntity<String> agreeToBuy(@PathVariable String id, @RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
//    // @TODO
//    return ResponseEntity.created(URI.create("")).body(createAsset(request.getPublicDescription(), request.getPrivateData()));
//  }

  // i jeszcze zgodnie z gaetway w ts:
  //  getAssetSalesPrice
  //  getAssetBidPrice
  //  transferAsset

  /**
   * Evaluate a transaction to query ledger state.
   */
  private byte[] getAllAssets() throws GatewayException {
    System.out.println("\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger");

    var result = contract.evaluateTransaction("GetAllAssets", String.valueOf(new ArrayList<>()));

    System.out.println("*** Result: " + prettyJson(result));

    return result;
  }

  private String prettyJson(final byte[] json) {
    return prettyJson(new String(json, StandardCharsets.UTF_8));
  }

  private String prettyJson(final String json) {
    var parsedJson = JsonParser.parseString(json);
    return gson.toJson(parsedJson);
  }

  /**
   * Submit a transaction synchronously, blocking until it has been committed to
   * the ledger.
   */
  private String createAsset(String publicDescription, Boolean isForSale, AssetPrivateData privateData) throws GatewayException, CommitException, IOException {
    System.out.println("\n--> Submit Transaction: CreateAsset, creates new asset");

    AssetPropertiesJSON assetPropertiesJSON = new AssetPropertiesJSON("asset_properties", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", privateData.getPartName(),
            privateData.getPartNumber(), privateData.getDescription(), privateData.getManufacturer(), privateData.getLength(), privateData.getWidth(), privateData.getHeight(), privateData.getStatus(),
            privateData.getLastInspectionDate(), privateData.getInspectionPerformedBy(), privateData.getNextInspectionDate(), privateData.getLifeLimit(), privateData.getCurrentUsageTimes()); // ?
    byte[] resultBytes = contract.newProposal("CreateAsset")
            .addArguments(publicDescription.getBytes(), isForSale.toString().getBytes())
            .putTransient("asset_properties",  assetPropertiesJSON.toString())
            .build()
            .endorse()
            .submit();

    String assetID = prettyJson(resultBytes); // decode to utf
    System.out.println(String.format("id: %s", assetID));

    System.out.println("*** Transaction committed successfully");
    return assetID;
  }

  private byte[] readAssetById(String assetId) throws GatewayException {
    System.out.println("\n--> Evaluate Transaction: ReadAsset, function returns asset attributes");

    var evaluateResult = contract.evaluateTransaction("ReadAsset", assetId);

    System.out.println("*** Result:" + prettyJson(evaluateResult));

    return evaluateResult;
  }

  private byte[] readAssetDetailsById(String assetId) throws GatewayException {
    System.out.println("\n--> Evaluate Transaction: GetAssetPrivateProperties, function returns private asset attributes");

    var evaluateResult = contract.evaluateTransaction("GetAssetPrivateProperties", assetId);

    System.out.println("*** Result:" + prettyJson(evaluateResult));

    return evaluateResult;
  }

//  private byte[] convertToBytes(Object object) throws IOException {
//    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
//         ObjectOutputStream out = new ObjectOutputStream(bos)) {
//      out.writeObject(object);
//      return bos.toByteArray();
//    }
//  }

  // @todo TU MNIE JESZCZE NIE BYLO
  /**
   * Submit transaction asynchronously, allowing the application to process the
   * smart contract response (e.g. update a UI) while waiting for the commit
   * notification.
   */
  private void transferAssetAsync() throws EndorseException, SubmitException, CommitStatusException {
    System.out.println("\n--> Async Submit Transaction: TransferAsset, updates existing asset owner");

    var commit = contract.newProposal("TransferAsset")
        .addArguments(assetId, "Saptha")
        .build()
        .endorse()
        .submitAsync();

    var result = commit.getResult();
    var oldOwner = new String(result, StandardCharsets.UTF_8);

    System.out.println("*** Successfully submitted transaction to transfer ownership from " + oldOwner + " to Saptha");
    System.out.println("*** Waiting for transaction commit");

    var status = commit.getStatus();
    if (!status.isSuccessful()) {
      throw new RuntimeException("Transaction " + status.getTransactionId() +
          " failed to commit with status code " + status.getCode());
    }

    System.out.println("*** Transaction committed successfully");
  }


  // @TODO Przyklad ze nie dziala cos
  /**
   * submitTransaction() will throw an error containing details of any error
   * responses from the smart contract.
   */
  private void updateNonExistentAsset() {
    try {
      System.out.println("\n--> Submit Transaction: UpdateAsset asset70, asset70 does not exist and should return an error");

      contract.submitTransaction("UpdateAsset", "asset70", "blue", "5", "Tomoko", "300");

      System.out.println("******** FAILED to return an error");
    } catch (EndorseException | SubmitException | CommitStatusException e) {
      System.out.println("*** Successfully caught the error: ");
      e.printStackTrace(System.out);
      System.out.println("Transaction ID: " + e.getTransactionId());

      var details = e.getDetails();
      if (!details.isEmpty()) {
        System.out.println("Error Details:");
        for (var detail : details) {
          System.out.println("- address: " + detail.getAddress() + ", mspId: " + detail.getMspId()
              + ", message: " + detail.getMessage());
        }
      }
    } catch (CommitException e) {
      System.out.println("*** Successfully caught the error: " + e);
      e.printStackTrace(System.out);
      System.out.println("Transaction ID: " + e.getTransactionId());
      System.out.println("Status code: " + e.getCode());
    }
  }
}
