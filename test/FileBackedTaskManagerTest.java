import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import task.Epic;
import task.Task;
import util.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        //создание файла
        try {
            file = File.createTempFile("tasks", ".csv");
            file.deleteOnExit();
            return new FileBackedTaskManager(file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл", e);
        }
    }

    @Test
    void testSaveAndLoadWithTasks() {
        //сохранение и чтение задач в файле
        Task task = new Task("задача", "задача №1", TaskStatus.NEW);
        taskManager.addTask(task);

        Epic epic = new Epic("основная задача", "основная задача №1");
        taskManager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть загружена задача");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть загружена основная задача");
    }

    @Test
    void testFileCreation() {
        //создание файла
        assertTrue(file.exists(), "Файл должен быть создан");
    }

    @Test
    void testLoadFromNonExistentFile() {
        //загрузка несуществующего файл
        File nonExistentFile = new File("non_existent_file.csv");
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Загрузка из несуществующего файла");
    }
}