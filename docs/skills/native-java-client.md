# Skill: Native Java Client

**Trigger:** `API & Network Layer`
**Context:** When the user asks to fetch data, handle JSON, or you are editing files in `src/main/java/com/circularo/cli/api/*`

## Rules
- Exclusively use `java.net.http.HttpClient` for all network requests.
- DO NOT import or use third-party HTTP clients such as Retrofit, OkHttp, or Apache HTTP Client.
