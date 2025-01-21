# MyBlog Application

## Сборка проекта

### 1. Установка зависимостей

```bash
./gradlew clean build
```

### 2. Сборка WAR-файла

```bash
./gradlew bootWar
```

Готовый WAR-файл будет находиться по пути:

```
build/libs/myblog-0.0.1-SNAPSHOT.war
```

---

## Запуск тестов

Для запуска всех тестов:

```bash
./gradlew test
```

Тесты включают:

* **Unit-тесты** для сервисов и контроллеров
* **Интеграционные тесты** с H2 БД

---

## Деплой в сервлет-контейнер

1. **Соберите WAR-файл**:
   ```bash
   ./gradlew bootWar
   ```
2. **Скопируйте WAR в Tomcat**:
   ```bash
   cp build/libs/myblog-0.0.1-SNAPSHOT.war /path/to/tomcat/webapps/ROOT.war
   ```
3. **Запустите Tomcat**:
   ```bash
   ./catalina.sh run
   ```
4. **Откройте браузер:**
   ```
   http://localhost:8080/
   ```

---

## Docker: Запуск в контейнере

### 1. Собрать и запустить контейнеры

```bash
docker-compose up --build
```

### 2. Доступ к приложению

http://localhost:8080/
