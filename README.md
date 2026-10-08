# EcoScan Backend

EcoScan is a small Spring Boot API for identifying waste from a photo and suggesting a bin. It uses Google Gemini when an API key is configured and includes an in-memory demo mode.

## Run the project

Use Java 21 and Maven. Set your Gemini API key in the environment, then start the app:

```powershell
$env:GEMINI_API_KEY="your-gemini-api-key"
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`. Without a key, `/api/status` reports `{"aiEnabled":false}` and a valid image scan returns `503` with a friendly message. Demo endpoints remain available. Copy `.env.example` only as a reference; Spring reads `GEMINI_API_KEY` from the process environment.

## API

### `POST /api/scan`

Submit a multipart file using the field name `image`. JPEG, PNG, and WebP images are accepted, up to 5 MB, and the image signature is checked as well as its MIME type.

```powershell
curl.exe -X POST http://localhost:8080/api/scan -F "image=@C:\images\plastic-bottle.jpg"
```

Example response:

```json
{
  "itemName": "Plastic Bottle",
  "category": "Plastic",
  "bin": "recyclable",
  "tip": "Rinse it and put it in the blue bin."
}
```

If Gemini is not configured:

```json
{"message":"AI mode is off. Use demo mode."}
```

### `GET /api/demo`

Returns the nine built-in demo items as `{ "id", "name", "icon" }` objects.

```powershell
curl.exe http://localhost:8080/api/demo
```

### `GET /api/demo/{id}`

Returns a `WasteResult` for a demo item. Unknown IDs return `404`.

```powershell
curl.exe http://localhost:8080/api/demo/plastic-bottle
```

Example response:

```json
{
  "itemName": "Plastic Bottle",
  "category": "Plastic",
  "bin": "recyclable",
  "tip": "Rinse it and put it in the recycling bin."
}
```

### `GET /api/status`

Shows whether the Gemini API key is configured.

```powershell
curl.exe http://localhost:8080/api/status
```

Example response with no key:

```json
{"aiEnabled":false}
```

## Use the API from a React frontend with RTK Query

The examples below use React, TypeScript, and Vite. They assume the backend is running at `http://localhost:8080` and the frontend at `http://localhost:5173`.

### 1. Install Redux Toolkit and React Redux

Run this in the frontend project:

```bash
npm install @reduxjs/toolkit react-redux
```

### 2. Define API response types

Create `src/services/ecoScanApi.ts`. These types match the JSON returned by this backend:

```ts
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

export type WasteResult = {
  itemName: string;
  category: "Plastic" | "Organic" | "E-Waste" | "Paper" | "Metal" | "Glass" | "Other";
  bin: "recyclable" | "organic" | "non-recyclable" | "special";
  tip: string;
};

export type DemoItem = {
  id: string;
  name: string;
  icon: string;
};

export type ApiStatus = {
  aiEnabled: boolean;
};
```

### 3. Create RTK Query endpoints

Add these endpoints in the same `ecoScanApi.ts` file. `FormData` sends the image as multipart data with the required field name `image`.

```ts
export const ecoScanApi = createApi({
  reducerPath: "ecoScanApi",
  baseQuery: fetchBaseQuery({ baseUrl: "http://localhost:8080/api" }),
  endpoints: (builder) => ({
    getDemoItems: builder.query<DemoItem[], void>({
      query: () => "/demo",
    }),
    getDemoResult: builder.query<WasteResult, string>({
      query: (id) => `/demo/${encodeURIComponent(id)}`,
    }),
    getApiStatus: builder.query<ApiStatus, void>({
      query: () => "/status",
    }),
    scanImage: builder.mutation<WasteResult, File>({
      query: (image) => {
        const body = new FormData();
        body.append("image", image);
        return {
          url: "/scan",
          method: "POST",
          body,
        };
      },
    }),
  }),
});

export const {
  useGetDemoItemsQuery,
  useGetDemoResultQuery,
  useGetApiStatusQuery,
  useScanImageMutation,
} = ecoScanApi;
```

Do not set the `Content-Type` header yourself for the image request. The browser must add the multipart boundary.

### 4. Add the API reducer and middleware to the Redux store

Create `src/store.ts`:

```ts
import { configureStore } from "@reduxjs/toolkit";
import { ecoScanApi } from "./services/ecoScanApi";

export const store = configureStore({
  reducer: {
    [ecoScanApi.reducerPath]: ecoScanApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(ecoScanApi.middleware),
});
```

### 5. Provide the store to React

Wrap the application with Redux's `Provider`, for example in `src/main.tsx`:

```tsx
import React from "react";
import ReactDOM from "react-dom/client";
import { Provider } from "react-redux";
import { store } from "./store";
import App from "./App";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>,
);
```

### 6. Show AI status and the demo list

Generated query hooks load data and expose loading and error states:

```tsx
import { useGetApiStatusQuery, useGetDemoItemsQuery } from "./services/ecoScanApi";

export function DemoList() {
  const { data: status } = useGetApiStatusQuery();
  const { data: items, isLoading, error } = useGetDemoItemsQuery();

  if (isLoading) return <p>Loading demo items...</p>;
  if (error) return <p>Could not load demo items.</p>;

  return (
    <section>
      <p>AI mode: {status?.aiEnabled ? "On" : "Off (use demo mode)"}</p>
      <ul>
        {items?.map((item) => (
          <li key={item.id}>
            {item.icon} {item.name}
          </li>
        ))}
      </ul>
    </section>
  );
}
```

Call `useGetDemoResultQuery(id)` to load the result for a chosen item:

```tsx
const { data, isLoading, error } = useGetDemoResultQuery("plastic-bottle");
```

### 7. Upload an image and display the scan result

The scan is a mutation. Pass the selected `File` to the trigger function; the browser sends it as multipart form data.

```tsx
import { useState, type FormEvent } from "react";
import { useScanImageMutation } from "./services/ecoScanApi";
import { getErrorMessage } from "./getErrorMessage";

export function ImageScan() {
  const [image, setImage] = useState<File>();
  const [scanImage, { data, isLoading, error }] = useScanImageMutation();

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (image) {
      await scanImage(image);
    }
  }

  return (
    <section>
      <form onSubmit={submit}>
        <input
          type="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={(event) => setImage(event.target.files?.[0])}
        />
        <button type="submit" disabled={!image || isLoading}>
          {isLoading ? "Scanning..." : "Scan image"}
        </button>
      </form>

      {data && (
        <div>
          <h2>{data.itemName}</h2>
          <p>Category: {data.category}</p>
          <p>Bin: {data.bin}</p>
          <p>{data.tip}</p>
        </div>
      )}

      {error && <p>{getErrorMessage(error)}</p>}
    </section>
  );
}
```

To show the backend's friendly error message (such as AI mode being off), create `src/getErrorMessage.ts`:

```ts
import type { FetchBaseQueryError } from "@reduxjs/toolkit/query";
import type { SerializedError } from "@reduxjs/toolkit";

export function getErrorMessage(error: FetchBaseQueryError | SerializedError): string {
  if ("status" in error && "data" in error) {
    const data = error.data;
    if (data && typeof data === "object" && "message" in data) {
      return String(data.message);
    }
  }
  return "The request failed. Please try again.";
}
```

The scan endpoint accepts files up to 5 MB and only accepts valid JPEG, PNG, and WebP images. When `aiEnabled` is `false`, scans return HTTP `503`; the demo list and demo result endpoints still work.

### 8. Check CORS if the browser blocks a request

The backend permits the Vite origin `http://localhost:5173` and `http://localhost:3000` for `/api/**` by default. If your frontend uses a different origin, add it to `app.cors.allowed-origins` in `application.properties`, restart the backend, and retry.

## Test requests

Run these with the application started. Replace the sample path with an existing image for the valid scan:

```powershell
curl.exe http://localhost:8080/api/demo
curl.exe http://localhost:8080/api/demo/plastic-bottle
curl.exe http://localhost:8080/api/demo/not-a-demo-item
curl.exe http://localhost:8080/api/status
curl.exe -X POST http://localhost:8080/api/scan -F "image=@C:\images\plastic-bottle.jpg"
curl.exe -X POST http://localhost:8080/api/scan -F "image=@C:\images\notes.txt;type=text/plain"
```

To verify the empty-key scan response in PowerShell, temporarily clear the key, restart the app, and send a valid image:

```powershell
$env:GEMINI_API_KEY=""
.\mvnw.cmd spring-boot:run
curl.exe -X POST http://localhost:8080/api/scan -F "image=@C:\images\plastic-bottle.jpg"
```

## Simple architecture

- `ScanController` receives HTTP requests and returns JSON responses.
- `GeminiService` validates scan images, sends image data to Gemini, and checks the JSON response. It never substitutes a demo result when a scan fails.
- `DemoService` serves the nine fixed in-memory examples.
- `GlobalExceptionHandler` turns API errors into a short `{ "message": "..." }` response.
- `AppConfig` creates the timeout-configured `RestClient` and allows local frontend origins for `/api/**`.

This keeps the exhibition flow easy to explain: a photo goes to the controller, the Gemini service asks the AI, and the API returns the item, category, bin, and tip. Demo requests use fixed examples and do not call Gemini.

## Run tests

```powershell
.\mvnw.cmd test
```
