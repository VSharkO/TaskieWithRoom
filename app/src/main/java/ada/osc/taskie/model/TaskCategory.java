package ada.osc.taskie.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "category_table")
public class TaskCategory implements Serializable{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    public TaskCategory(@NonNull String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString(){
        return category;
    }
}
