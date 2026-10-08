# Architecture

## Request flow

Scan: `ScanController` -> `GeminiService` -> `ImageValidator` -> `GeminiClient` -> `GeminiResultParser` -> JSON response.

Demo requests go from `ScanController` to `DemoService`, which reads fixed values from `DemoData`. They do not call Gemini. Status asks `GeminiService` whether its client has a configured key.

## Class responsibilities

- `ScanController` maps the four `/api` endpoints and delegates to services.
- `GeminiService` coordinates validation, Gemini access, parsing, and the AI-enabled status.
- `ImageValidator` reads image bytes once and checks empty files, size, MIME type, and signature.
- `GeminiClient` sends requests with `x-goog-api-key`, maps upstream failures, and logs only status numbers or exception class names.
- `GeminiRequestBuilder` builds the Gemini request and response schema.
- `GeminiResultParser` parses and validates the JSON response and normalizes optional fields.
- `DemoService` serves demo data; `DemoData` contains the fixed nine examples.
- `AppConfig` configures RestClient timeouts; `CorsConfig` configures the existing API CORS policy.
- `ApiMessages`, `WasteConstants`, and `GeminiConstants` hold user messages, waste limits/values, and Gemini request constants.
- `TextUtils`, `ListUtils`, and `ImageSignature` provide small static operations used by the scan flow.
- `GlobalExceptionHandler` returns consistent JSON error bodies.

## Safety and behavior

The AI-selected bin remains the returned bin, with the existing consistency rules for `Unknown` and `E-Waste`. Invalid scan results become `502`; scans never fall back to demo data. The API key is sent only in the request header and is never logged. Image data is never logged. Only JPEG, PNG, and WebP images up to 5 MB with matching signatures are accepted.
