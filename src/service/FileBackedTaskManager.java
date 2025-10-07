package service;

import task.Epic;
import task.SubTask;
import task.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", exception);
        }
    }

    private String toString(Task task) {
        String type;
        String epicId = "";

        if (task instanceof Epic) {
            type = String.valueOf(TaskType.EPIC);
        } else if (task instanceof SubTask) {
            type = String.valueOf(TaskType.SUBTASK);
            epicId = String.valueOf(((SubTask) task).getEpicId());
        } else {
            type = String.valueOf(TaskType.TASK);
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getTaskId(),
                type,
                task.getTaskName(),
                task.getTaskStatus(),
                task.getTaskDescription(),
                epicId);
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        Enum<TaskType> type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        String epicId = fields.length > 5 ? fields[5] : "";

        if ("TASK".equals(type)) {
            return new Task(id, name, description, status);
        } else if ("EPIC".equals(type)) {
            Epic epic = new Epic(id, name, description, status, new ArrayList<>());
            return epic;
        } else if ("SUBTASK".equals(type)) {
            int parentEpicId = Integer.parseInt(epicId);
            return new SubTask(id, name, description, status, parentEpicId);
        }
        throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            //Пропускаем заголовок и пустые строки
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getTaskId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    manager.subTasks.put(task.getTaskId(), (SubTask) task);
                    // Добавляем ID подзадачи в эпик
                    Epic epic = manager.epics.get(((SubTask) task).getEpicId());
                    if (epic != null) {
                        epic.addSubTaskId(task.getTaskId());
                    }
                } else {
                    manager.tasks.put(task.getTaskId(), task);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", exception);
        }
        return manager;
    }

    //Переопределение методов с сохранением
    //Task
    @Override
    public Task addTask(Task task) {
        Task result = super.addTask(task);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task task) {
        Task result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    //Epic
    @Override
    public Epic addEpic(Epic epic) {
        Epic result = super.addEpic(epic);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    //SubTask
    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask result = super.addSubTask(subTask);
        save();
        return result;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask result = super.updateSubTask(subTask);
        save();
        return result;
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }
}