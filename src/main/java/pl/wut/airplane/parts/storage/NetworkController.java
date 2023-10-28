package pl.wut.airplane.parts.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.wut.airplane.parts.storage.model.*;

@RestController
@CrossOrigin
public class NetworkController {

  private final NetworkService networkService;

  public NetworkController(NetworkService networkService)  {
    // @TODO FLOW: Chaincode mam w Go -> mam gateway tylko w ts -> musze napisac na bazie tego odpowiednie funkcje w springu, to co mam w springu to jest basic asset transfer
    // 1. krok ref - pzeniesc do serwisu
    // 2. ogarnac set up na dwie org
    // 3. sprawdzic czy dzialaja te endpointy co dodalam
    // 4. dopisac pozostale endpointy
    // 7. dalej by byly potrzebne endpointy do spedzay lub edycji
    // 10. posprzatac
    // 11. dane init, readme
    // 12. uzyc serwisu
    // 13. usunac niuzywane klasy
    this.networkService = networkService;
  }



//  @GetMapping("/parts")
//  public ResponseEntity<byte[]> getAllParts() throws GatewayException {
//    return ResponseEntity.ok(getAllAssets());
//  }
  @PostMapping("/parts")
  public ResponseEntity<String> addPart(@RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
    return ResponseEntity.created(URI.create("")).body(networkService.createAsset(request.getPublicDescription(), request.getIsForSale(), request.getPrivateData()));
  }

  @GetMapping("/parts/{id}")
  public ResponseEntity<byte[]> getAssetById(@PathVariable String id) {
    try {
      return ResponseEntity.ok(networkService.readAssetById(id));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
  }

  @GetMapping("/parts/{id}/details") // zepsute
  public ResponseEntity<byte[]> getPrivateAssetDetailsById(@PathVariable String id) {
    try {
      return ResponseEntity.ok(networkService.readAssetDetailsById(id));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
  }

  //0. serializacja obiektow - DONE
  // 1. Endpoint do pobrania tylko rekordow na sprzedaz => filtrowane getAll - DONE

  @GetMapping("/parts/sale")
  public ResponseEntity<List<Asset>> getAllPartsForSale() throws GatewayException {
    List<Asset> result = networkService.getAllAssetsForSale();
    return ResponseEntity.ok(result);
  }

  // 2.Endpoint do wystawienia na sprzedaż => potrzebny apdejt rejestru, analogia do changePublicDescription tylko ja chce zeby
  // pole isForSale sie ustawilo a nie public description

  @PatchMapping("/parts/{id}/details")
  public ResponseEntity<byte[]> changeAssetForSale(@PathVariable String id) {
    try {
      networkService.setAssetForSale(id);
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
      return ResponseEntity.ok().build();
  }

  // 3. Agree to sell by org1 przyjmujace cene, zawiera losowe tradeId (AgreeToSell)
    @PatchMapping("/parts/{id}/price")
  public ResponseEntity<String> agreeToSell(@PathVariable String id, @RequestParam Long price) throws GatewayException, CommitException, IOException {
      try {
      networkService.agreeToSell(id, price);
      }
      catch (Exception e) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Asset Not Found", e);
      }
      return ResponseEntity.ok().build();
  }


  // 4. Agree to buy as org 2

  @PostMapping("/market/{id}")
  public ResponseEntity<String> agreeToBuy(@RequestBody AgreeToBuyAssetRequest agreeToBuyAssetRequest, @PathVariable String id) throws GatewayException, CommitException, IOException {
    try {
      networkService.agreeToBuy(id, agreeToBuyAssetRequest);
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
    return ResponseEntity.ok().build();
  }

  // 5. Transfer


  // 3.5 Dodanie params na obie org
  // 3.6 Weryfikacja czys zrobic jako org 2

  // pisanie + front + auth

  // 6. W ogole endpoint do edycji sie przyda ;)

  // 7. Enpoint na pobranie historii

  // 8. podpiac baze danych i basic auth

  // 9. podpiac frontend

  // 10. testy



// @TODO: Tego na razie nie potrzebuje, ale spoko wzor
//  @PutMapping("/parts/{id}")
//  public ResponseEntity<String> changePublicDescription(@PathVariable String id, @RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
//    // @TODO
//    return ResponseEntity.created(URI.create("")).body(createAsset(request.getPublicDescription(), request.getPrivateData()));
//  }
//


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
//  private void transferAssetAsync() throws EndorseException, SubmitException, CommitStatusException {
//    System.out.println("\n--> Async Submit Transaction: TransferAsset, updates existing asset owner");
//
//    var commit = contract.newProposal("TransferAsset")
//        .addArguments(assetId, "Saptha")
//        .build()
//        .endorse()
//        .submitAsync();
//
//    var result = commit.getResult();
//    var oldOwner = new String(result, StandardCharsets.UTF_8);
//
//    System.out.println("*** Successfully submitted transaction to transfer ownership from " + oldOwner + " to Saptha");
//    System.out.println("*** Waiting for transaction commit");
//
//    var status = commit.getStatus();
//    if (!status.isSuccessful()) {
//      throw new RuntimeException("Transaction " + status.getTransactionId() +
//          " failed to commit with status code " + status.getCode());
//    }
//
//    System.out.println("*** Transaction committed successfully");
//  }


  // @TODO Przyklad ze nie dziala cos
  /**
   * submitTransaction() will throw an error containing details of any error
   * responses from the smart contract.
   */
//  private void updateNonExistentAsset() {
//    try {
//      System.out.println("\n--> Submit Transaction: UpdateAsset asset70, asset70 does not exist and should return an error");
//
//      contract.submitTransaction("UpdateAsset", "asset70", "blue", "5", "Tomoko", "300");
//
//      System.out.println("******** FAILED to return an error");
//    } catch (EndorseException | SubmitException | CommitStatusException e) {
//      System.out.println("*** Successfully caught the error: ");
//      e.printStackTrace(System.out);
//      System.out.println("Transaction ID: " + e.getTransactionId());
//
//      var details = e.getDetails();
//      if (!details.isEmpty()) {
//        System.out.println("Error Details:");
//        for (var detail : details) {
//          System.out.println("- address: " + detail.getAddress() + ", mspId: " + detail.getMspId()
//              + ", message: " + detail.getMessage());
//        }
//      }
//    } catch (CommitException e) {
//      System.out.println("*** Successfully caught the error: " + e);
//      e.printStackTrace(System.out);
//      System.out.println("Transaction ID: " + e.getTransactionId());
//      System.out.println("Status code: " + e.getCode());
//    }
//  }
}
