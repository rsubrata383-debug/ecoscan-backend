# EcoScan API

Base URL: `http://localhost:8080`

## `POST /api/scan`

Upload an image as multipart field `image`. Supported types are JPEG, PNG, and WebP, up to 5 MB. File signatures are checked.

```http
POST /api/scan
Content-Type: multipart/form-data
```

Example response:

```json
{
  "itemName": "Plastic Water Bottle",
  "category": "Plastic",
  "bin": "recyclable",
  "tip": "Empty it, crush it, and keep the cap on.",
  "material": "PET 1 Plastic",
  "recyclability": "easy",
  "howToPrepare": ["Empty all liquid", "Crush it", "Keep the cap on"],
  "decompositionTime": "Up to 450 years",
  "whyItMatters": "Plastic breaks into tiny pieces that enter water.",
  "prosOfRightDisposal": ["Saves energy", "Less landfill waste"],
  "consOfWrongDisposal": ["Pollutes soil and water", "Spoils other recyclables"],
  "afterRecyclingItBecomes": ["Polyester clothing", "New bottles"],
  "reuseIdeas": ["Plant pot", "Pen holder"],
  "funFact": "One recycled bottle can help save energy.",
  "commonMistake": "Leaving liquid inside spoils paper in the same bin."
}
```

The required response fields are `itemName`, `category`, `bin`, and `tip`. Educational fields are optional; omitted strings and lists become empty. A failed scan returns an error and never a demo item.

## `GET /api/demo`

Returns the nine fixed demo items.

```http
GET /api/demo
```

Example response:

```json
[
  {"id":"plastic-bottle","name":"Plastic Bottle","icon":"🥤"},
  {"id":"aluminum-can","name":"Aluminum Can","icon":"🥫"}
]
```

The actual response contains all nine entries.

## `GET /api/demo/{id}`

Returns the complete result for a known demo identifier.

```http
GET /api/demo/plastic-bottle
```

Example response:

```json
{
  "itemName": "Plastic Bottle",
  "category": "Plastic",
  "bin": "recyclable",
  "tip": "Rinse it and recycle it if accepted locally.",
  "material": "PET 1 Plastic",
  "recyclability": "easy",
  "howToPrepare": ["Empty all liquid", "Crush it if local rules allow", "Keep the cap on if accepted"],
  "decompositionTime": "Up to 450 years",
  "whyItMatters": "Plastic can break into tiny pieces that move through waterways.",
  "prosOfRightDisposal": ["Keeps plastic out of landfills", "Provides material for new products"],
  "consOfWrongDisposal": ["Can litter land and water", "Can spoil other recyclables"],
  "afterRecyclingItBecomes": ["New bottles", "Polyester fabric"],
  "reuseIdeas": ["Use it as a small planter", "Use it to store craft items"],
  "funFact": "PET bottles can be made into new bottles.",
  "commonMistake": "Leaving liquid inside can spoil paper in the same bin."
}
```

Unknown identifiers return `404` with `{"message":"Demo item was not found."}`.

## `GET /api/status`

Returns whether the Gemini key is configured.

```http
GET /api/status
```

Example response:

```json
{"aiEnabled":false}
```

## Error codes

Errors have a JSON `message` field.

| Status | Meaning |
| --- | --- |
| `400` | Missing, empty, unsupported, or invalid image |
| `404` | Unknown demo item or route |
| `413` | Image exceeds 5 MB |
| `429` | Gemini rate limit |
| `502` | Gemini request failed or result was invalid |
| `503` | Gemini is not configured |
