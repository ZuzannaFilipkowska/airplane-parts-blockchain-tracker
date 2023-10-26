import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable()
export class TrackingService {
  public apiUrl: string = `${environment.baseUrl}`;
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  constructor(private http: HttpClient) {}

  testConnection(): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/hello`, this.httpOptions);
  }

  getPackageData(id: string): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/tracking/${id}`,
      this.httpOptions
    );
  }

  getPackageHistory(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/history/${id}`, this.httpOptions);
  }
}
