package ada.osc.taskie.util;

import android.arch.persistence.room.TypeConverter;

import ada.osc.taskie.model.TaskCategory;
import ada.osc.taskie.model.TaskPriority;

public class TypeConverterUtil {
    @TypeConverter
    public static TaskPriority fromString(String string) {
        return TaskPriority.valueOf(string);
    }

    @TypeConverter
    public static String fromTaskPriority(TaskPriority taskPriority) {
        return taskPriority.toString();
    }

    @TypeConverter
    public static TaskCategory fromStringToCategory(String string) {
        return new TaskCategory(string);
    }

    @TypeConverter
    public static String fromTaskCategory(TaskCategory taskCategory) {
        return taskCategory.toString();
    }
}
