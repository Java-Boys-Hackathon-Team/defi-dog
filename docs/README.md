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
- Mythril:
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
- Slither:
```text
usage: slither target [flag]

target can be:
        - file.sol // a Solidity file
        - project_directory // a project directory. See https://github.com/crytic/crytic-compile/#crytic-compile for the supported platforms
        - 0x.. // a contract on mainnet
        - NETWORK:0x.. // a contract on a different network. Supported networks: mainne,sepoli,holesk,bs,testnet.bs,pol,amoy.pol,polyz,cardona.polyz,bas,sepolia.bas,arb,nova.arb,sepolia.arb,line,sepolia.line,ft,testnet.ft,blas,sepolia.blas,opti,sepolia.opti,ava,testnet.ava,btt,testnet.btt,cel,alfajores.cel,crono,fra,holesky.fra,gn,krom,sepolia.krom,mantl,sepolia.mantl,moonbea,moonrive,moonbas,opbn,testnet.opbn,scrol,sepolia.scrol,taik,hekla.taik,wemi,testnet.wemi,era.zksyn,sepoliaera.zksyn,xa,sepolia.xa,xd,testnet.xd,apechai,curtis.apechai,worl,sepolia.worl,sopho,testnet.sopho,soni,testnet.soni,unichai,sepolia.unichai,abstrac,sepolia.abstrac,berachai

For usage information, see https://github.com/crytic/slither/wiki/Usage

options:
  -h, --help            show this help message and exit
  --version             displays the current version
  --filter-paths FILTER_PATHS
                        Regex filter to exclude detector results matching file
                        path e.g. (mocks/|test/)
  --include-paths INCLUDE_PATHS
                        Regex filter to include detector results matching file
                        path e.g. (src/|contracts/). Opposite of --filter-
                        paths

Compile options:
  --compile-force-framework COMPILE_FORCE_FRAMEWORK
                        Force the compile to a given framework (foundry,buidle
                        r,hardhat,truffle,waffle,solc,embark,dapp,etherlime,et
                        herscan,vyper,brownie,solc-json,standard,archive)
  --compile-libraries COMPILE_LIBRARIES
                        Libraries used for linking. Format: --compile-
                        libraries "(name1, 0x00),(name2, 0x02)"
  --compile-remove-metadata
                        Remove the metadata from the bytecodes
  --compile-custom-build COMPILE_CUSTOM_BUILD
                        Replace platform specific build command
  --ignore-compile      Do not run compile of any platform
  --skip-clean          Do not attempt to clean before compiling with a
                        platform

Solc options:
  --solc SOLC           solc path
  --solc-remaps SOLC_REMAPS
                        Add remapping
  --solc-args SOLC_ARGS
                        Add custom solc arguments. Example: --solc-args "--
                        allow-path /tmp --evm-version byzantium".
  --solc-disable-warnings
                        Disable solc warnings
  --solc-working-dir SOLC_WORKING_DIR
                        Change the default working directory
  --solc-solcs-select SOLC_SOLCS_SELECT
                        Specify different solc version to try (env config).
                        Depends on solc-select
  --solc-solcs-bin SOLC_SOLCS_BIN
                        Specify different solc version to try (path config).
                        Example: --solc-solcs-bin solc-0.4.24,solc-0.5.3
  --solc-standard-json  Compile all specified targets in a single compilation
                        using solc standard json
  --solc-force-legacy-json
                        Force the solc compiler to use the legacy json ast
                        format over the compact json ast format

Truffle options:
  --truffle-ignore-compile
                        Do not run truffle compile
  --truffle-build-directory TRUFFLE_BUILD_DIRECTORY
                        Use an alternative truffle build directory
  --truffle-version TRUFFLE_VERSION
                        Use a local Truffle version (with npx)
  --truffle-overwrite-config
                        Use a simplified version of truffle-config.js for
                        compilation
  --truffle-overwrite-version TRUFFLE_OVERWRITE_VERSION
                        Overwrite solc version in truffle-config.js (only if
                        --truffle-overwrite-config)

Embark options:
  --embark-ignore-compile
                        Do not run embark build
  --embark-overwrite-config
                        Install @trailofbits/embark-contract-export and add it
                        to embark.json

Brownie options:
  --brownie-ignore-compile
                        Do not run brownie compile

Dapp options:
  --dapp-ignore-compile
                        Do not run dapp build

Etherlime options:
  --etherlime-ignore-compile
                        Do not run etherlime compile
  --etherlime-compile-arguments
                        Add arbitrary arguments to etherlime compile (note:
                        [dir] is the the directory provided to crytic-compile)

Etherscan options:
  --etherscan-only-source-code
                        Only compile if the source code is available.
  --etherscan-only-bytecode
                        Only looks for bytecode.
  --etherscan-apikey ETHERSCAN_API_KEY
                        Etherscan API key.
  --avax-apikey AVAX_API_KEY
                        Etherscan API key.
  --etherscan-export-directory ETHERSCAN_EXPORT_DIR
                        Directory in which to save the analyzed contracts.

Waffle options:
  --waffle-ignore-compile
                        Do not run waffle compile
  --waffle-config-file WAFFLE_CONFIG_FILE
                        Provide a waffle config file

NPX options:
  --npx-disable         Do not use npx

Buidler options:
  --buidler-ignore-compile
                        Do not run buidler compile
  --buidler-cache-directory BUIDLER_CACHE_DIRECTORY
                        Use an alternative buidler cache directory (default
                        ./cache)
  --buidler-skip-directory-name-fix
                        Disable directory name fix (see
                        https://github.com/crytic/crytic-compile/issues/116)

Hardhat options:
  --hardhat-ignore-compile
                        Do not run hardhat compile
  --hardhat-cache-directory HARDHAT_CACHE_DIRECTORY
                        Use an alternative hardhat cache directory (default
                        ./cache)
  --hardhat-artifacts-directory HARDHAT_ARTIFACTS_DIRECTORY
                        Use an alternative hardhat artifacts directory
                        (default ./artifacts)

Foundry options:
  --foundry-ignore-compile
                        Do not run foundry compile
  --foundry-out-directory FOUNDRY_OUT_DIRECTORY
                        Use an alternative out directory (default: out)
  --foundry-compile-all
                        Don't skip compiling test and script

Detectors:
  --detect DETECTORS_TO_RUN
                        Comma-separated list of detectors, defaults to all,
                        available detectors: abiencoderv2-array, arbitrary-
                        send-erc20, arbitrary-send-erc20-permit, arbitrary-
                        send-eth, array-by-reference, controlled-array-length,
                        assembly, assert-state-change, backdoor, weak-prng,
                        boolean-cst, boolean-equal, shadowing-builtin, cache-
                        array-length, chainlink-feed-registry, chronicle-
                        unchecked-price, codex, constant-function-asm,
                        constant-function-state, pragma, controlled-
                        delegatecall, costly-loop, constable-states,
                        immutable-states, cyclomatic-complexity, dead-code,
                        delegatecall-loop, deprecated-standards, divide-
                        before-multiply, domain-separator-collision, encode-
                        packed-collision, enum-conversion, external-function,
                        function-init-state, gelato-unprotected-randomness,
                        erc20-interface, erc721-interface, incorrect-exp,
                        incorrect-return, solc-version, incorrect-equality,
                        incorrect-unary, incorrect-using-for, shadowing-local,
                        locked-ether, low-level-calls, mapping-deletion,
                        events-access, events-maths, missing-inheritance,
                        missing-zero-check, incorrect-modifier, msg-value-
                        loop, calls-loop, multiple-constructors, name-reused,
                        naming-convention, optimism-deprecation, out-of-order-
                        retryable, variable-scope, protected-vars, public-
                        mappings-nested, pyth-deprecated-functions, pyth-
                        unchecked-confidence, pyth-unchecked-publishtime,
                        redundant-statements, reentrancy-benign, reentrancy-
                        eth, reentrancy-events, reentrancy-unlimited-gas,
                        reentrancy-no-eth, return-bomb, return-leave, reused-
                        constructor, rtlo, shadowing-abstract, incorrect-
                        shift, shadowing-state, storage-array, suicidal,
                        tautological-compare, timestamp, too-many-digits, tx-
                        origin, tautology, unchecked-lowlevel, unchecked-send,
                        unchecked-transfer, unimplemented-functions,
                        erc20-indexed, uninitialized-fptr-cst, uninitialized-
                        local, uninitialized-state, uninitialized-storage,
                        unprotected-upgrade, unused-return, unused-state, var-
                        read-using-this, void-cst, write-after-write
  --list-detectors      List available detectors
  --exclude DETECTORS_TO_EXCLUDE
                        Comma-separated list of detectors that should be
                        excluded
  --exclude-dependencies
                        Exclude results that are only related to dependencies
  --exclude-optimization
                        Exclude optimization analyses
  --exclude-informational
                        Exclude informational impact analyses
  --exclude-low         Exclude low impact analyses
  --exclude-medium      Exclude medium impact analyses
  --exclude-high        Exclude high impact analyses
  --include-detectors DETECTORS_TO_INCLUDE
                        Comma-separated list of detectors that should be
                        included
  --fail-pedantic       Fail if any findings are detected
  --fail-low            Fail if any low or greater impact findings are
                        detected
  --fail-medium         Fail if any medium or greater impact findings are
                        detected
  --fail-high           Fail if any high impact findings are detected
  --fail-none, --no-fail-pedantic
                        Do not return the number of findings in the exit code
  --show-ignored-findings
                        Show all the findings

Printers:
  --print PRINTERS_TO_RUN
                        Comma-separated list of contract information printers,
                        available printers: cfg, ck, cheatcode, constructor-
                        calls, contract-summary, data-dependency, declaration,
                        dominator, echidna, function-id, function-summary,
                        halstead, loc, martin, modifiers, call-graph, evm,
                        entry-points, human-summary, inheritance, inheritance-
                        graph, slithir, slithir-ssa, not-pausable, vars-and-
                        auth, require, variable-order
  --include-interfaces  Include interfaces from inheritance-graph printer
  --list-printers       List available printers

Checklist (consider using https://github.com/crytic/slither-action):
  --checklist           Generate a markdown page with the detector results
  --checklist-limit CHECKLIST_LIMIT
                        Limit the number of results per detector in the
                        markdown file
  --markdown-root MARKDOWN_ROOT
                        URL for markdown generation

Additional options:
  --json JSON           Export the results as a JSON file ("--json -" to
                        export to stdout)
  --sarif SARIF         Export the results as a SARIF JSON file ("--sarif -"
                        to export to stdout)
  --sarif-input SARIF_INPUT
                        Sarif input (beta)
  --sarif-triage SARIF_TRIAGE
                        Sarif triage (beta)
  --json-types JSON_TYPES
                        Comma-separated list of result types to output to
                        JSON, defaults to detectors,printers. Available types:
                        compilations,console,detectors,printers,list-
                        detectors,list-printers
  --zip ZIP             Export the results as a zipped JSON file
  --zip-type ZIP_TYPE   Zip compression type. One of
                        lzma,stored,deflated,bzip2. Default lzma
  --disable-color       Disable output colorization
  --triage-mode         Run triage mode (save results in triage database)
  --triage-database TRIAGE_DATABASE
                        File path to the triage database (default:
                        slither.db.json)
  --config-file CONFIG_FILE
                        Provide a config file (default: slither.config.json)
  --change-line-prefix CHANGE_LINE_PREFIX
                        Change the line prefix (default #) for the displayed
                        source codes (i.e. file.sol#1).
  --solc-ast            Provide the contract as a json AST
  --generate-patches    Generate patches (json output only)
  --no-fail             Do not fail in case of parsing (echidna mode only)

Codex (https://beta.openai.com/docs/guides/code):
  --codex               Enable codex (require an OpenAI API Key)
  --codex-log           Log codex queries (in crytic_export/codex/)
  --codex-contracts CODEX_CONTRACTS
                        Comma separated list of contracts to submit to OpenAI
                        Codex
  --codex-model CODEX_MODEL
                        Name of the Codex model to use (affects pricing).
                        Defaults to 'text-davinci-003'
  --codex-temperature CODEX_TEMPERATURE
                        Temperature to use with Codex. Lower number indicates
                        a more precise answer while higher numbers return more
                        creative answers. Defaults to 0
  --codex-max-tokens CODEX_MAX_TOKENS
                        Maximum amount of tokens to use on the response. This
                        number plus the size of the prompt can be no larger
                        than the limit (4097 for text-davinci-003)
  --codex-organization CODEX_ORGANIZATION
                        Codex organization
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
