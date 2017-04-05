package ru.linker.whattodo.Model;

import android.content.Context;
import android.support.test.filters.SmallTest;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by root on 3/25/17.
 */

//@RunWith(AndroidJUnit4.class)
@SmallTest
public class TaskStorageModelTest extends AndroidTestCase {

    private Context insturmentationContext;

    //@Rule
    //public Activity


    @Before
    public void setUp() {
        insturmentationContext = getContext();
    }

    @Test
    public void addTask() throws Exception {

        TaskStorageModel tester = TaskStorageModel.getInstance(insturmentationContext);
        assertNotNull("Tester TaskStorageModel was notCreated", tester);

    }

}