# DeFi Dog - Аудит безопасности смарт-контрактов

## Описание проекта

DeFi Dog — это комплексный инструмент для аудита безопасности смарт-контрактов в сфере DeFi (Децентрализованные Финансы). Проект использует искусственный интеллект и множество источников данных блокчейна для предоставления глубокого анализа и оценки безопасности смарт-контрактов.

## Презентация проекта

[Смотреть презентацию DeFi Dog](./Java_Boys_DeFi_Dog_Аудит_безопасности_смарт_контрактов.pdf)

### Основные функции

- **Анализ смарт-контрактов**: Получение и анализ исходного кода смарт-контрактов и ABI-интерфейса из Etherscan и Sourcify
- **Статический анализ кода**: Аудит смарт-контрактов с помощью лидирующих в индустрии инструментов статического анализа Solidity-кода - Slither и Mythril
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

## Продовое окружение

На сервере проект **не** поднимает свою базу: она берётся из общей
инфраструктуры (`common-infra`), одной на все проекты.

| Где | Чем запускается | Что поднимается |
|---|---|---|
| Машина разработчика | `docker-compose.yml` | приложение, PostgreSQL, панель администрирования |
| Сервер | `docker-compose.prod.yml` | только приложение, подключённое к сети `common-infra` |

Параметры подключения и токены лежат на сервере в `~/defi-dog/.env.prod`
(права `600`) и в репозиторий не попадают:

```properties
PG_DATASOURCE_URL=jdbc:postgresql://common-postgres:5432/defidog
PG_NAME=defidog
PG_PASS=<пароль из add-project.sh>
OPENAI_API_KEY=<ключ или заглушка>
ETHERSCAN_API_KEY=<ключ или заглушка>
ALCHEMY_API_KEY=<ключ или заглушка>
COINMARKETCAP_API_KEY=<ключ или заглушка>
DEDAUB_API_KEY=<ключ или заглушка>
DEFI_DOG_TELEGRAM_BOT_TOKEN=<токен или заглушка>
MAIL_KEY=<пароль SMTP или заглушка>
```

Место в общей инфраструктуре заводится один раз:
`~/common-infra/scripts/add-project.sh defidog --no-bucket`.

С ключами-заглушками приложение работает, но фоновые обращения к внешним
сервисам выключены в `application-prod.properties` (`telegram.bot.enabled`,
`coinmarketcap.updater.enabled`) - иначе журнал забивается отказами авторизации.
Получив настоящие ключи, эти строки нужно убрать.

### Статические анализаторы и Docker

Приложение запускает `slither` и `mythril` отдельными контейнерами, обращаясь к
демону Docker. Адрес демона задаётся свойством `docker.host`, по умолчанию это
сокет `unix:///var/run/docker.sock`, проброшенный в контейнер приложения.
Прежний вариант для прода - `tcp://host.docker.internal:2375` - требовал
открытого на хосте порта Docker без шифрования и проверки клиента, то есть
полного доступа к хосту для всей сети.

Каталог с исходниками монтируется по совпадающему пути (`/srv/defi-dog/sources`
и внутри, и снаружи контейнера): анализатору каталог монтирует демон хоста, и
путь должен существовать с обеих сторон. Проброшенный сокет даёт контейнеру
полный доступ к Docker хоста - это осознанный размен ради работы анализаторов;
если они не нужны, том с сокетом снимается.

### Развёртывание на сервере

```sh
./deploy.sh main
```

Скрипт забирает ветку, собирает `bootJar` под Java 17, пересобирает образы
анализаторов и поднимает приложение продовым compose-файлом. Он же проверяет,
что рядом есть `.env.prod`, поднята сеть `common-infra` и существует каталог
исходников, - иначе останавливается, ничего не трогая. То же самое делает
workflow `CI/CD Pipeline for DeFi-Dog Project` (ручной запуск, параметр - имя
ветки).

Наружу приложение публикуется обратным прокси сервера:
`./add-domain.sh defi-dog.javaboys.ru http://172.17.0.1:8082`.

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
