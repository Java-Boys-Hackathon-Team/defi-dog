# DeFi Dog - Smart Contract Security Audit

DeFi Dog is a comprehensive tool for smart contract security auditing in the DeFi (Decentralized Finance) space. It leverages AI and multiple blockchain data sources to provide in-depth analysis and security assessments of smart contracts.

## Features

- **Smart Contract Analysis**: Fetch and analyze smart contract source code from Etherscan and Sourcify
- **AI-Powered Analysis**: Utilize OpenAI's GPT models to analyze smart contracts for security vulnerabilities
- **Blockchain Integration**: Interact with Ethereum blockchain data
- **Decompilation**: Decompile smart contract bytecode using Dedaub
- **Market Data**: Get cryptocurrency market data from CoinMarketCap
- **Notifications**: Send notifications to users via Telegram

## Technology Stack

- **Backend**: Java 17, Spring Boot
- **Frontend**: Vaadin Flow
- **Database**: PostgreSQL
- **AI**: Spring AI with OpenAI integration
- **Blockchain**: Web3j
- **Messaging**: Telegram Bot API
- **Build Tool**: Gradle
- **Framework**: Jmix (for rapid business application development)

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running locally)
- API keys for:
  - OpenAI
  - Etherscan
  - Alchemy (Ethereum node provider)
  - CoinMarketCap
  - Dedaub
  - Telegram Bot

## Setup

### Environment Variables

Create a `.env` file in the project root with the following variables:

```
OPENAI_API_KEY=your_openai_api_key
ETHERSCAN_API_KEY=your_etherscan_api_key
ALCHEMY_API_KEY=your_alchemy_api_key
COINMARKETCAP_API_KEY=your_coinmarketcap_api_key
DEDAUB_API_KEY=your_dedaub_api_key
DEFI_DOG_TELEGRAM_BOT_TOKEN=your_telegram_bot_token
```

### Local Development

1. Clone the repository:
   ```
   git clone https://github.com/RustamKuramshin/defi-dog
   cd defi-dog
   ```

2. Start the PostgreSQL database:
   ```
   docker-compose up -d defi-dog-db
   ```

3. Run the application:
   ```
   ./gradlew bootRun
   ```

4. Access the application at http://localhost:8080

### Docker Deployment

1. Build the application:
   ```
   ./gradlew build
   ```

2. Start the containers:
   ```
   docker-compose up -d
   ```

3. Access the application at http://localhost:8082

## Configuration

The application can be configured through the following files:

- `src/main/resources/application.properties`: Main configuration file
- `src/main/resources/application-prod.properties`: Production-specific configuration

## Database

The application uses PostgreSQL as its database. The database schema is managed through Liquibase and defined in:

```
src/main/resources/ru/javaboys/defidog/liquibase/changelog.xml
```

## Testing

Run the tests with:

```
./gradlew test
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## Contact

- Рустам Курамшин — https://t.me/KuramshinRustam | 📞 +7 (952) 584-34-99
- Рустам Гулямов — https://t.me/gulyamovrustam | 📞 +7 (912) 321-88-19
- Александр Янчий — https://t.me/AlexYanchiy_ru | 📞 +7 (978) 127-77-34
- Рустам Зулкарниев — https://t.me/WerderR | 📞 +7 (919) 883-84-60
