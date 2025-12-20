#!/bin/bash

# ============================================================
# RadioAwa Stream Testing Script
# Tests if a stream URL is accessible and valid
# ============================================================

set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Print header
echo "=========================================="
echo "RadioAwa Stream URL Tester"
echo "=========================================="
echo ""

# Check if URL provided
if [ -z "$1" ]; then
    echo -e "${RED}Error: No stream URL provided${NC}"
    echo ""
    echo "Usage:"
    echo "  ./test-stream.sh \"STREAM_URL\""
    echo ""
    echo "Examples:"
    echo "  ./test-stream.sh \"https://vividhbharati-lh.akamaihd.net/i/vividhbharati_1@507811/index_1_a-p.m3u8?sd=10&rebase=on\""
    echo "  ./test-stream.sh \"http://prclive1.listenon.in:9960/\""
    echo ""
    exit 1
fi

STREAM_URL="$1"

echo -e "${YELLOW}Testing stream URL:${NC}"
echo "$STREAM_URL"
echo ""

# Test 1: Check if URL is reachable with curl
echo "Test 1: Checking URL accessibility..."
if curl -I --max-time 10 --silent --fail "$STREAM_URL" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ URL is accessible${NC}"
else
    echo -e "${RED}✗ URL is not accessible or timed out${NC}"
    echo ""
    echo "Detailed curl output:"
    curl -I --max-time 10 "$STREAM_URL" 2>&1 || true
    echo ""
    echo -e "${YELLOW}Note: Some streams require specific headers or may be geo-blocked${NC}"
fi
echo ""

# Test 2: Check response headers
echo "Test 2: Checking response headers..."
HEADERS=$(curl -I --max-time 10 --silent "$STREAM_URL" 2>&1)
if echo "$HEADERS" | grep -q "200 OK"; then
    echo -e "${GREEN}✓ Server returned 200 OK${NC}"
elif echo "$HEADERS" | grep -q "302\|301"; then
    echo -e "${YELLOW}⚠ Server returned a redirect${NC}"
    echo "$HEADERS" | grep -i "location:" || true
elif echo "$HEADERS" | grep -q "403"; then
    echo -e "${RED}✗ Server returned 403 Forbidden (may require authentication or referrer)${NC}"
elif echo "$HEADERS" | grep -q "404"; then
    echo -e "${RED}✗ Server returned 404 Not Found${NC}"
else
    echo -e "${YELLOW}⚠ Received unusual response:${NC}"
    echo "$HEADERS" | head -5
fi
echo ""

# Test 3: Check content type
echo "Test 3: Checking content type..."
CONTENT_TYPE=$(curl -I --max-time 10 --silent "$STREAM_URL" 2>&1 | grep -i "content-type:" | cut -d: -f2 | tr -d ' \r')
if [ -n "$CONTENT_TYPE" ]; then
    if echo "$CONTENT_TYPE" | grep -iq "audio\|mpegurl\|m3u"; then
        echo -e "${GREEN}✓ Content type is audio/streaming: $CONTENT_TYPE${NC}"
    else
        echo -e "${YELLOW}⚠ Unexpected content type: $CONTENT_TYPE${NC}"
    fi
else
    echo -e "${YELLOW}⚠ No content type header found${NC}"
fi
echo ""

# Test 4: Try to fetch first few bytes
echo "Test 4: Attempting to fetch stream data..."
if timeout 10 curl --max-time 10 --silent "$STREAM_URL" 2>/dev/null | head -c 1000 > /tmp/stream_test_$$.dat 2>&1; then
    SIZE=$(wc -c < /tmp/stream_test_$$.dat)
    if [ "$SIZE" -gt 0 ]; then
        echo -e "${GREEN}✓ Successfully fetched stream data (${SIZE} bytes)${NC}"

        # Check if it's m3u8
        if head -1 /tmp/stream_test_$$.dat | grep -q "#EXTM3U"; then
            echo -e "${GREEN}✓ Detected HLS/m3u8 format${NC}"
            echo ""
            echo "M3U8 Content Preview:"
            head -10 /tmp/stream_test_$$.dat
        else
            echo -e "${YELLOW}⚠ Not a valid m3u8 file (may be direct audio stream)${NC}"
        fi
    else
        echo -e "${RED}✗ No data received from stream${NC}"
    fi
    rm -f /tmp/stream_test_$$.dat
else
    echo -e "${RED}✗ Failed to fetch stream data${NC}"
fi
echo ""

# Final recommendation
echo "=========================================="
echo "Summary & Recommendations:"
echo "=========================================="
echo ""

# Simple pass/fail logic
if curl -I --max-time 10 --silent --fail "$STREAM_URL" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ This stream appears to be working!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Test it in VLC or a media player"
    echo "2. Update the database using update-hindi-stream.sql"
    echo "3. Reload RadioAwa in your browser"
else
    echo -e "${RED}✗ This stream appears to have issues${NC}"
    echo ""
    echo "Troubleshooting:"
    echo "1. Try the stream in VLC media player"
    echo "2. Check if you need a VPN (geo-blocking)"
    echo "3. Try a different stream URL from HINDI-STREAM-OPTIONS.md"
    echo "4. Some streams require a referrer header"
fi
echo ""
