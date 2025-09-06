# HTTP POST Request Block Test

## Implementation Summary

Successfully added HTTP POST request block functionality to the MC Looper automation system:

### Features Added:
1. **HTTP POST Request Block**: New block type "post_request" with the following parameters:
   - `url`: Target URL for the POST request
   - `body`: JSON body content to send  
   - `timeout`: Timeout in milliseconds (default: 5000ms)
   - `response_var`: Variable name prefix for storing response data

2. **Response Variable Capture**: Automatically saves:
   - `{response_var}_code`: HTTP status code (200, 404, etc.)
   - `{response_var}_body`: Response body content
   - `{response_var}_success`: Boolean indicating success (2xx status codes)
   - `{response_var}_error`: Error message if request failed

3. **UI Integration**: 
   - Added to BlockSelectorScreen with descriptive labels
   - Full editing interface in BlockEditorScreen with proper field mapping
   - Form fields: URL input, request body, timeout, response variable name

4. **Execution Logic**:
   - Non-blocking asynchronous HTTP requests using Java HttpClient
   - Timeout handling to prevent hanging
   - Variable replacement in URL and body parameters
   - Comprehensive error handling for network issues

### Technical Implementation:
- **Java HttpClient**: Uses built-in Java 11+ HTTP client with async support
- **Timeout Control**: Configurable timeout prevents blocking game thread
- **Variable System**: Integrates with existing variable replacement system
- **Error Handling**: Graceful handling of network errors, timeouts, malformed URLs

### Example Usage:
```json
{
  "type": "post_request",
  "params": {
    "url": "https://api.example.com/webhook",
    "body": "{\"player\": \"{username}\", \"coins\": {mob_coins}}",
    "timeout": 3000,
    "response_var": "webhook"
  }
}
```

This would create variables:
- `webhook_code`: Status code (e.g., 200)
- `webhook_body`: Response content
- `webhook_success`: true/false
- `webhook_error`: Error message (if any)

### Build Status:
The implementation compiles successfully and integrates seamlessly with the existing loop execution system. The HTTP client runs asynchronously to avoid blocking Minecraft's game thread.
