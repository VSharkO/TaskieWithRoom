package ada.osc.taskie.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import ada.osc.taskie.R;
import ada.osc.taskie.database.CategoriesDao;
import ada.osc.taskie.database.TaskDao;
import ada.osc.taskie.database.TaskRoomDatabase;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskCategory;
import ada.osc.taskie.model.TaskPriority;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTaskActivity extends AppCompatActivity {

	@BindView(R.id.edittext_newtask_title)	EditText mTitleEntry;
	@BindView(R.id.edittext_newtask_description) EditText mDescriptionEntry;
	@BindView(R.id.spinner_newtask_priority) Spinner mPriorityEntry;
	@BindView(R.id.spinner_newtask_category) Spinner mCategoriesEntry;
	@BindView(R.id.imagebutton_newtask_addCategory) ImageButton buttonAddCategory;
	@BindView(R.id.edittext_newtask_newcategory) EditText mNewCategory;
	private TaskDao mTaskDao;
	private CategoriesDao mCategoriesDao;
	private final TaskCategory initialCategory = new TaskCategory("No categories");
	boolean isAddNewCategory;
	SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_task);
		ButterKnife.bind(this);
		initDao();
		setUpTaskSpinnerSource();
		setUpCategorySpinnerSource();
		isAddNewCategory=false;
	}

	private void setUpCategorySpinnerSource() {
		if(mCategoriesDao.getAllCategories().size()==0){
			mCategoriesDao.insert(initialCategory);
		}else{
			mCategoriesDao.delete(initialCategory);
		}
		mCategoriesEntry.setAdapter(
				new ArrayAdapter<>(
						this, android.R.layout.simple_list_item_1, mCategoriesDao.getAllCategories())
		);

	}

	private void setUpTaskSpinnerSource() {
		sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		mPriorityEntry.setAdapter(
				new ArrayAdapter<>(
						this, android.R.layout.simple_list_item_1, TaskPriority.values()
				)
		);

		sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		String savedPriority = sharedPref.getString(getString(R.string.saved_priority_key),getString(R.string.defaultValue));

		switch(savedPriority){
			case "LOW":
				mPriorityEntry.setSelection(0);
				break;
			case "MEDIUM":
				mPriorityEntry.setSelection(1);
				break;
			case "HIGH":
				mPriorityEntry.setSelection(2);
				break;

			default:
				mPriorityEntry.setSelection(0);
		}
	}

	private void initDao() {
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(this);
        mTaskDao = database.taskDao();
        mCategoriesDao = database.categoryDao();
    }

	@OnClick(R.id.imagebutton_newtask_savetask)
	public void saveTask(){
		sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		String title = mTitleEntry.getText().toString();
		String description = mDescriptionEntry.getText().toString();
		TaskCategory category=initialCategory;
		if(isAddNewCategory){
			TaskCategory newCategory = addNewCategoryToDb();
			if(newCategory != null) {
				category = newCategory;
			}
		}else{
			category = (TaskCategory) mCategoriesEntry.getSelectedItem();
		}
		TaskPriority priority = (TaskPriority) mPriorityEntry.getSelectedItem();
		editor.putString(getString(R.string.saved_priority_key),priority.name());
		editor.apply();
		Log.i("priority",sharedPref.getString(getString(R.string.saved_priority_key),getString(R.string.defaultValue)));
		Task newTask = new Task(title, description, priority, category);
		mTaskDao.insert(newTask);
		finish();
	}

	@OnClick(R.id.imagebutton_newtask_addCategory)
	public void addCategory(){

		mCategoriesEntry.setVisibility(View.GONE);
		mNewCategory.setVisibility(View.VISIBLE);
		buttonAddCategory.setVisibility(View.GONE);
		isAddNewCategory=true;
	}

	public TaskCategory addNewCategoryToDb() {
		TaskCategory category=null;
			if (mNewCategory.getText().toString().length() != 0) {
				category = new TaskCategory(mNewCategory.getText().toString());
				mCategoriesDao.insert(category);
			}
		return category;
	}
}
