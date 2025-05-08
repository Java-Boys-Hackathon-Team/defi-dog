# DeFi Dog - Аудит безопасности смарт-контрактов

DeFi Dog - это комплексный инструмент для аудита безопасности смарт-контрактов в сфере DeFi (Децентрализованные Финансы). Он использует ИИ и множество источников данных блокчейна для предоставления глубокого анализа и оценки безопасности смарт-контрактов.

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
