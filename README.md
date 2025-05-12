# DeFi Dog - Аудит безопасности смарт-контрактов

## Описание проекта

DeFi Dog — это комплексный инструмент для аудита безопасности смарт-контрактов в сфере DeFi (Децентрализованные Финансы). Проект использует искусственный интеллект и множество источников данных блокчейна для предоставления глубокого анализа и оценки безопасности смарт-контрактов.

### Основные функции

- **Анализ смарт-контрактов**: Получение и анализ исходного кода смарт-контрактов и ABI-интерфейса из Etherscan и Sourcify
- **Анализ на базе ИИ**: Использование моделей GPT от OpenAI для анализа уязвимостей безопасности в смарт-контрактах и ABI
- **Интеграция с блокчейном**: Взаимодействие с данными блокчейна Ethereum через JSON-RPC API 
- **Декомпиляция**: Декомпиляция байткода смарт-контрактов с помощью Dedaub и Panoramix
- **Рыночные данные**: Получение данных о рынке криптовалют от CoinMarketCap API
- **Уведомления**: Отправка уведомлений пользователям через Telegram-бота и Email
- **Ролева модель и безопасность**: Аутентификация пользователей. Роли обычного пользователя и админа системы

## Контакты разработчиков

- Рустам Курамшин — https://t.me/KuramshinRustam | 📞 +7 (952) 584-34-99
- Рустам Гулямов — https://t.me/gulyamovrustam | 📞 +7 (912) 321-88-19
- Александр Янчий — https://t.me/AlexYanchiy_ru | 📞 +7 (978) 127-77-34
- Рустам Зулкарниев — https://t.me/WerderR | 📞 +7 (919) 883-84-60

## Протестировать продукт

[https://defi-dog.javaboys.ru/login](https://defi-dog.javaboys.ru/login)

## Технологический стек

- **Бэкенд**: Java 17, Spring Boot
- **Фронтенд**: Jmix и Vaadin
- **База данных**: PostgreSQL
- **ИИ**: Spring AI с интеграцией OpenAI API
- **Блокчейн**: Web3j
- **Обмен сообщениями**: Telegram Bot API и SMTP
- **Инструмент сборки**: Gradle

## Структура проекта

Основные пакеты проекта:

- `src/main/java/ru/javaboys/defidog/view` - сервисные бины связанные с экранами на UI и вообще любой код для UI-логики
- `src/main/java/ru/javaboys/defidog/event` - модели данных и сущности
- `src/main/java/ru/javaboys/defidog/repositories` - репозитории для работы с базой данных
- `src/main/java/ru/javaboys/defidog/integrations` - сервисные бины и DTO для интеграций с внешними API
- `src/main/java/ru/javaboys/defidog/crypto` - сервисные бины и DTO для работы с криптовалютами
- `src/main/java/ru/javaboys/defidog/asyncjobs` - сервисные бины и DTO задач по расписанию синхронизации исходного кода и аудита смарт-контрактов 
- `src/main/java/ru/javaboys/defidog/security` - компоненты безопасности приложения

## Запуск проекта локально

### Предварительные требования

- Java 17
- IntelliJ IDEA
- Docker и Docker Compose (для контейнеризованного развертывания)
- PostgreSQL (при локальном запуске)
- API-ключи для:
  - OpenAI API
  - Etherscan API
  - Alchemy (провайдер узла Ethereum)
  - CoinMarketCap API
  - Dedaub API
  - Telegram Bot

### Переменные окружения

Создайте файл `.env` в корне проекта на основе `.env.example` со следующими переменными:

```
OPENAI_API_KEY=your_openai_api_key
ETHERSCAN_API_KEY=your_etherscan_api_key
ALCHEMY_API_KEY=your_alchemy_api_key
COINMARKETCAP_API_KEY=your_coinmarketcap_api_key
DEDAUB_API_KEY=your_dedaub_api_key
DEFI_DOG_TELEGRAM_BOT_TOKEN=your_telegram_bot_token
MAIL_KEY=gmail_smtp_login
```

### Локальная разработка

1. Клонируйте репозиторий:
   ```shell
   git clone https://github.com/RustamKuramshin/defi-dog
   cd defi-dog
   ```

2. Запустите базу данных PostgreSQL:
   ```shell
   docker-compose up -d defi-dog-db
   ```

3. Запустите приложение из Run/Debug Configuration и указать в нем путь к .env-файлу:
   ```
   .run/Defi-dog Jmix Application.run.xml
   ```

4. Доступ к приложению по адресу http://localhost:8080/login

### Развертывание с Docker

1. Соберите приложение:
   ```
   ./gradlew -Pvaadin.productionMode=true bootJar -x test
   ```

2. Запустите контейнеры:
   ```
   docker-compose up -d
   ```

3. Доступ к приложению по адресу http://localhost:8082/login

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
