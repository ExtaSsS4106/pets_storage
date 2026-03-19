# Pet Storage API

<div align="center">
  <a href="https://extasss4106.github.io/pets_storage/">
    <img src="https://img.shields.io/badge/📖-API%20Документация-6a0dad?style=for-the-badge&logo=readthedocs&logoColor=white" alt="API Docs">
  </a>
  <br>
  <sub>Нажмите для просмотра документации API</sub>
</div>

---

## 🔗 Прямая ссылка

[https://extasss4106.github.io/pets_storage/](https://extasss4106.github.io/pets_storage/)

---

## 📥 Скачивание и установка

### Предварительные требования
- Python 3.8 или выше
- Git (опционально)
- Виртуальное окружение (рекомендуется)

### Вариант 1: Клонирование через Git

```bash
# Клонировать репозиторий
git clone https://github.com/extasss4106/pets_storage.git

# Перейти в директорию проекта
cd pets_storage

# Создать виртуальное окружение
python -m venv venv

# Активировать виртуальное окружение
# На Windows:
venv\Scripts\activate
# На Linux/Mac:
source venv/bin/activate

# Установить зависимости
pip install -r requirements.txt

# Запустить сервер
python manage.py runserver
```
Вариант 2: Скачивание ZIP-архива

  - Перейдите на страницу репозитория

  - Нажмите кнопку "Code" (зеленая)

  - Выберите "Download ZIP"

  - Распакуйте архив в нужную директорию

  - Откройте терминал в распакованной папке

# Выполните команды:

```bash

# Создать виртуальное окружение
python -m venv venv

# Активировать виртуальное окружение
# На Windows:
venv\Scripts\activate
# На Linux/Mac:
source venv/bin/activate

# Установить зависимости
pip install -r requirements.txt

# Запустить сервер
python manage.py runserver

🚀 Быстрый старт
1. Запуск сервера
bash

python manage.py runserver 0.0.0.0:8000
```
Сервер запустится по адресу: http://127.0.0.1:8000


# Войдите под учетной записью суперпользователя.
📋 Основные возможности

    Учет товаров - добавление, списание, перемещение

    Отслеживание поставок - статус "delivery" для новых поступлений

    История движений - автоматическая запись всех операций

    Группировка товаров - удобное отображение одинаковых позиций

    Фильтрация - просроченные, списанные, в доставке

    Управление сотрудниками - роли и права доступа

🔑 Тестовые данные
Должности сотрудников

    ID 1 - Administrator (полный доступ)

    ID 2 - Warehouse_Operator (ограниченный доступ)

Категории товаров

      ID	Категория
      1	dry (сухой)
      2	wet (жидкий)
      3	treats (лакомства)
      4	hay (сено)
      Типы животных
      ID	Тип
      1	dogs (собаки)
      2	cats (кошки)
      3	rodents (грызуны)
      
Статусы товаров

    exists - активный на складе

    deleted - списанный товар

    delivery - в поставке

📚 Документация API

Подробная документация API доступна по ссылке:

https://extasss4106.github.io/pets_storage/

В документации описаны все эндпоинты, форматы запросов и примеры ответов.
🛠️ Технологии

    Backend: Django, Django REST Framework

    База данных: SQLite (по умолчанию)

    Аутентификация: Token Authentication

    Фронтенд: HTML, CSS, JavaScript

    Документация: GitHub Pages


