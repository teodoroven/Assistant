package com.assistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Dictionary
import kotlin.concurrent.thread

fun input(vararg strings: String): String {
    /*
    Запрос ввода строки от пользователя с выводом вопроса

    ### Examples:
    ```
    input()
    input(">>>")
    input("Введите ответ в следующей строке\n")
    ```

    ### Usage:
    ```
    input().toString()
    ```

    ### Parameters:
    @strings: Строки через запятую будут выведены через пробел без сноса строки в конце

    ### Returns:
    :return: Строку, введённую пользователем
     */

    print(strings.joinToString(" "))
    return readLine().toString()
}

fun tryInt(string: String, default: Int = 0): Int {
    /*
    Приведение строки к целому числу, либо возврат значения по умолчанию

    ### Examples:
    ```
    tryInt("1") // 1
    tryInt("a") // 0
    tryInt("b",default=-1) // -1
    ```

    ### Usage:
    ```
    val id = tryInt(input().toString(),default=-1)
    ```

    ### Parameters:
    @string: Строка приводимая к целому числу
    @default: Число по умолчанию будет возвращено, если строку не удалось привести к целому числу

    ### Returns:
    :return: Число из строки, либо число по умолчанию
     */

    return try {
        string.toInt()
    } catch (e: NumberFormatException) {
        default
    }
}

class Condition(
    /*
    Условие для проверки перед выполнением сценария
    - Если список conditions не пуст, то выполняется рекурсивная проверка входящих в него условий
    - Если список пуст, то проверяется наличие word

    ### Examples:
    ```
    val word1 = Condition(word = "ЗАПУС")
    val word2 = Condition(word = "СЦЕН")
    val condition = Condition(count = 2, conditions = listOf(word1, word2))
    ```

    ### Usage:
    ```
    // Предварительное создание условия
    val word1 = Condition(word = "ЗАПУС")
    val word2 = Condition(word = "СЦЕН")
    val condition = Condition(count = 2, conditions = listOf(word1, word2))

    // Проверка выполнения условия
    val userCommand = "запусти сценарий".uppercase()
    val fulfilled = condition.check(userCommand.split(" "))
    ```
     */

    private val count: Int = 1, // Минимальное количество условий, необходимое для выполнения сценария
    private val conditions: List<Condition> = emptyList(), // Список условий может состоять из слов или подусловий
    word: String = "", // Конечное условие - слово, которое должно встречаться в запросе. Проверяется если conditions пуст
 )
{
    private val word = word.uppercase();

    fun check(words: List<String>): Boolean {
        /*
        Проверка условий перед запуском
        - Если список conditions пуст, то проверяется наличие word в списке words
        - Если список не пуст, то выполняется рекурсивная проверка входящих в него условий

        ### Examples:
        ```
        check("запусти сценарий".uppercase().split(" "))
        check(listOf(["ЗАПУС", "СЦЕН")
        ```

        ### Usage:
        ```
        val words = readLine().uppercase().split(" ")

        for (script in scripts) {
            if (script.check(words)) return script.start()
        }
        ```

        ### Parameters:
        @words: Список слов из пользовательской команды В ВЕРХНЕМ РЕГИСТРЕ

        ### Returns:
        :return: Возвращает true если количество выполненных условий больше либо равно минимально необходимому
         */

        // Если нет условий, то проверяется слово
        if (conditions.isEmpty()) {
            for (w in words) {
                if (w.uppercase().startsWith(prefix=word)) return true;
            }
            return false
        }

        // Количество выполненных условий
        var fulfilled: Int = 0;

        // Если условия есть, то выполняется их рекурсивная проверка
        for (condition in conditions) {
            if (condition.check(words)) fulfilled++;
        }

        return fulfilled >= count;
    }
}

open class Script(
    /*
    Сценарий для выполнения, если выполняются условия
    - Хранит условия, которые могут быть проверены методом check
    - Хранит метод для выполнения

    ### Examples:
    ```
    Script(conditions = listOf(
            Condition(1, listOf(Condition(word = "EXIT"), Condition(word = "ВЫХОД")))
        ),
        function = {
            stop()
        }
    )
    ```

    ### Usage:
    ```
    class MyScript(
        conditions: List<Condition>, // Условия выполнения
        function: () -> Unit // Метод выполнения
    ):Script(conditions,function) {
        override fun start() {
            function()
        }
    }
    ```
     */

    private val conditions: List<Condition>, // Условия выполнения
    val function: () -> Unit // Метод выполнения
) {
    fun check(words: List<String>): Boolean {
        /*
        Проверяет выполняется ли хотя бы одно условие

        ### Examples:
        ```
        check("запусти сценарий".uppercase().split(" "))
        check(listOf(["ЗАПУС", "СЦЕН")
        ```

        ### Usage:
        ```
        val words = readLine().uppercase().split(" ")

        for (script in scripts) {
            if (script.check(words)) return script.start()
        }
        ```

        ### Parameters:
        @words: Список слов из пользовательской команды В ВЕРХНЕМ РЕГИСТРЕ

        ### Raises:
        :raises TypeError: if obj is not iterable

        ### Returns:
        :return: Возвращает true если количество выполненных условий больше либо равно минимально необходимому
         */

        for (condition in conditions)
        {
            if (condition.check(words)) return true
        }

        return false
    }

    // Метод для вывода на экран
    open fun whenStarts() {
        println("Пользовательский сценарий запущен")
    }

    // Метод для долгой обработки
    open fun start() {
        whenStarts()
        Thread.sleep(2000) // Имитируем длительное выполнение сценария
        function()
    }
}

class Command(
    /*
    Команда клиента для выполнения, если выполняются условия
    - Хранит условия, которые могут быть проверены методом check
    - Хранит метод для выполнения

    ### Examples:
    ```
    Command(conditions = listOf(
            Condition(1, listOf(Condition(word = "EXIT"), Condition(word = "ВЫХОД")))
        ),
        function = {
            stop()
        }
    )
    ```

    ### Usage:
    ```
    val commands = listof(
        Command(conditions = listOf(
                Condition(1, listOf(Condition(word = "EXIT"), Condition(word = "ВЫХОД")))
            ),
            function = {
                stop()
            }
        )
    )

    val words = readLine().uppercase().split(" ")

    for (command in commands) {
        if (command.check(words)) return command.start();
    }
    ```
     */

    conditions: List<Condition>, // Условия выполнения
    function: () -> Unit // Метод выполнения
):Script(conditions,function) {
    override fun start() {
        /*
        Выполнение переданного метода в основном потоке

        ### Examples:
        ```
        start()
        ```

        ### Usage:
        ```
        val words =  readLine().uppercase().split(" ")

        for (command in commands) {
            if (command.check(words)) return command.start()
        }
        ```
         */

        function()
    }
}

class Routine(
    /*
    Пользовательский сценарий
    - Имеет уникальный идентификатор
    - Имеет пользовательское название
    - Хранит условия, которые могут быть проверены методом check
    - Хранит метод для выполнения

    ### Examples:
    ```
    Routine(getRoutineId(),"Мой сценарий",listOf(
            Condition(2, mutableListOf(
                    Condition(word="ВКЛЮЧ"),
                    Condition(word="ФОНАР")
                )
            )
        ),{
            println("Фонарик включен")
        }
    )
    ```

    ### Usage:
    ```
    val routines = listOf(
        Routine(getRoutineId(),"Мой сценарий",listOf(
                Condition(2, mutableListOf(
                        Condition(word="ВКЛЮЧ"),
                        Condition(word="ФОНАР")
                    )
                )
            ),{
                println("Фонарик включен")
            }
        )
    )

    val words = readLine().uppercase().split(" ")

    for (routine in routines) {
        if (routine.check(words)) return routine.start()
    }
    ```

    Примечание: метод getRoutineId() содержится в Assistant
     */

    val id: Int, // Уникальный внутренний идентификатор
    val name: String, // Пользовательское название
    conditions: List<Condition>, // Условия выполнения
    function: () -> Unit // Метод выполнения
):Script(conditions,function) {
    private var status:String = "created" // "waiting" "working" "paused" "stopped" "finished"

    fun setStatus(status: String) {
        /*
        Установка статуса пользовательскому сценарию
        - created: создан и ещё не был запущен
        - waiting: запущен и ждёт очереди на выполнение
        - paused: был приостановлен пользователем и может быть возобновлён
        - stopped: не может быть возобновлён в результате остановки или ошибки при выполнении
        - finished: успешно выполнен

        ### Examples:
        ```
        setStatus("waiting")
        setStatus("working")
        setStatus("paused")
        setStatus("stopped")
        setStatus("finished")
        ```

        ### Usage:
        ```
        thread {
            setStatus("waiting")
            Thread.sleep(2000)
            setStatus("working")
            function()
            setStatus("finished")
        }

        ### Parameters:
        @status: "waiting" или "working" или "paused" или "stopped" или "finished"
        ```
         */

        if (status in listOf("waiting", "working", "paused", "stopped", "finished")) {
            this.status = status
        } else {
            // TODO: raise TypeError
        }
    }

    fun getStatus() {
        /*
        Возвращает статус пользовательского сценария
        - created: создан и ещё не был запущен
        - waiting: запущен и ждёт очереди на выполнение
        - paused: был приостановлен пользователем и может быть возобновлён
        - stopped: не может быть возобновлён в результате остановки или ошибки при выполнении
        - finished: успешно выполнен

        ### Examples:
        ```
        getStatus()
        ```

        ### Usage:
        ```
        thread {
            setStatus("waiting")
            Thread.sleep(2000)
            setStatus("working")
            function()
            setStatus("finished")
        }

        while (getStatus() in listOf("waiting","working)) {
            print("Выполнение...",end="\r")
        }

        ### Returns:
        :return: Один из 6 статусов пользовательского сценария
        ```
         */

        if (status in listOf("waiting", "working", "paused", "stopped", "finished")) {
            this.status = status
        } else {
            // TODO: raise TypeError
        }
    }

   override fun toString(): String {
        return "Сценарий#${id} name=${name} status=${status}"
    }

    override fun whenStarts() {
        println("Пользовательский сценарий ${name} запущен")
    }

    override fun start() {
        /*
        Выполнение переданного метода в параллельном потоке
        - Выводит сообщение о начале выполнения
        - Можно отслеживать выполнение через поле status

        ### Examples:
        ```
        start()
        ```

        ### Usage:
        ```
        val words =  readLine().uppercase().split(" ")

        for (routine in routines) {
            if (routine.check(words)) return routine.start()
        }
        ```
         */

        whenStarts()

        // Выполняется параллельно
        thread {
            setStatus("waiting")
            Thread.sleep(2000) // Имитируем длительное выполнение сценария
            setStatus("working")
            function()
            setStatus("finished")
        }
    }
}

class Assistant(
    /*
    Клиент выполняет команды пользователя

    ### Examples:
    ```
    Assistant()
    ```

    ### Usage:
    ```
    val client = Assistant();

    while (client.checkWorking()) {
        client.processCommand(input(">>>").toString())
    }
    ```
     */

    private val commands: MutableList<Command> = mutableListOf(), // Обрабатываемые команды
    private val routines: MutableList<Routine> = mutableListOf() // Пользовательские сценарии
) {
    private var working: Boolean = true // Статус отслеживается методом checkWorking и меняется методом stop
    private var routineId: Int = 0 // Уникальный идентификатор для пользовательских сценариев возвращается методом getRoutineId

    // Добавление обрабатываемых команд, они предопределены и не изменяются
    init {
        // Завершение работы клиента
        commands.add(
            Command(conditions = listOf(
                    Condition(1, listOf(Condition(word = "EXIT"), Condition(word = "ВЫХОД")))
                ),
                function = {
                    stop()
                }
            )
        )

        // Создание пользовательского сценария
        commands.add(
            Command(conditions = listOf(
                    Condition(2, listOf(Condition(word = "СОЗД"), Condition(word = "СЦЕНАР")))
                ),
                function = {
                    createRoutine()
                }
            )
        )

        // Запуск пользовательского сценария
        commands.add(
            Command(conditions = listOf(
                Condition(2, listOf(Condition(word = "ЗАПУС"), Condition(word = "СЦЕНАР")))
            ),
                function = {
                    startRoutine()
                }
            )
        )

        // Вывод списка пользовательских сценариев
        commands.add(
            Command(conditions = listOf(
                Condition(2, listOf(Condition(word = "СПИС"), Condition(word = "СЦЕНАР")))
            ),
                function = {
                    printRoutines()
                }
            )
        )

        // Удаление пользовательского сценария с выводом списка и запросом идентификатора
        commands.add(
            Command(conditions = listOf(
                Condition(2, listOf(Condition(word = "УДАЛ"), Condition(word = "СЦЕНАР")))
            ),
                function = {
                    removeRoutine()
                }
            )
        )
    }

    fun checkWorking(): Boolean {
        /*
        Проверка статуса клиента
        - true работает (по умолчанию)
        - false остановлен методом stop

        ### Examples:
        ```
        checkWorking()
        ```

        ### Usage:
        ```
        while (Assistant().checkWorking()) {
            Thread.Sleep(1)
        }

        ### Returns:
        :return: true если клиент работает, false если остановлен методом stop
        ```
         */

        return working
    }

    fun stop() {
        /*
        Завершает работу клиента
        - Останавливаются все циклы и потоки
         */

        working = false
    }

    fun getRoutineId(): Int {
        /*
        Возвращает уникальный идентификатор для пользовательского сценария

        ### Returns:
        :return: При каждом вызове идентификатор будет на единицу больше
        ```
         */

        return routineId++
    }

    fun createRoutine(): Routine? {
        /*
        Создание пользовательского сценария в диалоговом режиме
        - Запрос названия
        - Условия устанавливаются автоматически
        - Выбор действий для выполнения

        WARNING: Запросы занимают основной поток
        TODO: Ввод условий через интерфейс приложения

        ### Returns:
        :return: Созданный сценарий или null, если не было указано одно из полей сценария
        ```
         */

        // Запрос названия сценария
        fun askName(): String {
            println("Введите название сценария")
            return input(">>>").toString()
        }

        // Условия выполнения временно устанавливаются автоматически
        fun askConditions(): List<Condition> {
            println("Условия сценария установлены автоматически")
            return listOf(
                Condition(2, mutableListOf(
                        Condition(word="ВКЛЮЧ"),
                        Condition(word="ФОНАР")
                    )
                )
            )
        }

        // Возможные действия TODO: расширить список вариантов
        val actions:Map<String, () -> Unit> = mutableMapOf(
            "Включить фонарик" to {
                println("Фонарик включен")
            }
        )

        // Выбор действия
        fun askFunction(): () -> Unit {
            println("Выберите действие")

            var i:Int = 0;
            for (action in actions.keys) {
                i++;
                println("${i}) ${action}")
            }

            var choice: Int
            do {
                choice = input(">>>").toInt()
            } while (choice < 1 || choice > actions.size)

            val chosenAction = actions.values.elementAt(choice - 1)
            return chosenAction
        }

        // Создание пользовательского сценария в диалоговом режиме
        println("Отлично, давайте создадим сценарий!")
        val id = getRoutineId()
        val name = askName()

        if (name.isNotEmpty()) {
            val conditions = askConditions()

            if (conditions.isNotEmpty()) {
                val function = askFunction()
                val routine = Routine(id,name,conditions,function)
                routines.add(routine)
                println("Сценарий создан")
                return routine
            }
        }

        return null
    }

    // Вывод элементов коллекции
    fun printRoutines() {
        for (routine in routines) {
            println(routine.toString())
        }
    }

    // Сортировка элементов коллекции
    fun sortRoutines() {
        routines.sortBy { it.id }
    }

    // Фильтрация элементов коллекции
    fun filterRoutines(id: Int): List<Routine> {
        return routines.filter { it.id == id }
    }

    fun askRoutine(required:Boolean = true): Routine? {
        /*
        Выбор пользовательского сценария
        - Вывод отсортированного списка сценариев
        - Запрос идентификатора
        - Поиск по идентификатору

        WARNING: Если сценарий не найден или идентификатор не указан пользователем, возвращает null
        WARNING: Перед указанием required=true проверьте наличие сценариев! Иначе будет бесконечный цикл

        ### Examples:
        ```
        askRoutine()
        ```

        ### Usage:
        ```
        val routine = askRoutine()
        if (routine != null) {
            routines.removeIf { it == routine }
        }
        ```

        ### Parameters:
        @required: Если true, то метод обязательно вернёт объект Routine и точно не вернёт null, пользователя не выпустят, пока он не выберет сценарий, даже если сценариев нет

        ### Returns:
        :return: Выбранный пользователем сценарий или null
         */

        sortRoutines()

        println("Выберите сценарий")
        printRoutines()

        while (required) {
            val answer = input().toString()
            val id = tryInt(answer,default=-1)
            val filtered = filterRoutines(id)

            if (filtered.isNotEmpty()) return filtered[0]
        }

        return null
    }

    fun removeRoutine() {
        /*
        Удаление пользовательского сценария
        - Запрос идентификатора
        - Поиск по идентификатору
        - Если сценарий не найден или идентификатор не указан пользователем, сценарий удалён не будет

        ### Examples:
        ```
        askRoutine()
        ```

        ### Usage:
        ```
        val routine = askRoutine()
        if (routine != null) {
            routines.removeIf { it == routine }
        }
        ```
         */

        val routine = askRoutine()
        if (routine != null) {
            routines.removeIf { it == routine }
        }
    }

    fun startRoutine() {
        /*
        Запускает выполнение пользовательского сценария
        - Сценарий выбирается пользователем по ключевым словам
        - Ключевые слова указываются в conditions при создании пользовательского сценария
        - Пользовательский сценарий выполняется в параллельном потоке
        - Выполнение можно отслеживать при помощи метода Routine.getStatus()
        ```
         */

        fun askRoutine(): String {
            return input("Какой сценарий запустить?\n")
        }

        val command:String = askRoutine();
        val words = command.uppercase().split(" ")

        for (routine in routines) {
            if (routine.check(words)) return routine.start()
        }
    }

    fun processCommand(command: String): Any? {
        /*
        Выполняет команду пользователя
        - Если команда не указана или не найдена, то ничего не произойдёт

        ### Examples:
        ```
        processCommand("exit")
        processCommand("Запусти сценарий")
        ```

        ### Usage:
        ```
        val client = Assistant();

        while (client.checkWorking()) {
            client.processCommand(input(">>>").toString())
        }
        ```

        ### Parameters:
        @command: Пользовательская команда

        ### Returns:
        :return: Возвращает результат выполнения команды Command.start()
         */

        // Парсим пользовательскую команду
        val words = command.uppercase().split(" ")

        // Ищем и выполняем команду
        for (routine in commands) {
            if (routine.check(words)) return routine.start();
        }

        return null
    }
}

fun main()
{
    val client = Assistant();

    println("""Список команд:
        | Создать сценарий
        | Запустить сценарий
        | Удалить сценарий
        | Список сценариев
        | exit
    """.trimMargin())

    while (client.checkWorking()) {
        client.processCommand(input(">>>").toString())
    }
}