# DeFi Dog — Аудит безопасности смарт-контрактов | Документация разработчика

DeFi Dog — это комплексный инструмент для аудита безопасности смарт-контрактов в сфере DeFi (Децентрализованные Финансы). Он использует ИИ и множество источников данных блокчейна для предоставления глубокого анализа и оценки безопасности смарт-контрактов.

## Возможности

- **Анализ смарт-контрактов**: Получение и анализ исходного кода смарт-контрактов из Etherscan и Sourcify
- **Анализ на базе ИИ**: Использование моделей GPT от OpenAI для анализа уязвимостей безопасности в смарт-контрактах
- **Интеграция с блокчейном**: Взаимодействие с данными блокчейна Ethereum
- **Декомпиляция**: Декомпиляция байткода смарт-контрактов с помощью Dedaub
- **Рыночные данные**: Получение данных о рынке криптовалют от CoinMarketCap
- **Уведомления**: Отправка уведомлений пользователям через Telegram

## Технологический стек

- **Бэкенд**: Java 17, Spring Boot
- **Фронтенд**: Vaadin Flow
- **База данных**: PostgreSQL
- **ИИ**: Spring AI с интеграцией OpenAI
- **Блокчейн**: Web3j
- **Обмен сообщениями**: Telegram Bot API
- **Инструмент сборки**: Gradle
- **Фреймворк**: Jmix (для быстрой разработки бизнес-приложений)

## Предварительные требования

- Java 17 или выше
- Docker и Docker Compose (для контейнеризованного развертывания)
- PostgreSQL (при локальном запуске)
- API-ключи для:
  - OpenAI
  - Etherscan
  - Alchemy (провайдер узла Ethereum)
  - CoinMarketCap
  - Dedaub
  - Telegram Bot

### Структура пакетов
- `view` - сервисные бины связанные с экранами на UI и вообще любой код для UI-логики
- `sources` - сервисные бины и DTO для работы с исходным кодом смарт-контрактов
- `notifications` - сервисные бины и DTO для работы с уведомлениями
- `integrations` - сервисные бины и DTO для интеграций с внешними API
- `crypto` - сервисные бины и DTO для главного экрана
- `audit` и `audit/tools` - сервисные бины и DTO бэкендовой логики аудита смартов
- `asyncjobs` - сервисные бины и DTO для асинхронных задач и задач по расписанию
- `admin` - сервисные бины и DTO для работы с функциями администратора системы

### Help инструментов статического анализа кода
Mythril:
```text
usage: myth analyze [-h] [--rpc HOST:PORT / ganache / infura-[network_name]]
                    [--rpctls RPCTLS] [--infura-id INFURA_ID]
                    [--solc-json SOLC_JSON] [--solc-args SOLC_ARGS]
                    [--solv SOLV] [-c BYTECODE] [-f BYTECODEFILE]
                    [-a CONTRACT_ADDRESS] [--bin-runtime]
                    [-o <text/markdown/json/jsonv2>] [-g GRAPH]
                    [-j OUTPUT_FILE] [-m MODULES] [--max-depth MAX_DEPTH]
                    [--call-depth-limit CALL_DEPTH_LIMIT]
                    [--strategy {dfs,bfs,naive-random,weighted-random,pending}]
                    [--transaction-sequences TRANSACTION_SEQUENCES]
                    [--beam-search BEAM_SEARCH] [-b N] [-t TRANSACTION_COUNT]
                    [--execution-timeout EXECUTION_TIMEOUT]
                    [--solver-timeout SOLVER_TIMEOUT]
                    [--create-timeout CREATE_TIMEOUT] [--parallel-solving]
                    [--solver-log SOLVER_LOG] [--no-onchain-data]
                    [--pruning-factor PRUNING_FACTOR]
                    [--unconstrained-storage] [--phrack] [--enable-physics]
                    [-q] [--disable-iprof] [--disable-dependency-pruning]
                    [--disable-coverage-strategy] [--disable-mutation-pruner]
                    [--enable-state-merging] [--enable-summaries]
                    [--custom-modules-directory CUSTOM_MODULES_DIRECTORY]
                    [--attacker-address ATTACKER_ADDRESS]
                    [--creator-address CREATOR_ADDRESS]
                    [solidity_files ...]

positional arguments:
  solidity_files        Inputs file name and contract name. 
                        usage: file1.sol:OptionalContractName file2.sol file3.sol:OptionalContractName

options:
  -h, --help            show this help message and exit
  --rpc HOST:PORT / ganache / infura-[network_name]
                        custom RPC settings
  --rpctls RPCTLS       RPC connection over TLS
  --infura-id INFURA_ID
                        set infura id for onchain analysis
  --solc-json SOLC_JSON
                        Json for the optional 'settings' parameter of solc's standard-json input
  --solc-args SOLC_ARGS
                        Provide solc args, example: --solc-args "--allow-paths --include-path /root_folder/node_modules --base-path /home/contracts" 
  --solv SOLV           specify solidity compiler version. If not present, will try to install it (Experimental)
  -c BYTECODE, --code BYTECODE
                        hex-encoded bytecode string ("6060604052...")
  -f BYTECODEFILE, --codefile BYTECODEFILE
                        file containing hex-encoded bytecode string
  -a CONTRACT_ADDRESS, --address CONTRACT_ADDRESS
                        pull contract from the blockchain
  --bin-runtime         Only when -c or -f is used. Consider the input bytecode as binary runtime code, default being the contract creation bytecode.
  -o <text/markdown/json/jsonv2>, --outform <text/markdown/json/jsonv2>
                        report output format

commands:
  -g GRAPH, --graph GRAPH
                        generate a control flow graph
  -j OUTPUT_FILE, --statespace-json OUTPUT_FILE
                        dumps the statespace json

options:
  -m MODULES, --modules MODULES
                        Comma-separated list of security analysis modules
  --max-depth MAX_DEPTH
                        Maximum recursion depth for symbolic execution
  --call-depth-limit CALL_DEPTH_LIMIT
                        Maximum call depth limit for symbolic execution
  --strategy {dfs,bfs,naive-random,weighted-random,pending}
                        Symbolic execution strategy
  --transaction-sequences TRANSACTION_SEQUENCES
                        The possible transaction sequences to be executed. Like [[func_hash1, func_hash2], [func_hash2, func_hash3]] where the first transaction is constrained with func_hash1 and func_hash2, and the second tx is constrained with func_hash2 and func_hash3. Use -1 as a proxy for fallback() function and -2 for receive() function.
  --beam-search BEAM_SEARCH
                        Beam search with with
  -b N, --loop-bound N  Bound loops at n iterations
  -t TRANSACTION_COUNT, --transaction-count TRANSACTION_COUNT
                        Maximum number of transactions issued by laser
  --execution-timeout EXECUTION_TIMEOUT
                        The amount of seconds to spend on symbolic execution
  --solver-timeout SOLVER_TIMEOUT
                        The maximum amount of time(in milli seconds) the solver spends for queries from analysis modules
  --create-timeout CREATE_TIMEOUT
                        The amount of seconds to spend on the initial contract creation
  --parallel-solving    Enable solving z3 queries in parallel
  --solver-log SOLVER_LOG
                        Path to the directory for solver log
  --no-onchain-data     Don't attempt to retrieve contract code, variables and balances from the blockchain
  --pruning-factor PRUNING_FACTOR
                        Checks for reachability at the rate of <pruning-factor> (range 0-1.0). Where 1.0 would mean checking for every execution
  --unconstrained-storage
                        Default storage value is symbolic, turns off the on-chain storage loading
  --phrack              Phrack-style call graph
  --enable-physics      Enable graph physics simulation
  -q, --query-signature
                        Lookup function signatures through www.4byte.directory
  --disable-iprof       Disable the instruction profiler
  --disable-dependency-pruning
                        Deactivate dependency-based pruning
  --disable-coverage-strategy
                        Disable coverage based search strategy
  --disable-mutation-pruner
                        Disable mutation pruner
  --enable-state-merging
                        Enable State Merging
  --enable-summaries    Enable using symbolic summaries
  --custom-modules-directory CUSTOM_MODULES_DIRECTORY
                        Designates a separate directory to search for custom analysis modules
  --attacker-address ATTACKER_ADDRESS
                        Designates a specific attacker address to use during analysis
  --creator-address CREATOR_ADDRESS
                        Designates a specific creator address to use during analysis

```

## Настройка

### Переменные окружения

Создайте файл `.env` в корне проекта со следующими переменными:

```
OPENAI_API_KEY=your_openai_api_key
ETHERSCAN_API_KEY=your_etherscan_api_key
ALCHEMY_API_KEY=your_alchemy_api_key
COINMARKETCAP_API_KEY=your_coinmarketcap_api_key
DEDAUB_API_KEY=your_dedaub_api_key
DEFI_DOG_TELEGRAM_BOT_TOKEN=your_telegram_bot_token
```

### Локальная разработка

1. Клонируйте репозиторий:
   ```
   git clone https://github.com/RustamKuramshin/defi-dog
   cd defi-dog
   ```

2. Запустите базу данных PostgreSQL:
   ```
   docker-compose up -d defi-dog-db
   ```

3. Запустите приложение:
   ```
   ./gradlew bootRun
   ```

4. Доступ к приложению по адресу http://localhost:8080

### Развертывание с Docker

1. Соберите приложение:
   ```
   ./gradlew build
   ```

2. Запустите контейнеры:
   ```
   docker-compose up -d
   ```

3. Доступ к приложению по адресу http://localhost:8082

## Конфигурация

Приложение может быть настроено через следующие файлы:

- `src/main/resources/application.properties`: Основной файл конфигурации
- `src/main/resources/application-prod.properties`: Конфигурация для продакшена

## База данных

Приложение использует PostgreSQL в качестве базы данных. Схема базы данных управляется через Liquibase и определена в:

```
src/main/resources/ru/javaboys/defidog/liquibase/changelog.xml
```

## Тестирование

Запустите тесты с помощью:

```
./gradlew test
```

## Участие в разработке

1. Сделайте форк репозитория
2. Создайте ветку для функции: `git checkout -b feature/your-feature-name`
3. Зафиксируйте изменения: `git commit -am 'Add some feature'`
4. Отправьте в ветку: `git push origin feature/your-feature-name`
5. Отправьте запрос на включение изменений (pull request)

## Контакты

- Рустам Курамшин — https://t.me/KuramshinRustam | 📞 +7 (952) 584-34-99
- Рустам Гулямов — https://t.me/gulyamovrustam | 📞 +7 (912) 321-88-19
- Александр Янчий — https://t.me/AlexYanchiy_ru | 📞 +7 (978) 127-77-34
- Рустам Зулкарниев — https://t.me/WerderR | 📞 +7 (919) 883-84-60
