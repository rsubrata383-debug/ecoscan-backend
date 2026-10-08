# EcoScan Backend

Simple Spring Boot API for scanning waste photos with Gemini, serving demo results, and reporting AI status.

## Run

Use Java 21 or newer. Set `GEMINI_API_KEY` in the environment or the project-root `.env` file, then run:

```powershell
$env:GEMINI_API_KEY="your-key"
.\mvnw.cmd spring-boot:run
```

The model defaults to `gemini-3.5-flash`; set `GEMINI_MODEL` to override it.

## Project structure

```text
src/main/java/com/ecoscan/
  config/       RestClient and CORS configuration
  constant/     API messages and Gemini/waste constants
  controller/   Four API endpoints
  demo/         Fixed demo data
  gemini/       Gemini request, HTTP call, and result parsing
  model/        API records
  service/      Scan and demo services
  util/         Small text, list, and image-signature utilities
  validation/   Uploaded-image validation
docs/           API, architecture, and sample HTTP requests
scripts/        API smoke checks for Windows and Mac/Linux
```

- [API reference](docs/API.md)
- [Architecture and request flow](docs/ARCHITECTURE.md)

Run tests with `.\mvnw.cmd test`. With the server running, run `.\scripts\check-api.ps1` on Windows or `sh scripts/check-api.sh` on Mac/Linux.
