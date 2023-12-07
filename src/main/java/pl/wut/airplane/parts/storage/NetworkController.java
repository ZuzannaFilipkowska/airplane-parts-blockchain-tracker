package pl.wut.airplane.parts.storage;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.GatewayException;
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
    this.networkService = networkService;
  }

  @GetMapping("/parts/all")
  public ResponseEntity<byte[]> getAllParts() throws GatewayException {
    return ResponseEntity.ok(networkService.getAllAssets());
  }

  @PostMapping("/parts")
  public ResponseEntity<String> addPart(@RequestBody CreateAssetRequest request) throws GatewayException, CommitException, IOException {
    try {
      return ResponseEntity.created(URI.create("")).body(networkService.createAsset(request.getPublicDescription(), request.getIsForSale(), request.getPrivateData()));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "Invalid asset data", e);
    }
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

  @GetMapping("/parts/{id}/details")
  public ResponseEntity<byte[]> getPrivateAssetDetailsById(@PathVariable String id) {
    try {
      return ResponseEntity.ok(networkService.readAssetDetailsById(id));
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
  }

  @GetMapping("/parts/sale")
  public ResponseEntity<List<Asset>> getAllPartsForSale() throws GatewayException {
    List<Asset> result = networkService.getAllAssetsForSale();
    return ResponseEntity.ok(result);
  }

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

    @PatchMapping("/parts/{id}/price")
  public ResponseEntity<String> agreeToSell(@PathVariable String id, @RequestParam Long price, @RequestParam String tradeId) throws GatewayException, CommitException, IOException {
      try {
      networkService.agreeToSell(id, price, tradeId);
      }
      catch (Exception e) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Asset Not Found", e);
      }
      return ResponseEntity.ok().build();
  }

    @PostMapping("/parts/{assetId}")
  public ResponseEntity<String> verifyAssetProperties(@RequestBody AssetProperties asset, @PathVariable String assetId) throws GatewayException, CommitException, IOException {
    try {
      networkService.verifyAssetProperties(assetId, asset);
    }
    catch (Exception e) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
    return ResponseEntity.ok().build();
  }

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

  @PatchMapping("/parts/{id}/owner")
  public ResponseEntity<byte[]> changeAssetOwner(@PathVariable String id, @RequestBody TransferAssetRequest request) {
    try {
      networkService.transferAsset(id, request.getPrice(), request.getTradeID());
    } catch (Exception e) {
            throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "Asset Not Found", e);
    }
    return ResponseEntity.ok().build();

  }
}
