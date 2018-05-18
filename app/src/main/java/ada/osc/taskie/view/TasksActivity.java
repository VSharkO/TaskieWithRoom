package ada.osc.taskie.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import ada.osc.taskie.R;
import ada.osc.taskie.database.CategoriesDao;
import ada.osc.taskie.database.TaskDao;
import ada.osc.taskie.database.TaskRoomDatabase;
import ada.osc.taskie.model.Task;
import ada.osc.taskie.model.TaskCategory;
import ada.osc.taskie.model.TaskGenerator;
import ada.osc.taskie.model.TaskPriority;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.getSimpleName();
    private TaskDao mTaskDao;
    private CategoriesDao mCategoriesDao;
    TaskPriority showTasksWithPriority = null;
    TaskAdapter mTaskAdapter;
    @BindView(R.id.my_toolbar)Toolbar myToolbar;
    @BindView(R.id.fab_tasks_addNew)
    FloatingActionButton mNewTask;
    @BindView(R.id.recycler_tasks)
    RecyclerView mTasksRecycler;

    TaskClickListener mListener = new TaskClickListener() {
        @Override
        public void onClick(Task task) {
            toastTask(task);
        }

        @Override
        public void onLongClick(final Task task) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TasksActivity.this);
            builder.setTitle(R.string.doYouWantToDelete)
                    .setItems(R.array.optons_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                mTaskDao.delete(task);
                            }
                            updateTasksDisplay();
                        }
                    }).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        TaskRoomDatabase database = TaskRoomDatabase.getDatabase(this);
        //inserting tasks with task generator
        List<Task> randomTasks;
        randomTasks=TaskGenerator.generate(5);
        mTaskDao = database.taskDao();
        mCategoriesDao = database.categoryDao();
        for (Task task:randomTasks) {
            mTaskDao.insert(task);
        }
        mCategoriesDao.deleteAll(mCategoriesDao.getAllCategories());
        setUpRecyclerView();
        updateTasksDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTasksDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    private void setUpRecyclerView() {
        int orientation = LinearLayoutManager.VERTICAL;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                orientation,
                false
        );

        RecyclerView.ItemDecoration decoration =
                new DividerItemDecoration(this, orientation);

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();

        mTaskAdapter = new TaskAdapter(mListener);
        mTasksRecycler.setLayoutManager(layoutManager);
        mTasksRecycler.addItemDecoration(decoration);
        mTasksRecycler.setItemAnimator(animator);
        mTasksRecycler.setAdapter(mTaskAdapter);
    }

    private void updateTasksDisplay() {
        List<Task> tasks=null;
        if(showTasksWithPriority == null) {
            tasks = mTaskDao.getAllTasks();
        }else if(showTasksWithPriority == TaskPriority.HIGH){
            tasks = mTaskDao.findTasksByPriority(String.valueOf(TaskPriority.HIGH));
        }else if(showTasksWithPriority == TaskPriority.MEDIUM){
            tasks = mTaskDao.findTasksByPriority(String.valueOf(TaskPriority.MEDIUM));
        }else if(showTasksWithPriority == TaskPriority.LOW){
            tasks = mTaskDao.findTasksByPriority(String.valueOf(TaskPriority.LOW));
        }
        mTaskAdapter.updateTasks(tasks);
        for (Task t : tasks) {
            Log.d(TAG, t.getTitle());
        }
    }



    private void toastTask(Task task) {
        Toast.makeText(
                this,
                task.getTitle() + "\n" + task.getDescription() + "\n" + task.getCategory(),
                Toast.LENGTH_SHORT
        ).show();
    }

    @OnClick(R.id.fab_tasks_addNew)
    public void startNewTaskActivity() {
        Intent newTask = new Intent();
        newTask.setClass(this, NewTaskActivity.class);
        startActivity(newTask);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_highPriorty:
                showTasksWithPriority = TaskPriority.HIGH;
                updateTasksDisplay();
                return true;

            case R.id.action_mediumPriority:
                showTasksWithPriority = TaskPriority.MEDIUM;
                updateTasksDisplay();
                return true;

            case R.id.action_lowPriority:
                showTasksWithPriority = TaskPriority.LOW;
                updateTasksDisplay();
                return true;

            case R.id.action_all:
                showTasksWithPriority = null;
                updateTasksDisplay();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
