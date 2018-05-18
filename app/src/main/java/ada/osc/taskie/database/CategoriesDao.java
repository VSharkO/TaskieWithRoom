package ada.osc.taskie.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ada.osc.taskie.model.TaskCategory;

@Dao
public interface CategoriesDao {

    @Insert
    void insert(TaskCategory taskCategory);

    @Delete
    void delete(TaskCategory taskCategory);

    @Delete
    void deleteAll(List<TaskCategory> taskCategory);

    @Query("SELECT * from category_table ORDER BY category ASC")
    List<TaskCategory> getAllCategories();

}
