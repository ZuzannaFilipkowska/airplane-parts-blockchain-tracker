export interface Part {
  name: string;
  id: string;
  price: number;
  weight: number;
  width: number;
  length: number;
}

export interface PublicPartInfo {
  assetID: string;
  ownerOrg: string;
  publicDescription: string;
  isForSale: boolean;
}
