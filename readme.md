Loan Telegram Bot

Описание проекта

Loan Telegram Bot — это Telegram-бот для расчёта кредитов, разработанный на Java.

Бот позволяет пользователям рассчитывать графики погашения кредита по двум схемам:

* Аннуитетные платежи
* Дифференцированные платежи

Также реализовано сохранение истории запросов пользователей и подготовлена основа для аналитики менеджеров.

⸻

Функциональные возможности

Пользователь

Поддерживаются следующие команды:

/start

Отображает приветственное сообщение и список доступных команд.

/calculate

Запускает пошаговый диалог:

1. Ввод суммы кредита
2. Ввод срока кредита
3. Ввод процентной ставки
4. Выбор типа платежа

После ввода параметров бот формирует помесячный график платежей.

/history

Показывает историю ранее выполненных расчётов пользователя.

⸻

Менеджер

Подготовлен сервис аналитики:

* Подсчёт общего количества запросов
* Статистика по типам платежей
* Статистика по срокам кредитования
* Фильтрация по сумме кредита

⸻

Используемые технологии

* Java 21
* Maven
* TelegramBots API
* Java Collections Framework
* Git
* GitHub

⸻

Архитектура проекта

Проект построен по принципам разделения ответственности.

Структура проекта

src/main/java/com/example/loanbot
├── Main.java
├── bot
│   ├── LoanTelegramBot.java
│   ├── BotMessageHandler.java
│   ├── UserSession.java
│   ├── UserSessionStorage.java
│   └── UserState.java
├── model
│   ├── LoanRequest.java
│   ├── PaymentScheduleItem.java
│   └── PaymentType.java
├── calculator
│   ├── PaymentCalculator.java
│   ├── AnnuityPaymentCalculator.java
│   ├── DifferentiatedPaymentCalculator.java
│   └── PaymentCalculatorFactory.java
├── repository
│   ├── LoanRequestRepository.java
│   └── InMemoryLoanRequestRepository.java
├── service
│   ├── LoanService.java
│   └── AnalyticsService.java
└── formatter
    ├── PaymentScheduleFormatter.java
    ├── HistoryFormatter.java
    └── AnalyticsFormatter.java

⸻

Архитектурная схема

Telegram
    |
    v
LoanTelegramBot
    |
    v
BotMessageHandler
    |
    v
LoanService
    |
    v
PaymentCalculatorFactory
    |
    +----------------------+
    |                      |
    v                      v
Annuity           Differentiated
Calculator        Calculator
LoanService
    |
    v
LoanRequestRepository

⸻

Использование паттерна Factory

Для создания различных типов калькуляторов используется паттерн Factory.

PaymentCalculator calculator =
        calculatorFactory.create(paymentType);

В зависимости от выбранного пользователем типа платежа создаётся:

* AnnuityPaymentCalculator
* DifferentiatedPaymentCalculator

⸻

Применение принципов SOLID

SRP (Single Responsibility Principle)

Каждый класс отвечает только за одну задачу.

Примеры:

* LoanService — бизнес-логика кредита
* AnalyticsService — аналитика
* HistoryFormatter — форматирование истории
* LoanTelegramBot — взаимодействие с Telegram

OCP (Open Closed Principle)

Для добавления нового типа платежа достаточно создать новый калькулятор и расширить Factory.

LSP (Liskov Substitution Principle)

Все реализации PaymentCalculator взаимозаменяемы.

ISP (Interface Segregation Principle)

Используются небольшие специализированные интерфейсы.

DIP (Dependency Inversion Principle)

Сервисы зависят от абстракций:

LoanRequestRepository
PaymentCalculator

а не от конкретных реализаций.

⸻

Использование принципов разработки

DRY

Повторяющийся код вынесен в отдельные классы и методы.

KISS

Логика приложения максимально проста и понятна.

YAGNI

Реализован только необходимый функционал без преждевременного усложнения архитектуры.

⸻

Сборка проекта

Скомпилировать проект:

mvn clean compile

Создать артефакт:

mvn clean package

⸻

Настройка Telegram Bot

Создать бота через BotFather.

Получить:

* BOT_TOKEN
* BOT_USERNAME

Настроить переменные окружения.

Linux:

export BOT_TOKEN="your_token"
export BOT_USERNAME="your_bot_username"

Windows:

set BOT_TOKEN=your_token
set BOT_USERNAME=your_bot_username

⸻

Запуск проекта

Запуск через Maven:

mvn exec:java -Dexec.mainClass="com.example.loanbot.Main"

После запуска в консоли появится сообщение:

Бот запущен

⸻

Возможные улучшения

В дальнейшем проект может быть расширен:

* PostgreSQL
* JDBC
* Docker
* Docker Compose
* Авторизация менеджеров
* Развёртывание на VPS
* Сохранение истории между перезапусками
* Экспорт графиков в PDF

⸻

Автор

Учебный проект по разработке Telegram-бота на Java.
