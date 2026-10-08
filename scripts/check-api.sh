#!/usr/bin/env sh

set -eu

BASE_URL="${BASE_URL:-http://localhost:8080}"
TMP_DIR="$(mktemp -d)"
FAILURES=0
trap 'rm -rf "$TMP_DIR"' EXIT HUP INT TERM

request() {
    name="$1"
    shift
    status="$(curl --silent --show-error --output "$TMP_DIR/body" --write-out '%{http_code}' "$@")" || {
        printf 'FAIL %s (curl failed)\n' "$name"
        FAILURES=$((FAILURES + 1))
        return 1
    }
    printf '%s' "$status" > "$TMP_DIR/status"
}

report() {
    name="$1"
    passed="$2"
    if [ "$passed" -eq 1 ]; then
        printf 'PASS %s\n' "$name"
    else
        printf 'FAIL %s\n' "$name"
        FAILURES=$((FAILURES + 1))
    fi
}

request "GET /api/status" "$BASE_URL/api/status"
STATUS="$(cat "$TMP_DIR/status")"
if [ "$STATUS" = 200 ] && grep -Eq '"aiEnabled"[[:space:]]*:[[:space:]]*(true|false)' "$TMP_DIR/body"; then
    report "GET /api/status" 1
else
    report "GET /api/status" 0
fi

request "GET /api/demo" "$BASE_URL/api/demo"
STATUS="$(cat "$TMP_DIR/status")"
ITEM_COUNT="$(grep -o '"id"' "$TMP_DIR/body" | wc -l | tr -d ' ')"
if [ "$STATUS" = 200 ] && [ "$ITEM_COUNT" = 9 ]; then
    report "GET /api/demo returns 9 items" 1
else
    report "GET /api/demo returns 9 items" 0
fi

request "GET /api/demo/plastic-bottle" "$BASE_URL/api/demo/plastic-bottle"
STATUS="$(cat "$TMP_DIR/status")"
if [ "$STATUS" = 200 ] && grep -Eq '"itemName"[[:space:]]*:[[:space:]]*"Plastic Bottle"' "$TMP_DIR/body"; then
    report "GET /api/demo/plastic-bottle" 1
else
    report "GET /api/demo/plastic-bottle" 0
fi

request "GET /api/demo/unknown" "$BASE_URL/api/demo/unknown"
STATUS="$(cat "$TMP_DIR/status")"
if [ "$STATUS" = 404 ] && grep -Eq '"message"[[:space:]]*:[[:space:]]*"' "$TMP_DIR/body"; then
    report "GET /api/demo/unknown returns 404 message" 1
else
    report "GET /api/demo/unknown returns 404 message" 0
fi

printf '%s' 'not an image' > "$TMP_DIR/bad-file.txt"
if command -v cygpath >/dev/null 2>&1; then
    BAD_FILE="$(cygpath -w "$TMP_DIR/bad-file.txt")"
else
    BAD_FILE="$TMP_DIR/bad-file.txt"
fi
request "POST /api/scan bad file" -F "image=@$BAD_FILE;type=text/plain" "$BASE_URL/api/scan"
STATUS="$(cat "$TMP_DIR/status")"
if [ "$STATUS" = 400 ] && grep -Eq '"message"[[:space:]]*:[[:space:]]*"' "$TMP_DIR/body"; then
    report "POST /api/scan rejects bad file" 1
else
    report "POST /api/scan rejects bad file" 0
fi

if [ "$FAILURES" -gt 0 ]; then
    exit 1
fi
