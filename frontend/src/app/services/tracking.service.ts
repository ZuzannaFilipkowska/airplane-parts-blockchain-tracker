import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../environments/environment";
import {Part} from "../models/part";
import {ALL_PARTS} from "../core/config/api-paths";

@Injectable({
  providedIn: 'root'
})
export class TrackingService {
  public apiUrl: string = `${environment.baseUrl}`;
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  constructor(private http: HttpClient) {}

  getAllParts(): Observable<Part[]> {
    return this.http.get<Part[]>(
      `${this.apiUrl}${ALL_PARTS}`,
      this.httpOptions
    );
  }

  getPartDetails(): void {

  }

  addPart(): void {

  }

  // ew moglaby byc cala sprzedaz

 getAllPackages(): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/tracking`,
      this.httpOptions
    );
  }

  getPackageData(id: string): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/tracking/${id}`,
      this.httpOptions
    );
  }
}
