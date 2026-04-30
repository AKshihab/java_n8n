# java_n8n

Java Swing contact form that sends name/email/query to an n8n webhook.

## OOP Structure
- `User`: Model for user form data.
- `WebhookClient`: Validates user input and sends JSON payload to n8n webhook URL.
- `AppController`: Connects UI input to webhook submission.
- `AppFrame`: Swing GUI for data entry and submit action.

## Run
```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.example.javan8n.Main
```

## Payload Sent to n8n
```json
{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "query": "Need help with integration"
}
```

## Recommended n8n Workflow
1. **Webhook node** (`POST`) receives payload.
2. **Google Sheets node** (optional) appends row with `name`, `email`, `query`, and timestamp.
3. **Gmail node** sends confirmation email to `{{$json.email}}`.

### Gmail Node Example
- **To**: `{{$json.email}}`
- **Subject**: `We received your query`
- **Body**: `Hi {{$json.name}}, thanks for your message. We will reply soon.`

## Notes
- The Swing app only submits data and shows success/error feedback.
- Storage and email delivery are handled in n8n.
