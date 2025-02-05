# API @ OSRS.Cafe

This Project aims to offer a free to use API for Oldschool Runescape.

It will always be Open Source and Free to use on [OSRS.Cafe](https://api.osrs.cafe) (Soon)!

Currently, API Tokens are not implemented, but are planned to be implemented should the need for them arise.

## Features

- Swagger/OpenAPI Support [here](https://api.osrs.cafe)
- Player Data Endpoints featuring Skills, Activities and Combat Level (+ A query option to only query Skills/Activities you are ranked on)

## TO-DO

- Grand Exchange Endpoint
- Add proper ktor logging
- Decide on proper headers between different Clients (HiscoreClient vs GEClient) and decide on if a common Caching Utility should be used.

## Dependencies used in this project

| Dependency                      | Group                 | Used for                                                      |
|---------------------------------|-----------------------|---------------------------------------------------------------|
| kotlinx-serialization-json      | org.jetbrains.kotlinx | Serializing data sent and received                            |
| ktor-server-core                | io.ktor               | Essential ktor dependency                                     |
| ktor-server-netty               | io.ktor               | Essential ktor dependency                                     |
| ktor-server-content-negotiation | io.ktor               | Used to automatically serialize sent/received data            |
| ktor-server-cors                | io.ktor               | Used to easily set the CORS rules                             |
| ktor-server-rate-limit          | io.ktor               | Used for easy rate limiting                                   |
| ktor-server-status-pages        | io.ktor               | Used to easily return error messages on Exceptions            |
| ktor-server-websockets          | io.ktor               | Currently unused, might use it for GE Live Data in the future |
| ktor-client-core                | io.ktor               | Essential ktor dependency                                     |
| ktor-client-cio                 | io.ktor               | Essential ktor dependency                                     |
| ktor-client-content-negotiation | io.ktor               | Used to automatically serialize sent/received data            |
| ktor-serialization-kotlinx-json | io.ktor               | Essential for ktors content negotiation to work               |

Additionally, for Swagger we use a static HTML file obtained from [here](https://swagger.io/docs/open-source-tools/swagger-ui/usage/installation) and a custom Dark Mode CSS from [here](https://github.com/Amoenus/SwaggerDark).

## License
This Project uses the MIT License. See [here](LICENSE).