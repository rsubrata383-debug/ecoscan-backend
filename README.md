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
