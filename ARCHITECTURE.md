Architecture

Общая архитектура

Проект построен по многослойной архитектуре с разделением ответственности между компонентами.

Telegram User
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
      +---------------------+
      |                     |
      v                     v
AnnuityCalculator   DifferentiatedCalculator
LoanService
      |
      v
LoanRequestRepository
      |
      v
InMemoryLoanRequestRepository

⸻

Диаграмма классов

+----------------------+
| PaymentCalculator    |
+----------------------+
| calculate()          |
+----------+-----------+
           ^
           |
    +------+------+
    |             |
    |             |
+-----------+  +------------------+
| Annuity   |  | Differentiated   |
| Calculator|  | Calculator       |
+-----------+  +------------------+
           ^
           |
           |
+-------------------------+
| PaymentCalculatorFactory|
+-------------------------+
| create()               |
+-------------------------+
+----------------------+
| LoanService          |
+----------------------+
| calculateSchedule()  |
| getUserHistory()     |
+----------+-----------+
           |
           |
           v
+------------------------+
| LoanRequestRepository  |
+------------------------+
| save()                 |
| findByUserId()         |
| findAll()              |
+-----------+------------+
            ^
            |
            |
+------------------------------+
| InMemoryLoanRequestRepository |
+------------------------------+
+----------------------+
| BotMessageHandler    |
+----------------------+
| handle()             |
+----------+-----------+
           |
           |
           v
+----------------------+
| LoanService          |
+----------------------+

⸻

Компоненты системы

LoanTelegramBot

Отвечает за взаимодействие с Telegram API.

Функции:

* получение сообщений пользователей;
* отправка ответов;
* передача сообщений в обработчик.

⸻

BotMessageHandler

Реализует бизнес-логику пользовательского диалога.

Функции:

* обработка команд;
* управление состоянием пользователя;
* формирование запросов к сервисам.

⸻

UserSession

Хранит данные текущего диалога пользователя:

* сумма кредита;
* срок;
* ставка;
* тип платежа.

⸻

LoanService

Основной сервис приложения.

Функции:

* расчёт графика платежей;
* сохранение истории запросов;
* получение истории пользователя.

⸻

PaymentCalculatorFactory

Создаёт необходимую реализацию калькулятора.

Используется паттерн Factory.

⸻

PaymentCalculator

Интерфейс расчёта платежей.

Реализации:

* AnnuityPaymentCalculator
* DifferentiatedPaymentCalculator

⸻

LoanRequestRepository

Абстракция слоя хранения данных.

Позволяет заменить способ хранения без изменения бизнес-логики.

⸻

AnalyticsService

Сервис аналитики.

Функции:

* статистика по типам платежей;
* статистика по срокам;
* фильтрация заявок по сумме.

⸻

Применение принципов SOLID

SRP

Каждый класс имеет одну ответственность.

Примеры:

* LoanTelegramBot — Telegram API.
* LoanService — расчёты кредита.
* AnalyticsService — аналитика.
* HistoryFormatter — форматирование истории.

⸻

OCP

Для добавления нового типа платежей достаточно:

1. создать новую реализацию PaymentCalculator;
2. добавить её в Factory.

Существующий код не изменяется.

⸻

LSP

Любая реализация PaymentCalculator может использоваться вместо другой.

⸻

ISP

Интерфейсы небольшие и специализированные.

Пример:

PaymentCalculator
LoanRequestRepository

⸻

DIP

Сервисы работают через абстракции.

Пример:

private final LoanRequestRepository repository;

а не через конкретные реализации.

⸻

Используемые паттерны

Factory Method

Используется для выбора алгоритма расчёта кредита.

PaymentCalculatorFactory
        |
        +----> AnnuityPaymentCalculator
        |
        +----> DifferentiatedPaymentCalculator

⸻

Используемые принципы разработки

DRY

Повторяющийся код вынесен в отдельные компоненты.

KISS

Архитектура максимально проста и понятна.

YAGNI

Реализован только необходимый функционал согласно требованиям проекта.
